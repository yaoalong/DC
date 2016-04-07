package lab.mars.dc;

import lab.mars.dc.exception.DCException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */
public class DCTestBase {

    protected Logger LOG = LoggerFactory.getLogger(getClass());
    protected DC dc = new DC();
    AsyncCallback asyncCallback=new AsyncCallback.VoidCallback() {
        @Override
        public void processResult(DCException.Code code, String id) {
            if(code== DCException.Code.OK){
                LOG.info("success");
            }
            else{
                LOG.info("error"+code);
            }
        }

    };
    @Before
    public void testBefore() throws IOException, DCConfig.ConfigException {
        dc.start(new String[]{"zoo1.cfg"});
    }
    @After
    public void shutDown() throws Exception {
    	  try {
  			Thread.sleep(3000);
  		} catch (InterruptedException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
        dc.shutDown();
    }
}
