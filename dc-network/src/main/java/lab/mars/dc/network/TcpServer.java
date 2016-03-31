package lab.mars.dc.network;

import lab.mars.dc.connectmanage.LRUManage;
import lab.mars.dc.loadbalance.LoadBalanceInterface;
import lab.mars.dc.network.initializer.PacketServerChannelInitializer;
import lab.mars.dc.persistence.DCDatabaseImpl;
import lag.mars.server.DCDatabase;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public class TcpServer extends TcpServerNetwork {

    public TcpServer(Integer numberOfConnections, LoadBalanceInterface  loadBalanceInterface) {
    	System.out.println("GG"+numberOfConnections);
    	LRUManage lruManage=new LRUManage(numberOfConnections);
        setChannelChannelInitializer(new PacketServerChannelInitializer(
        		lruManage,loadBalanceInterface,new DCDatabase(new DCDatabaseImpl())));

    }

}

