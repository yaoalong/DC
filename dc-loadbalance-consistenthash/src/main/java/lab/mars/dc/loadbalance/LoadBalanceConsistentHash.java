package lab.mars.dc.loadbalance;

import lab.mars.dc.server.DCProcessor;
import lab.mars.dc.server.RangeDO;
import lab.mars.dc.util.MD5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.*;

/**
 * Author:yaoalong.
 * Date:2016/3/30.
 * Email:yaoalong@foxmail.com
 */


public class LoadBalanceConsistentHash implements LoadBalanceService {
    private static Logger LOG = LoggerFactory.getLogger(LoadBalanceConsistentHash.class);
    /**
     * 默认虚拟节点是一个
     */
    private int numOfVirtualNode = 1;
    private List<String> servers;
    private volatile boolean initialized = false;
    private TreeMap<Long, String> consistentBuckets = new TreeMap<>();

    private DCProcessor dcProcessor;

    private String myIp;


    public void populateConsistentBuckets() {
        if (servers == null || servers.size() <= 0) {
            if (LOG.isErrorEnabled())
                LOG.error("++++ trying to initialize with no servers");
            throw new IllegalStateException(
                    "++++ trying to initialize with no servers");
        }
        getConsistentBuckets(servers);
        initialized = true;
    }

    private void getConsistentBuckets(List<String> servers) {
        MessageDigest md5 = MD5Hash.MD5.get();
        for (int i = 0; i < servers.size(); i++) {
            for (long j = 0; j < numOfVirtualNode; j++) {
                byte[] d = md5.digest((servers.get(i) + "-" + j).getBytes(Charset.forName("utf-8")));
                Long k = ((long) (d[3] & 0xFF) << 24)
                        | ((long) (d[2] & 0xFF) << 16)
                        | ((long) (d[1] & 0xFF) << 8)
                        | ((long) (d[0] & 0xFF));

                consistentBuckets.put(k, servers.get(i));
            }
        }
    }


    @Override
    public void setServers(List<String> servers) {
        this.servers = servers;
        synchronized (consistentBuckets) {
            populateConsistentBuckets();
            dcProcessor.update(getRanges(myIp));
        }

    }

    public void setNumOfVirtualNode(Integer numOfVirtualNode) {
        this.numOfVirtualNode = numOfVirtualNode;
    }

    public String getServer(String key) {
        synchronized (consistentBuckets) {
            return consistentBuckets.get(getBucket(key));
        }

    }

    @Override
    public void setDCProcessor(DCProcessor dcProcessor) {
        this.dcProcessor = dcProcessor;
    }

    private final long getBucket(String key) {
        long hc = MD5Hash.md5HashingAlg(key);
        long result = findPointFor(hc);
        return result;
    }

    private final Long findPointFor(Long hv) {
        SortedMap<Long, String> tmap = this.consistentBuckets.tailMap(hv);
        return (tmap.isEmpty()) ? this.consistentBuckets.firstKey() : tmap
                .firstKey();

    }

    public List<RangeDO> getRanges(String server) {
        List<RangeDO> result = new ArrayList<>();
        if (consistentBuckets.isEmpty()) {
            return result;
        }
        long pre = 0;
        for (Map.Entry<Long, String> entry : consistentBuckets.entrySet()) {

            if (server.equals(entry.getValue())) {

                RangeDO rangeDO = new RangeDO(pre, entry.getKey());
                result.add(rangeDO);
            }

            pre = entry.getKey();
        }
        if (server.equals(consistentBuckets.firstEntry().getValue())) {
            Map.Entry<Long, String> end = consistentBuckets.lastEntry();
            RangeDO rangeDO = new RangeDO(end.getKey(), Long.MAX_VALUE);
            result.add(rangeDO);
        }
        return result;
    }

    @Override
    public void setMyIp(String myIp) {
        this.myIp = myIp;
    }
}
