package lab.mars.dc.loadbalance;

import lab.mars.dc.server.RangeDO;
import lab.mars.server.DCProcessor;

import java.util.List;

/**
 * Author:yaoalong.
 * Date:2016/3/3.
 * Email:yaoalong@foxmail.com
 */
public interface LoadBalanceService {


    void setServers(List<String> servers);

    void setNumOfVirtualNode(Integer factor);

    /**
     * 针对于key获取当前应该处理该key的server
     *
     * @param key
     * @return
     */
    String getServer(String key);

    void setDCProcessor(DCProcessor dcProcessor);

    List<RangeDO> getRanges(String server);

    void setMyIp(String myIp);


}
