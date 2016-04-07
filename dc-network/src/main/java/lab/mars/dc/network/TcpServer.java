package lab.mars.dc.network;

import lab.mars.dc.connectmanage.LRUManage;
import lab.mars.dc.loadbalance.LoadBalanceService;
import lab.mars.dc.network.initializer.PacketServerChannelInitializer;
import lab.mars.dc.persistence.DCDatabaseService;
import lab.mars.server.DCProcessor;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public class TcpServer extends TcpServerNetwork {

    private DCProcessor dcProcessor;

    public TcpServer(String self, Integer numberOfConnections, LoadBalanceService loadBalanceService, DCDatabaseService dcDatabaseService) {
        System.out.println("GG" + numberOfConnections);
        LRUManage lruManage = new LRUManage(numberOfConnections);
        dcProcessor = new DCProcessor(dcDatabaseService);
        setChannelChannelInitializer(new PacketServerChannelInitializer(self,
                lruManage, loadBalanceService, dcProcessor));

    }

    @Override
    public void close() {
        dcProcessor.close();
        super.close();


    }


}

