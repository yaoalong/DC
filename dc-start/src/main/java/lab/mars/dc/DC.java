package lab.mars.dc;

import lab.mars.dc.collaboration.RegisterAndMonitorService;
import lab.mars.dc.collaboration.ZKRegisterAndMonitorService;
import lab.mars.dc.exception.DCException;
import lab.mars.dc.loadbalance.LoadBalanceConsistentHash;
import lab.mars.dc.network.TcpServer;
import lab.mars.dc.persistence.DCDatabaseImpl;
import lab.mars.dc.server.DCHandler;
import lab.mars.dc.server.DCProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static lab.mars.dc.OperateType.*;
import static lab.mars.dc.exception.DCException.Code.OPERATE_TYPE_NOT_SUPPORT;
import static lab.mars.dc.exception.DCException.Code.PARAM_ERROR;

/**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */
public class DC {


    private static final Logger LOG = LoggerFactory.getLogger(DC.class);
    private TcpServer tcpServer;
    private DCHandler dcHandler;

    private volatile boolean isStart = false;
    private RegisterAndMonitorService registerAndMonitorService;

    /**
     * 校验客户端参数
     *
     * @param requestPacket
     * @throws DCException
     */
    private static void checkRequest(RequestPacket requestPacket) throws DCException {
        if (requestPacket == null || requestPacket.getOperateType() == null || StringUtils.isBlank(requestPacket.getId())) {
            throw new DCException(PARAM_ERROR);
        }
        OperateType operateType = requestPacket.getOperateType();
        if (operateType == CREATE || operateType == DELETE || operateType == UPDATE) {
            if (requestPacket.getAsyncCallback() != null && !(requestPacket.getAsyncCallback() instanceof AsyncCallback.VoidCallback)) {
                throw new DCException(PARAM_ERROR);
            }
        } else if (operateType == RETRIEVE) {
            if (requestPacket.getAsyncCallback() != null && !(requestPacket.getAsyncCallback() instanceof AsyncCallback.DataCallback)) {
                throw new DCException(PARAM_ERROR);
            }
        } else if (operateType == SERVICE) {
            if (requestPacket.getAsyncCallback() != null && !(requestPacket.getAsyncCallback() instanceof AsyncCallback.ServiceCallback)) {
                throw new DCException(PARAM_ERROR);
            }
        } else {
            throw new DCException(OPERATE_TYPE_NOT_SUPPORT);
        }
    }

    /**
     * 发送数据包的异步接口
     *
     * @param requestPacket
     */

    public void send(RequestPacket requestPacket) {
        while (!isStart) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        DCPacket dcPacket = new DCPacket();
        dcPacket.setRequestPacket(requestPacket);
        dcHandler.receiveMessage(dcPacket);
    }

    /**
     * 从配置文件中启动
     *
     * @param args
     */
    public void start(String args[]) throws DCConfig.ConfigException, IOException {
        if (args.length != 1) {
            LOG.error("no config file");
            throw new RuntimeException();
        }
        DCConfig dcConfig = new DCConfig();
        dcConfig.parse(args[0]);
        String ipAndPort = dcConfig.myIp + ":" + dcConfig.port;
        DCProcessor dcProcessor = new DCProcessor(new DCDatabaseImpl());
        LoadBalanceConsistentHash loadBalanceConsistentHash = new LoadBalanceConsistentHash();
        loadBalanceConsistentHash.setNumOfVirtualNode(dcConfig.numberOfViturlNodes);
        loadBalanceConsistentHash.setDCProcessor(dcProcessor);
        loadBalanceConsistentHash.setMyIp(ipAndPort);
        tcpServer = new TcpServer(ipAndPort, dcConfig.numberOfViturlNodes, loadBalanceConsistentHash, dcProcessor);
        try {
            tcpServer.bind(dcConfig.myIp, dcConfig.port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        registerAndMonitorService = new ZKRegisterAndMonitorService();
        registerAndMonitorService.register(dcConfig.zooKeeperServer, ipAndPort, loadBalanceConsistentHash);
        dcHandler = new DCHandler(dcProcessor);
        isStart = true;

    }

    public void shutDown() {
        isStart = false;
        registerAndMonitorService.close();
        dcHandler.close();
        tcpServer.close();
    }

    /**
     * 是否本地处理
     *
     * @param key
     * @return
     */
    public boolean isLocal(String key) {
        return false;
    }
}
