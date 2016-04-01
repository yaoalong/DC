package lab.mars.dc; 
import java.io.Serializable;
/**



import java.io.Serializable;
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */

/**
 * 异步回调函数
 */

public interface AsyncCallback extends Serializable {

        void processResult(OperateResultCode operateResultCode, String id, ResultDO resultDO, ResourceService resourceService);
}
