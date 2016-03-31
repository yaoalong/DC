package lab.mars.dc.network.initializer;

import io.netty.channel.ChannelPipeline;
import lab.mars.dc.connectmanage.LRUManage;
import lab.mars.dc.network.handler.ServerChannelHandler;

public class PacketServerChannelInitializer extends TcpChannelInitializer {
    private LRUManage lruManage;

    public PacketServerChannelInitializer(LRUManage lrumanage) {
        this.lruManage = lrumanage;
    }

    @Override
    public void init(ChannelPipeline ch) {
        ch.addLast(new ServerChannelHandler(lruManage,null));

    }
}