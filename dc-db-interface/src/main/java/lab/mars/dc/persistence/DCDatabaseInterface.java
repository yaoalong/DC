package lab.mars.dc.persistence;

import lab.mars.dc.server.ResourceServiceDO;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public interface DCDatabaseInterface {


    ResourceServiceDO retrieve(String id);

    Long create(Object object);

    Long delete(String id);

    Long update(String id, ResourceServiceDO resourceServiceDO);

    void close();

}
