package lab.mars.dc.persistence;

import lab.mars.dc.server.ResourceServiceDO;
import org.junit.Test;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public class DCDatabaseRetrieveTest extends  DCTestBase {

    @Test
    public void test(){
        for(int i=0;i<10;i++){
            ResourceServiceDO resourceServiceDO=dcDatabase.retrieve(i+"");
            System.out.println(resourceServiceDO.getId()+":"+new String(resourceServiceDO.getData()));
        }
    }
}
