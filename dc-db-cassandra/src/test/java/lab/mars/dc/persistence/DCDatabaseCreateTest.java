package lab.mars.dc.persistence;

import lab.mars.dc.exception.DCException;
import lab.mars.dc.server.ResourceServiceDO;
import org.junit.Test;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public class DCDatabaseCreateTest extends DCTestBase {
    @Test
    public void testCreate() throws DCException {
        long currentTime=System.currentTimeMillis();
        for (int i = 1; i < 100000; i++) {
            ResourceServiceDO resourceServiceDO = new ResourceServiceDO();
            resourceServiceDO.setData(("allen" + i).getBytes());
            resourceServiceDO.setId("" + i);
            dcDatabase.create(resourceServiceDO);
        }
        System.out.println(System.currentTimeMillis()-currentTime);
    }
    @Test
    public void testConcurrentTest(){

    }
}
