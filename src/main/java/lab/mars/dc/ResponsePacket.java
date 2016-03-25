package lab.mars.dc;

import java.io.Serializable;

/**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */

/**
 * 返回的数据包
 */
public class ResponsePacket implements Serializable{

    /**
     * id
     * 操作表示码，用来表示异常
     * 结果
     * 处理结果
     * resourceService，当操作码为检索的时候，用来返回具体的resourceService
     */
    private String id;
    private OperateResultCode operateResultCode;
    private ResultDO result;

    private ResourceService resourceService;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OperateResultCode getOperateResultCode() {
        return operateResultCode;
    }

    public void setOperateResultCode(OperateResultCode operateResultCode) {
        this.operateResultCode = operateResultCode;
    }

    public ResultDO getResult() {
        return result;
    }

    public void setResult(ResultDO result) {
        this.result = result;
    }

    public ResourceService getResourceService() {
        return resourceService;
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }
}
