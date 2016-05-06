package lab.mars.dc.network;

import lab.mars.dc.connectmanage.LRUManage;
import lab.mars.dc.loadbalance.LoadBalanceService;
import lab.mars.dc.network.initializer.PacketServerChannelInitializer;
import lab.mars.dc.server.DCProcessor;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public class TcpServer extends TcpServerNetwork {

    private DCProcessor dcProcessor;

    public TcpServer(String self, Integer numberOfConnections, LoadBalanceService loadBalanceService, DCProcessor dcProcessor) {
        LRUManage lruManage = new LRUManage(numberOfConnections);
        this.dcProcessor = dcProcessor;
        setChannelChannelInitializer(new PacketServerChannelInitializer(self,
                lruManage, loadBalanceService, dcProcessor));

    }

    @Override
    public void close() {
        dcProcessor.close();
        super.close();
    }
}

