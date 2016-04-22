package lab.mars.dc.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lab.mars.dc.DCPacket;
import lab.mars.dc.network.TcpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public class ClientChannelHandler extends
        SimpleChannelInboundHandler<Object> {

    private static final Logger LOG = LoggerFactory
            .getLogger(ClientChannelHandler.class);
    private final TcpClient tcpClient;

    public ClientChannelHandler(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {

        try {
            readResponse((DCPacket) msg);
        } catch (IOException e) {
            LOG.error("channel read error:{}", e);
        }
    }

    private void readResponse(DCPacket dcPacket) throws IOException {
        DCPacket packet;
            if (tcpClient.getPendingQueue().size() == 0) {
                throw new IOException("Nothing in the queue, but got "
                );
            }
            packet = tcpClient.getPendingQueue().remove(dcPacket.getCid());
            synchronized (packet){
                packet.setFinished(true);
                packet.setResponsePacket(dcPacket.getResponsePacket());
                packet.notifyAll();
            }
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
