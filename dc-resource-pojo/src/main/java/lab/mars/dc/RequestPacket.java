package lab.mars.dc; /**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */

import java.io.Serializable;

/**
 * 请求数据包
 * id---资源的id
 * ResourceService
 * OperateType---定义的是操作类型
 * data---如果是一个服务资源计算的话，那么有可能需要传入参数
 */
public class RequestPacket implements Serializable{

    private static final long serialVersionUID = 1111221244099503861L;
    private String id;
  //  private ResourceService resourceService;

    private OperateType operateType;


  //  private DataContent data;

    private AsyncCallback asyncCallback;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OperateType getOperateType() {
        return operateType;
    }

    public void setOperateType(OperateType operateType) {
        this.operateType = operateType;
    }

//    public void setResourceService(ResourceService resourceService) {
//        this.resourceService = resourceService;
//    }
//
//    public void setData(DataContent data) {
//        this.data = data;
//    }
//
//
//    public ResourceService getResourceService() {
//        return resourceService;
//    }
//
//    public DataContent getData() {
//        return data;
//    }

    public AsyncCallback getAsyncCallback() {
        return asyncCallback;
    }

    public void setAsyncCallback(AsyncCallback asyncCallback) {
        this.asyncCallback = asyncCallback;
    }
}

