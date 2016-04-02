package lab.mars.dc.persistence;

import lab.mars.dc.exception.DCException;
import org.junit.Test;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public class DCDatabaseDeleteTest extends  DCTestBase{

    @Test
    public void test() throws DCException {
        for(int i=0;i<10;i++){
            dcDatabase.delete(""+i);
        }
    }
}
