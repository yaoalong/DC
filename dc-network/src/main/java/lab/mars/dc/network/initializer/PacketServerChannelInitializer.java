package lab.mars.dc.network.initializer;

import io.netty.channel.ChannelPipeline;
import lab.mars.dc.connectmanage.LRUManage;
import lab.mars.dc.loadbalance.LoadBalanceService;
import lab.mars.dc.network.handler.ServerChannelHandler;
import lag.mars.server.DCDatabase;

public class PacketServerChannelInitializer extends TcpChannelInitializer {
	private LRUManage lruManage;
	private DCDatabase dcDatabase;
	private LoadBalanceService loadBalanceService;
	private String self;

	public PacketServerChannelInitializer(String self,LRUManage lrumanage,
										  LoadBalanceService loadBalanceService, DCDatabase dcDatabase) {
		this.self=self;
		this.lruManage = lrumanage;
		this.dcDatabase = dcDatabase;
		this.loadBalanceService = loadBalanceService;
	}

	@Override
	public void init(ChannelPipeline ch) {
		ch.addLast(new ServerChannelHandler(self,lruManage, loadBalanceService,
				dcDatabase));

	}
}