package lab.mars.dc.network.initializer;

import io.netty.channel.ChannelPipeline;
import lab.mars.dc.connectmanage.LRUManage;
import lab.mars.dc.loadbalance.LoadBalanceInterface;
import lab.mars.dc.network.handler.ServerChannelHandler;
import lag.mars.server.DCDatabase;

public class PacketServerChannelInitializer extends TcpChannelInitializer {
    private LRUManage lruManage;
private DCDatabase dcDatabase;
    private  LoadBalanceInterface loadBalanceInterface;
    public PacketServerChannelInitializer(LRUManage lrumanage, LoadBalanceInterface loadBalanceInterface,DCDatabase dcDatabase) {
        this.lruManage = lrumanage;
        this.dcDatabase=dcDatabase;
        this.loadBalanceInterface=loadBalanceInterface;
    }

    @Override
    public void init(ChannelPipeline ch) {
        ch.addLast(new ServerChannelHandler(lruManage,loadBalanceInterface,dcDatabase));

    }
}