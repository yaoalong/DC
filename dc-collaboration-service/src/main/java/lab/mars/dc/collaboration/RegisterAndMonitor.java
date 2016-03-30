package lab.mars.dc.collaboration;

/**
 * Author:yaoalong.
 * Date:2016/3/30.
 * Email:yaoalong@foxmail.com
 */
public interface RegisterAndMonitor {

    void register(String zooKeeperServer,String value);//TODO 后面加上负载均衡器
}
