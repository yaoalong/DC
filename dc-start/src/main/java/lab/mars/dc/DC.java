package lab.mars.dc;

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

    public static void main(String args[]) {
        DC dc = new DC();
        try {
            dc.start(args);
        } catch (DCConfig.ConfigException e) {
            LOG.error("Invalid config, exiting abnormally", e);
            System.err.println("Invalid config, exiting abnormally");
            System.exit(2);
        } catch (Exception e) {
            LOG.error("Unexpected exception, exiting abnormally", e);
            System.exit(1);
        }
        dc.send(generateDCRequestPacket(), new AsyncCallback.ServiceCallback() {
            @Override
            public void processResult(DCException.Code code, String id, ResultDO resultDO) {
                if(resultDO instanceof  NameResultDO){
                    System.out.println(((NameResultDO)resultDO).getName());
                }
                System.out.println("id:" + id + ":code:" + code.getCode() + ":resultDO:" + resultDO.toString());
            }
        });

    }

    public static RequestPacket generateDCRequestPacket() {
        RequestPacket requestPacket = new RequestPacket();
        requestPacket.setId("11133");

        LogResourceServiceImpl logResourceService = new LogResourceServiceImpl();
        logResourceService.setId(1222);
        byte[] bytes = ResourceReflection.serializeKryo(logResourceService);
        requestPacket.setResourceService(bytes);
        requestPacket.setOperateType(OperateType.SERVICE);
        return requestPacket;
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
    public void start(String args[]) throws DCConfig.ConfigException {
        if (args.length != 1) {
            LOG.error("no config file");
            System.exit(-1);
        }
        DCConfig dcConfig = new DCConfig();
        dcConfig.parse(args[0]);
        LoadBalanceConsistentHash loadBalanceConsistentHash = new LoadBalanceConsistentHash();
        loadBalanceConsistentHash.setNumOfVirtualNode(dcConfig.numberOfViturlNodes);
        ZKRegisterAndMonitorService registerAndMonitorService = new ZKRegisterAndMonitorService();
        registerAndMonitorService.register(dcConfig.zooKeeperServer, dcConfig.myIp + ":" + dcConfig.port, loadBalanceConsistentHash);
        tcpServer = new TcpServer(dcConfig.myIp + ":" + dcConfig.port, dcConfig.numberOfViturlNodes, loadBalanceConsistentHash, new DCDatabaseImpl());


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
