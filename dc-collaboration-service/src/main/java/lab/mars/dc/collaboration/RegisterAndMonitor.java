package lab.mars.dc.collaboration;

import lab.mars.dc.loadbalance.LoadBalanceService;

/**
 * Author:yaoalong.
 * Date:2016/3/30.
 * Email:yaoalong@foxmail.com
 */
public interface RegisterAndMonitor {

    void register(String zooKeeperServer, String value, LoadBalanceService loadBalanceInterface);
}
