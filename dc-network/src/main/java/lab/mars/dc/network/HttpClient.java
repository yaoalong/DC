package lab.mars.dc.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;
import lab.mars.dc.network.initializer.HttpClientChannelInitializer;

import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Author:yaoalong.
 * Date:2016/4/16.
 * Email:yaoalong@foxmail.com
 */
public class HttpClient {
    protected Channel channel;
    protected ReentrantLock reentrantLock = new ReentrantLock();
    protected Condition condition = reentrantLock.newCondition();
    protected volatile boolean isSuccess = true;

    public static HttpRequest makeRequest(HttpMethod method,
                                          String rawPath,
                                          String[][] req_headers,
                                          String requestBody) {
        ByteBufOutputStream out = new ByteBufOutputStream(Unpooled.buffer());
        if (requestBody != null)
            try {
                out.write(requestBody.getBytes(CharsetUtil.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HTTP_1_1, method, rawPath, out.buffer());
        if (req_headers != null)
            for (String[] head : req_headers)
                request.headers().set(head[0], head[1]);
//		request.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, Integer.toString(request.content().readableBytes()));
        return request;
    }

    public void connectionOne(String host, int port) {

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(NetworkEventLoopGroup.workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new HttpClientChannelInitializer());

        bootstrap.connect(host, port).addListener((ChannelFuture future) -> {
            reentrantLock.lock();
            channel = future.channel();

            if (!future.isSuccess()) {
                isSuccess = false;
            }
            condition.signalAll();
            reentrantLock.unlock();
        });


    }

    /**
     * @param msg
     * @throws Exception
     */
    public void write(Object msg) throws Exception {
        if (channel == null) {
            try {
                reentrantLock.lock();
                condition.await();
            } catch (InterruptedException e) {
            } finally {
                reentrantLock.unlock();
            }
        }
        channel.writeAndFlush(msg);

    }

    public void close() {
        if (channel != null) {
            channel.close();
        }
    }
    public static void main(String args[]) throws Exception {
        HttpClient httpClient=new HttpClient();
        httpClient.connectionOne("localhost",2185);
        httpClient.write(makeRequest(HttpMethod.GET,"/",null,"long"));
    }
}
