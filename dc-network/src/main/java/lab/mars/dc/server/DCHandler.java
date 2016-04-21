package lab.mars.dc.server;

import io.netty.channel.Channel;
import lab.mars.dc.*;
import lab.mars.dc.exception.DCException;
import lab.mars.dc.loadbalance.LoadBalanceService;
import lab.mars.dc.network.TcpClient;
import lab.mars.dc.reflection.ResourceReflection;
import lab.mars.server.DCProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author:yaoalong.
 * Date:2016/4/7.
 * Email:yaoalong@foxmail.com
 */

/**
 * DC处理器，进行线程管理
 */
public class DCHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DCHandler.class);
    private final ConcurrentHashMap<String, TcpClient> ipAndTcpClient = new ConcurrentHashMap<>();
    private  String self;
    private  LoadBalanceService loadBalanceService;
    private final LinkedList<DCPacket> pendingQueue = new LinkedList<>();
    private DCProcessor dcProcessor;
    public DCHandler(DCProcessor dcProcessor){
        this.dcProcessor=dcProcessor;
    }
    public DCHandler(DCProcessor dcProcessor, String self, LoadBalanceService loadBalanceService) {
        this.dcProcessor = dcProcessor;
        this.self = self;
        this.loadBalanceService = loadBalanceService;
    }

    public void receiveMessage(DCPacket dcPacket) {
        DCPacket result = dcProcessor.receiveMessage(dcPacket, null);
        dcPacket.setResponsePacket(result.getResponsePacket());
        ResponsePacket responsePacket = dcPacket.getResponsePacket();
        AsyncCallback asyncCallback = dcPacket.getRequestPacket().getAsyncCallback();
        if (asyncCallback != null) {
            if (dcPacket.getRequestPacket().getOperateType() == OperateType.CREATE || dcPacket.getRequestPacket().getOperateType() == OperateType.DELETE || dcPacket.getRequestPacket().getOperateType() == OperateType.UPDATE) {
                AsyncCallback.VoidCallback voidCallback = (AsyncCallback.VoidCallback) asyncCallback;
                voidCallback.processResult(responsePacket.getCode(), dcPacket.getRequestPacket().getId());
            } else if (dcPacket.getRequestPacket().getOperateType() == OperateType.RETRIEVE) {
                AsyncCallback.DataCallback dataCallback = (AsyncCallback.DataCallback) asyncCallback;
                if (dcPacket.getResponsePacket().getCode() == DCException.Code.OK) {

                    ResourceService resourceService = null;
                    if (responsePacket.getResourceService() != null) {
                        resourceService = (ResourceService) ResourceReflection.deserializeKryo(responsePacket.getResourceService());
                    }

                    dataCallback.processResult(responsePacket.getCode(), dcPacket.getRequestPacket().getId(), resourceService);
                } else {
                    dataCallback.processResult(responsePacket.getCode(), dcPacket.getRequestPacket().getId(), null);
                }
            } else if (dcPacket.getRequestPacket().getOperateType() == OperateType.SERVICE) {
                AsyncCallback.ServiceCallback serviceCallback = (AsyncCallback.ServiceCallback) dcPacket.getRequestPacket().getAsyncCallback();
                if (dcPacket.getResponsePacket() == null) {
                    ResponsePacket responsePacket1 = new ResponsePacket();
                    responsePacket1.setCode(DCException.Code.SYSTEM_ERROR);
                    serviceCallback.processResult(responsePacket1.getCode(), null, null);
                } else {
                    ResultDO resultDO = (ResultDO) ResourceReflection.deserializeKryo(responsePacket.getResult());
                    serviceCallback.processResult(responsePacket.getCode(), dcPacket.getRequestPacket().getId(), resultDO);
                }
            }
        }

    }
    //TODO 进行异步接口调用


    public void receiveMessage(DCPacket dcPacket, Channel channel) {
        try {
            if (preProcessPacket(dcPacket, channel)) {
                dcProcessor.receiveMessage(dcPacket, channel);
            }
        } catch (DCException e) {
            DCException.Code code = e.getCode();
            ResponsePacket responsePacket = new ResponsePacket();
            responsePacket.setCode(code);
            DCPacket result = new DCPacket();
            result.setResponsePacket(responsePacket);
            channel.writeAndFlush(result);
        } catch (Exception e) {
            DCException.Code code = DCException.Code.SYSTEM_ERROR;
            ResponsePacket responsePacket = new ResponsePacket();
            responsePacket.setCode(code);
            DCPacket result = new DCPacket();
            result.setResponsePacket(responsePacket);
            channel.writeAndFlush(result);
        }

    }

    public void close() {
        dcProcessor.close();
    }

    /**
     * 数据包的处理
     *
     * @param dcPacket
     * @param channel
     * @return
     */
    public boolean preProcessPacket(DCPacket dcPacket,
                                    Channel channel) throws Exception {
        if (dcPacket == null || dcPacket.getRequestPacket() == null || StringUtils.isBlank(dcPacket.getRequestPacket().getId())) {
            throw new DCException(DCException.Code.PARAM_ERROR);
        }
        String key = dcPacket.getRequestPacket().getId();
        String server = loadBalanceService.getServer(key);
        if (server.equals(self)) {
            return true;
        }
        for (int i = 0; i < 5; i++) {
            if (ipAndTcpClient.containsKey(server)) {
                try {
                    ipAndTcpClient.get(server).write(dcPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                    ipAndTcpClient.remove(server);
                    continue;
                }
                channel.writeAndFlush(dcPacket);
                return false;
            } else {
                try {
                    TcpClient tcpClient = new TcpClient(pendingQueue);
                    String[] splitStrings = spilitString(server);
                    tcpClient.connectionOne(splitStrings[0], Integer.parseInt(splitStrings[1]));
                    tcpClient.write(dcPacket);
                    channel.writeAndFlush(dcPacket);
                    ipAndTcpClient.put(server, tcpClient);
                    return false;
                } catch (Exception e) {
                    LOG.error("process packet error:", e);
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            server = loadBalanceService.getServer(key);
            System.out.println("server" + server);
        }
        throw new Exception("系统错误");
    }

    /*
     * 将server拆分为ip以及port
     */
    private String[] spilitString(String ip) {
        String[] splitMessage = ip.split(":");
        return splitMessage;
    }

    public void readResponse(DCPacket dcPacket) {
        synchronized (pendingQueue) {
            DCPacket dcPacket1 = pendingQueue.remove();
            ResponsePacket responsePacket = dcPacket.getResponsePacket();
            AsyncCallback asyncCallback = dcPacket1.getRequestPacket().getAsyncCallback();
            if (asyncCallback != null) {
                if (dcPacket1.getRequestPacket().getOperateType() == OperateType.CREATE || dcPacket1.getRequestPacket().getOperateType() == OperateType.DELETE || dcPacket1.getRequestPacket().getOperateType() == OperateType.UPDATE) {
                    AsyncCallback.VoidCallback voidCallback = (AsyncCallback.VoidCallback) asyncCallback;
                    voidCallback.processResult(responsePacket.getCode(), dcPacket1.getRequestPacket().getId());
                } else if (dcPacket1.getRequestPacket().getOperateType() == OperateType.RETRIEVE) {
                    AsyncCallback.DataCallback dataCallback = (AsyncCallback.DataCallback) asyncCallback;
                    if (dcPacket.getResponsePacket().getCode() == DCException.Code.OK) {

                        ResourceService resourceService = null;
                        if (responsePacket.getResourceService() != null) {
                            resourceService = (ResourceService) ResourceReflection.deserializeKryo(responsePacket.getResourceService());
                        }

                        dataCallback.processResult(responsePacket.getCode(), dcPacket1.getRequestPacket().getId(), resourceService);
                    } else {
                        dataCallback.processResult(responsePacket.getCode(), dcPacket1.getRequestPacket().getId(), null);
                    }
                } else if (dcPacket1.getRequestPacket().getOperateType() == OperateType.SERVICE) {
                    AsyncCallback.ServiceCallback serviceCallback = (AsyncCallback.ServiceCallback) dcPacket1.getRequestPacket().getAsyncCallback();
                    if (dcPacket1.getResponsePacket() == null) {
                        ResponsePacket responsePacket1 = new ResponsePacket();
                        responsePacket1.setCode(DCException.Code.SYSTEM_ERROR);
                        serviceCallback.processResult(responsePacket1.getCode(), null, null);
                    } else {
                        ResultDO resultDO = (ResultDO) ResourceReflection.deserializeKryo(responsePacket.getResult());
                        serviceCallback.processResult(responsePacket.getCode(), dcPacket1.getRequestPacket().getId(), resultDO);
                    }
                }
            }

        }
    }
}
