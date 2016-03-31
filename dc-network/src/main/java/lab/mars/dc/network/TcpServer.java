package lab.mars.dc.network;

import lab.mars.dc.connectmanage.LRUManage;
import lab.mars.dc.network.initializer.PacketServerChannelInitializer;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public class TcpServer extends TcpServerNetwork {

    public TcpServer(Integer numberOfConnections) {
        setChannelChannelInitializer(new PacketServerChannelInitializer(
             new LRUManage(
                numberOfConnections)));

    }

}

