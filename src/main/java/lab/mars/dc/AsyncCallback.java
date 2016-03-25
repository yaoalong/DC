package lab.mars.dc; /**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */

/**
 * 异步回调函数
 */

public interface AsyncCallback {

        void processResult(OperateResultCode operateResultCode, String id, ResultDO resultDO, ResourceService resourceService);
}
