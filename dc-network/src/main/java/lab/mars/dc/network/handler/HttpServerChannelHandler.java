//package lab.mars.dc.network.handler;
//
//import io.netty.buffer.Unpooled;
//import io.netty.channel.ChannelFutureListener;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.handler.codec.http.DefaultFullHttpResponse;
//import io.netty.handler.codec.http.FullHttpRequest;
//import io.netty.handler.codec.http.FullHttpResponse;
//import io.netty.util.CharsetUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import static io.netty.handler.codec.http.HttpResponseStatus.OK;
//import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
//
///**
// * Author:yaoalong.
// * Date:2016/4/16.
// * Email:yaoalong@foxmail.com
// */
//public class HttpServerChannelHandler extends
//        SimpleChannelInboundHandler<FullHttpRequest> {
//    private static Logger LOG = LoggerFactory
//            .getLogger(ServerChannelHandler.class);
//
//    @Override
//    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
//        System.out.println(msg.content().toString(CharsetUtil.UTF_8));
//        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer("World! from Server", CharsetUtil
//                .UTF_8));
//        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
//    }
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ctx.fireChannelRegistered();
//    }
//
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) {
//        ctx.flush();
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//        LOG.info("Channel disconnect caused close:", cause);
//        cause.printStackTrace();
//        ctx.close();
//    }
//
//
//    /*
//     * 将server拆分为ip以及port
//     */
//    private String[] spilitString(String ip) {
//        String[] splitMessage = ip.split(":");
//        return splitMessage;
//    }
//}
