package lab.mars.dc.network.initializer;

import io.netty.channel.ChannelPipeline;
import lab.mars.dc.network.TcpClient;
import lab.mars.dc.network.handler.ClientChannelHandler;

/**
 *
 */
public class PacketClientChannelInitializer extends TcpChannelInitializer {
    private TcpClient tcpClient;

    public PacketClientChannelInitializer(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    @Override
    public void init(ChannelPipeline ch) {

        ch.addLast(new ClientChannelHandler(tcpClient));

    }
}
