package lab.mars.dc.persistence;

import lab.mars.dc.exception.DCException;
import lab.mars.dc.server.ResourceServiceDO;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public interface DCDatabaseService {


    ResourceServiceDO retrieve(String id)throws DCException;

    Long create(ResourceServiceDO  resourceServiceDO)throws DCException;

    Long delete(String id)throws DCException;

    Long update(String id, ResourceServiceDO resourceServiceDO)throws DCException;

    void close();

}
