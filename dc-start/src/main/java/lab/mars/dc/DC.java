package lab.mars.dc;

import lab.mars.dc.collaboration.ZKRegisterAndMonitorService;
import lab.mars.dc.loadbalance.LoadBalanceConsistentHash;
import lab.mars.dc.network.SendThread;
import lab.mars.dc.network.TcpServer;

/**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */
public class DC {

    private TcpServer tcpServer;


    private SendThread sendThread;

    private volatile boolean isStart = false;

    public static void main(String args[]) {
        DC dc = new DC();
        dc.start(args);
    }

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
        while (!isStart) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        DCPacket dcPacket = new DCPacket();
        requestPacket.setAsyncCallback(asyncCallback);
        dcPacket.setRequestPacket(requestPacket);
        sendThread.send(dcPacket);
    }

    /**
     * 从配置文件中启动
     *
     * @param args
     */
    public void start(String args[]) {

        DCConfig dcConfig = new DCConfig();
        dcConfig.parse(args[0]);
        LoadBalanceConsistentHash loadBalanceConsistentHash = new LoadBalanceConsistentHash();
        loadBalanceConsistentHash.setNumOfVirtualNode(dcConfig.numberOfViturlNodes);
        ZKRegisterAndMonitorService registerableService = new ZKRegisterAndMonitorService();
        registerableService.register(dcConfig.zooKeeperServer, dcConfig.myIp + ":" + dcConfig.port, loadBalanceConsistentHash);
        tcpServer = new TcpServer(dcConfig.myIp + ":" + dcConfig.port, dcConfig.numberOfViturlNodes, loadBalanceConsistentHash);


        try {
            tcpServer.bind(dcConfig.myIp, dcConfig.port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendThread = new SendThread(dcConfig.myIp, dcConfig.port);
        sendThread.start();
        isStart = true;

    }

    public void shutDown() {
        isStart = false;
        tcpServer.close();
    }
}
