package lab.mars.dc;

import java.io.Serializable;

/**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */
public interface ResourceService extends Serializable {

    void init();

    void start();

    ResultDO service(DataContent dataContent);

    void shutdown();

    String[] getRelatedResources();

    void setRelatedResources(String[] relatedResources);
}
