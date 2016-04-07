package lab.mars.dc.collaboration;

import lab.mars.dc.loadbalance.LoadBalanceService;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/*
 * 监控zookeeper,从而可以获取在线机器列表
 */
public class ZooKeeper_Monitor extends Thread implements Watcher {

    private static final Logger LOG = LoggerFactory
            .getLogger(ZooKeeper_Monitor.class);
    private static final String ROOT_NODE = "/server";
    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    private ZooKeeper zooKeeper;
    /*
     * zooKeeper服务器的地址
     */
    private String zooKeeperServer;
    private LoadBalanceService loadBalanceService;

    public void run() {
        try {
            zooKeeper = new ZooKeeper(zooKeeperServer, 5000, this);
            countDownLatch.await();
            getChildrens();

            while (true) {
                zooKeeper.getChildren("/server", this);
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("zookeepeer_monitor is error because of:",
                    e);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        if (KeeperState.SyncConnected == event.getState()
                && EventType.NodeChildrenChanged != event.getType()) {
            countDownLatch.countDown();
        } else if (EventType.NodeChildrenChanged == event.getType()
                && event.getPath().startsWith("/server")) {
            try {
                if (zooKeeper == null) {
                    return;
                }
                getChildrens();
            } catch (KeeperException | InterruptedException e) {
                LOG.error("error:{}", e.getMessage());
            }
        }
    }

    /*
     * 去修改networkPool的服务器列表
     */
    private void getChildrens() throws KeeperException, InterruptedException {
        if (zooKeeper == null) {
            LOG.error("zookeeper is empty");
            return;
        }
        List<String> serverStrings = zooKeeper.getChildren(ROOT_NODE, this);
        loadBalanceService.setServers(serverStrings);
    }


    public void setZooKeeperServer(String zooKeeperServer) {
        this.zooKeeperServer = zooKeeperServer;
    }

    public void setLoadBalanceService(LoadBalanceService loadBalanceService) {
        this.loadBalanceService = loadBalanceService;
    }
}
