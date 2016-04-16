package lab.mars.dc.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author:yaoalong.
 * Date:2016/4/16.
 * Email:yaoalong@foxmail.com
 */
public class HttpClientChannelHandler extends
        SimpleChannelInboundHandler<FullHttpResponse> {

    private static final Logger LOG = LoggerFactory
            .getLogger(ClientChannelHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) {

        System.out.println(msg.content().toString(CharsetUtil.UTF_8));
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.error("close ctx,because of:", cause);
        ctx.close();
    }

}
