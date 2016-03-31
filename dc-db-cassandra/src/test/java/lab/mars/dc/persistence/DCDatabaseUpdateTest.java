package lab.mars.dc.persistence;

import lab.mars.dc.server.ResourceServiceDO;
import org.junit.Test;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public class DCDatabaseUpdateTest  extends  DCTestBase{

    @Test
    public void test(){
        for(int i=0;i<10;i++){
            ResourceServiceDO resourceServiceDO=new ResourceServiceDO();
            resourceServiceDO.setId(i+"");
            resourceServiceDO.setData("aaa".getBytes());
            dcDatabase.update(i+"",resourceServiceDO);
        }
    }
}
