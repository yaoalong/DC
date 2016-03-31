package lab.mars.dc.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lab.mars.dc.RequestPacket;
import lab.mars.dc.connectmanage.LRUManage;
import lab.mars.dc.loadbalance.LoadBalanceConsistentHash;
import lab.mars.dc.network.TcpClient;
import oracle.jrockit.jfr.events.RequestableEventEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
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
    private final LinkedList<RequestPacket> pendingQueue = new LinkedList<RequestPacket>();
    private ConcurrentHashMap<String, TcpClient> ipAndTcpClient = new ConcurrentHashMap<>();
    private String self;
    private LoadBalanceConsistentHash loadBalanceConsistentHash;

    private LRUManage lruManage;

    public ServerChannelHandler(LRUManage lruManage, LoadBalanceConsistentHash loadBalanceConsistentHash) {
        this.lruManage = lruManage;
        this.loadBalanceConsistentHash = loadBalanceConsistentHash;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        lruManage.refresh(ctx.channel());
        RequestPacket m2mPacket = (RequestPacket) msg;
        try {
            if (preProcessPacket(m2mPacket, ctx)) {

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
     * 对数据进行处理
     * @param m2mPacket
     * @param ctx
     * @return
     */
    public boolean preProcessPacket(RequestPacket m2mPacket,
                                    ChannelHandlerContext ctx) {
        String key = m2mPacket.getId();



        String server = loadBalanceConsistentHash.getServer(key);
        if(server.equals(self)){
            return true;
        }
        if (ipAndTcpClient.containsKey(server)) {
            ipAndTcpClient.get(server).write(m2mPacket);
            ctx.writeAndFlush(m2mPacket);
            return false;
        } else {
            try {
                TcpClient tcpClient = new TcpClient(pendingQueue);
                String[] splitStrings = spilitString(server);
                tcpClient.connectionOne(splitStrings[0],
                        Integer.valueOf(splitStrings[1]));

                tcpClient.write(m2mPacket);
                ctx.writeAndFlush(m2mPacket);
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
