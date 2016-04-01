package lab.mars.dc;

import lab.mars.dc.collaboration.RegisterAndMonitorService;
import lab.mars.dc.loadbalance.LoadBalanceConsistentHash;
import lab.mars.dc.network.TcpServer;

/**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */
public class DC {

    private TcpServer tcpServer;
    /**
     * 发送数据包的同步接口
     *
     * @param requestPacket
     * @return
     */
    public ResponsePacket send(RequestPacket requestPacket) {
        return null;
    }

    /**
     * 发送数据包的异步接口
     *
     * @param requestPacket
     * @param asyncCallback
     */
    public void send(RequestPacket requestPacket, AsyncCallback asyncCallback) {

    }

    /**
     * 从配置文件中启动
     *
     * @param args
     */
    public void start(String args[]) {
        LoadBalanceConsistentHash loadBalanceConsistentHash=new LoadBalanceConsistentHash();
        DCConfig dcConfig = new DCConfig();
        dcConfig.parse(args[0]);
        RegisterAndMonitorService registerableService=new RegisterAndMonitorService();
        registerableService.register(dcConfig.zooKeeperServer,dcConfig.myIp+":"+dcConfig.port,loadBalanceConsistentHash);
        tcpServer=new TcpServer(dcConfig.myIp+":"+dcConfig.port,dcConfig.numberOfViturlNodes,loadBalanceConsistentHash);
        try {
            tcpServer.bind(dcConfig.myIp,dcConfig.port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
    public static void main(String args[]){
        DC dc=new DC();
        dc.start(args);
    }
    public void shutDown() {

    }
}
