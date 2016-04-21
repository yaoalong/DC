package lab.mars.dc.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lab.mars.dc.DCPacket;
import lab.mars.dc.connectmanage.LRUManage;
import lab.mars.dc.loadbalance.LoadBalanceService;
import lab.mars.dc.server.DCHandler;
import lab.mars.server.DCProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public class ServerChannelHandler extends
        SimpleChannelInboundHandler<Object> {
    private static Logger LOG = LoggerFactory
            .getLogger(ServerChannelHandler.class);

    private final LRUManage lruManage;

    private final DCHandler dcHandler;

    public ServerChannelHandler(String self, LRUManage lruManage, LoadBalanceService loadBalanceService, DCProcessor dcProcessor) {
        this.lruManage = lruManage;
        this.dcHandler = new DCHandler(dcProcessor, self, loadBalanceService);

    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        lruManage.refresh(ctx);
        dcHandler.receiveMessage((DCPacket) msg, ctx.channel());

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
        LOG.info("Channel disconnect caused close:", cause);
        cause.printStackTrace();
        lruManage.remove(ctx);
        ctx.close();
    }


}
