package lab.mars.dc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */

/**
 * 这个服务只是在每次服务的时候打印一条消息
 */
public class LogResourceService extends ResourceService {
    private static Logger LOG= LoggerFactory.getLogger(LogResourceService.class);
    public void init() {

    }

    public void start() {

    }

    public ResultDO service(DataContent dataContent) {
        LOG.info("service:");
        return null;
    }

    public void shutdown() {

    }
}
