package lab.mars.dc.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lab.mars.dc.DCPacket;
import lab.mars.dc.RequestPacket;
import lab.mars.dc.connectmanage.LRUManage;
import lab.mars.dc.loadbalance.LoadBalanceConsistentHash;
import lab.mars.dc.loadbalance.LoadBalanceService;
import lab.mars.dc.network.TcpClient;
import lag.mars.server.DCDatabase;
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
    private ConcurrentHashMap<String, TcpClient> ipAndTcpClient = new ConcurrentHashMap<>();
    private String self;
    private LoadBalanceService loadBalanceService;

    private LRUManage lruManage;

    private DCDatabase dcDatabase;

    public ServerChannelHandler(String self, LRUManage lruManage, LoadBalanceService loadBalanceService, DCDatabase dcDatabase) {
    	this.self=self;
        this.lruManage = lruManage;
        this.loadBalanceService = loadBalanceService;
        this.dcDatabase = dcDatabase;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        System.out.println("收到消息");
        lruManage.refresh(ctx.channel());
        DCPacket dcPacket = (DCPacket) msg;
        try {
            if (preProcessPacket(dcPacket, ctx)) {
                dcDatabase.receiveMessage(dcPacket, ctx.channel());
//                    M2mHandlerResult m2mHandlerResult = m2mHandler
//                            .recv(m2mPacket);
//
//                    boolean isDistributed = m2mHandlerResult.isFlag();
//                    if (isDistributed == true) {
//                        NettyServerCnxn nettyServerCnxn = ctx.attr(STATE).get();
//                        nettyServerCnxn.receiveMessage(ctx,
//                                m2mHandlerResult.getM2mPacket());
//                    }
                //TODO 交给处理器进行处理


            } else {// 需要增加对错误的处理

            }
        } catch (Exception e) {
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
        lruManage.add(ctx.channel());
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
        ctx.close();

    }

    /**
     * 数据包的处理
     * @param dcPacket
     * @param ctx
     * @return
     */
    public boolean preProcessPacket(DCPacket dcPacket,
                                    ChannelHandlerContext ctx) {
        String key = dcPacket.getRequestPacket().getId();


        String server = loadBalanceService.getServer(key);
        if (server.equals(self)) {
            return true;
        }
        if (ipAndTcpClient.containsKey(server)) {
            ipAndTcpClient.get(server).write(dcPacket);
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
