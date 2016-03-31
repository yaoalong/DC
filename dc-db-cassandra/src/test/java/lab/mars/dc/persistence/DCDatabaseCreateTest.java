package lab.mars.dc.persistence;

import lab.mars.dc.server.ResourceServiceDO;
import org.junit.Test;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public class DCDatabaseCreateTest extends DCTestBase {
    @Test
    public void testCreate() {
        for (int i = 10; i < 20; i++) {
            ResourceServiceDO resourceServiceDO = new ResourceServiceDO();
            resourceServiceDO.setData(("allen" + i).getBytes());
            resourceServiceDO.setId("" + i);
            dcDatabase.create(resourceServiceDO);
        }
    }
}
