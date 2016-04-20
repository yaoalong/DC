//package lab.mars.dc.network;
//
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
//import lab.mars.dc.network.initializer.HttpServerChannelInitializer;
//
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * Author:yaoalong.
// * Date:2016/4/16.
// * Email:yaoalong@foxmail.com
// */
//public class HttpServer {
//    private Set<Channel> channels;
//
//    public void bind(String host, int port) throws InterruptedException {
//        channels = new HashSet<>();
//        ServerBootstrap b = new ServerBootstrap();
//        b.group(NetworkEventLoopGroup.bossGroup,
//                NetworkEventLoopGroup.workerGroup)
//                .channel(NioServerSocketChannel.class)
//                .option(ChannelOption.TCP_NODELAY, true)
//                .option(ChannelOption.SO_BACKLOG, 1000)
//                .childHandler(new HttpServerChannelInitializer());
//        b.bind(host, port).addListener((ChannelFuture channelFuture) -> {
//            channels.add(channelFuture.channel());
//        });
//
//    }
//
//    public void close() {
//        channels.forEach(channel -> channel.close());
//        NetworkEventLoopGroup.shutdown();
//    }
// public static void main(String args[]) throws InterruptedException {
//     HttpServer httpServer=new HttpServer();
//     httpServer.bind("localhost",2185);
// }
//}