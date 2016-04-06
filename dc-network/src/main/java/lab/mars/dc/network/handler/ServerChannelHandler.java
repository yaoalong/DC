package lab.mars.dc.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lab.mars.dc.DCPacket;
import lab.mars.dc.ResponsePacket;
import lab.mars.dc.connectmanage.LRUManage;
import lab.mars.dc.exception.DCException;
import lab.mars.dc.loadbalance.LoadBalanceService;
import lab.mars.dc.network.TcpClient;
import lab.mars.server.DCProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public class ServerChannelHandler extends
        SimpleChannelInboundHandler<Object> {
    private static Logger LOG = LoggerFactory
            .getLogger(ServerChannelHandler.class);
    private final LinkedList<DCPacket> pendingQueue = new LinkedList<DCPacket>();
    private final ConcurrentHashMap<String, TcpClient> ipAndTcpClient = new ConcurrentHashMap<>();
    private final String self;
    private final LoadBalanceService loadBalanceService;

    private final LRUManage lruManage;

    private final DCProcessor dcProcessor;

    public ServerChannelHandler(String self, LRUManage lruManage, LoadBalanceService loadBalanceService, DCProcessor dcProcessor) {
        this.self = self;
        this.lruManage = lruManage;
        this.loadBalanceService = loadBalanceService;
        this.dcProcessor = dcProcessor;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        lruManage.refresh(ctx);
        DCPacket dcPacket = (DCPacket) msg;
        try {
            if (preProcessPacket(dcPacket, ctx)) {
                dcProcessor.receiveMessage(dcPacket, ctx.channel());
            }
        }
        catch (DCException e){
            DCException.Code code=e.getCode();
            ResponsePacket responsePacket=new ResponsePacket();
            responsePacket.setCode(code);
            DCPacket result=new DCPacket();
            result.setResponsePacket(responsePacket);
            ctx.writeAndFlush(result);
        }
        catch (Exception e) {
//            M2mReplyHeader m2mReplyHeader = new M2mReplyHeader(0, 0,
//                    e.getCode());
//            M2mRecord m2mRecord = null;
//            M2mPacket responseM2mPacket = new M2mPacket(null, m2mReplyHeader,
//                    null, m2mRecord);
//            ctx.writeAndFlush(responseM2mPacket);
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        lruManage.add(ctx);
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.info("Channel disconnect caused close:{}", cause);
        cause.printStackTrace();
        lruManage.remove(ctx);
        ctx.close();

    }

    /**
     * 数据包的处理
     *
     * @param dcPacket
     * @param ctx
     * @return
     */
    public boolean preProcessPacket(DCPacket dcPacket,
                                    ChannelHandlerContext ctx) throws DCException {
        if(dcPacket==null||dcPacket.getRequestPacket()==null|| StringUtils.isBlank(dcPacket.getRequestPacket().getId())){
            throw new DCException(DCException.Code.PARAM_ERROR);
        }
        String key = dcPacket.getRequestPacket().getId();
        String server = loadBalanceService.getServer(key);
        if (server.equals(self)) {
            return true;
        }
        for(int i=0;i<5;i++){
            if (ipAndTcpClient.containsKey(server)) {
                try {
                    ipAndTcpClient.get(server).write(dcPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                    ipAndTcpClient.remove(server);
                    continue;
                }
                ctx.writeAndFlush(dcPacket);
                return false;
            } else {
                try {
                    TcpClient tcpClient = new TcpClient(pendingQueue);
                    String[] splitStrings = spilitString(server);
                    tcpClient.connectionOne(splitStrings[0],
                            Integer.valueOf(splitStrings[1]));
                    tcpClient.write(dcPacket);
                    ctx.writeAndFlush(dcPacket);
                    ipAndTcpClient.put(server, tcpClient);
                    return false;
                } catch (Exception e) {
                    LOG.error("process packet error:{}", e);
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            server = loadBalanceService.getServer(key);
        }
        return false;
    }

    /*
     * 将server拆分为ip以及port
     */
    private String[] spilitString(String ip) {
        String[] splitMessage = ip.split(":");
        return splitMessage;
    }

}
