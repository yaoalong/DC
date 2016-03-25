package lab.mars.dc.test;

import lab.mars.dc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */
public class DCTestBase {

    protected Logger LOG = LoggerFactory.getLogger(getClass());
    protected DC dc = new DC();
    AsyncCallback asyncCallback=new AsyncCallback() {
        public void processResult(OperateResultCode operateResultCode, String id, ResultDO resultDO, ResourceService resourceService) {
            if(operateResultCode.getCode()== OperateResultCode.OK.getCode()){
                //请求正常
            }
            else{
                //处理异常
            }
        }
    };
    public void start() throws Exception {
        dc.start(new String[]{});
    }

    public void shutDown() throws Exception {
        dc.shutDown();
    }
}
