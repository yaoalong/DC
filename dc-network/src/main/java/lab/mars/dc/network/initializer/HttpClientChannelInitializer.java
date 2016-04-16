package lab.mars.dc.network.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import lab.mars.dc.network.handler.HttpClientChannelHandler;

/**
 * Author:yaoalong.
 * Date:2016/4/16.
 * Email:yaoalong@foxmail.com
 */
public class HttpClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new HttpClientCodec())
                .addLast(new HttpContentDecompressor())
                .addLast(new HttpObjectAggregator(1048576))
                .addLast(new HttpClientChannelHandler());
    }
}