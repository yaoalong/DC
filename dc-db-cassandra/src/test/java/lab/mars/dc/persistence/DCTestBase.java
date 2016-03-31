package lab.mars.dc.persistence;

import lab.mars.dc.server.ResourceServiceDO;
import org.junit.After;
import org.junit.Before;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public class DCTestBase {


    protected DCDatabaseImpl dcDatabase;

    @Before
    public void before() {

        dcDatabase = new DCDatabaseImpl(true, "tests", "dc", "192.168.10.124");
        for (int i = 0; i < 10; i++) {
            ResourceServiceDO resourceServiceDO = new ResourceServiceDO();
            resourceServiceDO.setData(("allen" + i).getBytes());
            resourceServiceDO.setId("" + i);
            dcDatabase.create(resourceServiceDO);
        }

    }

    @After
    public void after() {
        dcDatabase.close();
    }
}
