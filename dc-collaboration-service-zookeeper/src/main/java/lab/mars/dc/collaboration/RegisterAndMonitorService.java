package lab.mars.dc.collaboration;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

/**
 * Author:yaoalong.
 * Date:2016/3/30.
 * Email:yaoalong@foxmail.com
 */
public class RegisterAndMonitorService implements RegisterAndMonitor {


    public void register(String zooKeeperServer, String value) {
        RegisterIntoZooKeeper registerIntoZooKeeper = new RegisterIntoZooKeeper();
        registerIntoZooKeeper.setServer(zooKeeperServer);
        try {
            registerIntoZooKeeper.register(value);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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

        ZooKeeper_Monitor zooKeeper_monitor = new ZooKeeper_Monitor();
        zooKeeper_monitor.setServer(zooKeeperServer);
        zooKeeper_monitor.start();

    }
}
