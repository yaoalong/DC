//package lab.mars.dc.network.initializer;
//
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.handler.codec.http.HttpContentCompressor;
//import io.netty.handler.codec.http.HttpObjectAggregator;
//import io.netty.handler.codec.http.HttpServerCodec;
//import lab.mars.dc.network.handler.HttpServerChannelHandler;
//
///**
// * Author:yaoalong.
// * Date:2016/4/16.
// * Email:yaoalong@foxmail.com
// */
//public class HttpServerChannelInitializer   extends ChannelInitializer<SocketChannel> {
//    @Override
//    protected void initChannel(SocketChannel ch) throws Exception {
//        ch.pipeline().addLast(new HttpServerCodec());
//        ch.pipeline().addLast(new HttpObjectAggregator(512*1024));
//        ch.pipeline().addLast(new HttpContentCompressor());
//        ch.pipeline().addLast(new HttpServerChannelHandler());
//    }
//}
