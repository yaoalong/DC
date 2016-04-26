package lab.mars.dc.collaboration;

import lab.mars.dc.loadbalance.LoadBalanceService;

import java.io.IOException;

/**
 * Author:yaoalong.
 * Date:2016/3/30.
 * Email:yaoalong@foxmail.com
 */

/**
 * 协同服务接口
 */
public interface RegisterAndMonitorService {

    void register(String zooKeeperServer, String value, LoadBalanceService loadBalanceService) throws IOException;

    void close();
}
