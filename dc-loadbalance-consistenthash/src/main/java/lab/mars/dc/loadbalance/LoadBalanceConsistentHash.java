package lab.mars.dc.loadbalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Author:yaoalong.
 * Date:2016/3/30.
 * Email:yaoalong@foxmail.com
 */
public class LoadBalanceConsistentHash implements LoadBalanceService {
    private static Logger LOG = LoggerFactory.getLogger(LoadBalanceConsistentHash.class);
    private static ThreadLocal<MessageDigest> MD5 = new ThreadLocal<MessageDigest>() {
        @Override
        protected final MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                if (LOG.isErrorEnabled())
                    LOG.error("++++ no md5 algorithm found");
                throw new IllegalStateException("++++ no md5 algorythm found");
            }
        }
    };
    private int numOfVirtualNode = 1;
    private List<String> servers;
    private volatile boolean initialized = false;
    private TreeMap<Long, String> consistentBuckets = new TreeMap<>();

    /**
     * 计算一个key的hash值
     *
     * @param key
     * @return
     */
    private static long md5HashingAlg(String key) {
        MessageDigest md5 = MD5.get();
        md5.reset();
        md5.update(key.getBytes());
        byte[] bKey = md5.digest();
        long res = ((long) (bKey[3] & 0xFF) << 24)
                | ((long) (bKey[2] & 0xFF) << 16)
                | ((long) (bKey[1] & 0xFF) << 8) | (long) (bKey[0] & 0xFF);
        return res;
    }


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
        MessageDigest md5 = MD5.get();
        for (int i = 0; i < servers.size(); i++) {
            for (long j = 0; j < numOfVirtualNode; j++) {
                byte[] d = md5.digest((servers.get(i) + "-" + j).getBytes());
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

    private final long getBucket(String key) {
        long hc = md5HashingAlg(key);
        long result = findPointFor(hc);
        return result;
    }

    private final Long findPointFor(Long hv) {
        SortedMap<Long, String> tmap = this.consistentBuckets.tailMap(hv);
        return (tmap.isEmpty()) ? this.consistentBuckets.firstKey() : tmap
                .firstKey();

    }
}
