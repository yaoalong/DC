package lab.mars.dc.network.initializer;

import io.netty.channel.ChannelPipeline;
import lab.mars.dc.connectmanage.LRUManage;
import lab.mars.dc.network.handler.ServerChannelHandler;
import lag.mars.server.DCDatabase;

public class PacketServerChannelInitializer extends TcpChannelInitializer {
    private LRUManage lruManage;
private DCDatabase dcDatabase;
    public PacketServerChannelInitializer(LRUManage lrumanage,DCDatabase dcDatabase) {
        this.lruManage = lrumanage;
        this.dcDatabase=dcDatabase;
    }

    @Override
    public void init(ChannelPipeline ch) {
        ch.addLast(new ServerChannelHandler(lruManage,null,dcDatabase));

    }
}