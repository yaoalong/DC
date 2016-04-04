package lab.mars.dc;

import lab.mars.dc.exception.DCException.Code;

import java.io.Serializable;
/**
 * import java.io.Serializable;
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */

/**
 * 异步回调函数
 */
public interface AsyncCallback {

    /**
     * 这个异步接口用来实现无需客户端只需要返回具体的id，以及操作结果码
     * 例如创建服务资源的时候可能只是需要得知是否创建成功
     */
    interface  VoidCallback extends  AsyncCallback{

      void processResult(Code code,String id);
  }

    /**
     * 这个异步接口需要用户返回id、操作结果吗、以及对应的ResourceService
     * 例如一个检索服务
     */
    interface  DataCallback extends  AsyncCallback{
        void processResult(Code code,String id,ResourceService resoureService);
    }

    /**
     * 调用服务的异步调用接口
     */
    interface  ServiceCallback extends  AsyncCallback{
        void processResult(Code code,String id,ResultDO resultDO);
    }
}
