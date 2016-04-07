package lab.mars.dc;

import lab.mars.dc.collaboration.RegisterAndMonitorService;
import lab.mars.dc.collaboration.ZKRegisterAndMonitorService;
import lab.mars.dc.exception.DCException;
import lab.mars.dc.impl.LogResourceServiceImpl;
import lab.mars.dc.loadbalance.LoadBalanceConsistentHash;
import lab.mars.dc.network.SendThread;
import lab.mars.dc.network.TcpServer;
import lab.mars.dc.persistence.DCDatabaseImpl;
import lab.mars.dc.reflection.ResourceReflection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */
public class DC {


    private static final Logger LOG = LoggerFactory.getLogger(DC.class);
    private TcpServer tcpServer;


    private SendThread sendThread;

    private volatile boolean isStart = false;
    private RegisterAndMonitorService registerAndMonitorService;

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
    public void start(String args[]) throws DCConfig.ConfigException, IOException {
        if (args.length != 1) {
            LOG.error("no config file");
            System.exit(-1);
        }
        DCConfig dcConfig = new DCConfig();
        dcConfig.parse(args[0]);
        String ipAndPort=dcConfig.myIp+":"+dcConfig.port;
        LoadBalanceConsistentHash loadBalanceConsistentHash = new LoadBalanceConsistentHash();
        loadBalanceConsistentHash.setNumOfVirtualNode(dcConfig.numberOfViturlNodes);
        tcpServer = new TcpServer(ipAndPort, dcConfig.numberOfViturlNodes, loadBalanceConsistentHash, new DCDatabaseImpl());
        try {
            tcpServer.bind(dcConfig.myIp, dcConfig.port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        registerAndMonitorService = new ZKRegisterAndMonitorService();
        registerAndMonitorService.register(dcConfig.zooKeeperServer,ipAndPort, loadBalanceConsistentHash);
        sendThread = new SendThread(dcConfig.myIp, dcConfig.port);

        sendThread.start();
        isStart = true;

    }

    public void shutDown() {
        isStart = false;
        registerAndMonitorService.close();
        sendThread.close();
        tcpServer.close();

    }
}
