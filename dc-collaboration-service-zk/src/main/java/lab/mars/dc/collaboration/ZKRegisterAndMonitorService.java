package lab.mars.dc.collaboration;

import lab.mars.dc.loadbalance.LoadBalanceService;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Author:yaoalong.
 * Date:2016/3/30.
 * Email:yaoalong@foxmail.com
 */
public class ZKRegisterAndMonitorService implements RegisterAndMonitorService,Watcher {
    private ZooKeeper zooKeeper;
    private static final Logger LOG = LoggerFactory
            .getLogger(ZKRegisterAndMonitorService.class);
    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    @Override
    public void register(String zooKeeperServer, String value, LoadBalanceService loadBalanceService) throws IOException {
        zooKeeper = new ZooKeeper(zooKeeperServer, 5000, new ZKRegisterAndMonitorService());
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RegisterIntoZooKeeper registerIntoZooKeeper = new RegisterIntoZooKeeper();
        try {
            registerIntoZooKeeper.register(zooKeeper,value);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        registerIntoZooKeeper.start();
        if (registerIntoZooKeeper != null) {
            try {
                registerIntoZooKeeper.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ZooKeeper_Monitor zooKeeper_monitor = new ZooKeeper_Monitor(zooKeeper);
        zooKeeper_monitor.setLoadBalanceService(loadBalanceService);
        zooKeeper_monitor.start();
    }

    @Override
    public void process(WatchedEvent event) {
        if (Event.KeeperState.SyncConnected == event.getState()
                && Event.EventType.NodeChildrenChanged != event.getType()) {
            countDownLatch.countDown();
        }
    }
    @Override
    public void close(){
        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
