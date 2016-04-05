package lab.mars.dc;

import lab.mars.dc.exception.DCException;

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

    private static final long serialVersionUID = -2602934100954710281L;
    /**
     * id
     * 操作表示码，用来表示异常
     * 结果
     * 处理结果
     * resourceService，当操作码为检索的时候，用来返回具体的resourceService
     */
    private String id;
    private DCException.Code code;
    private byte[] result;

    private byte[] resourceService;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DCException.Code getCode() {
        return code;
    }

    public void setCode(DCException.Code code) {
        this.code = code;
    }

    public byte[] getResult() {
        return result;
    }

    public void setResult(byte[] result) {
        this.result = result;
    }

    public byte[] getResourceService() {
        return resourceService;
    }

    public void setResourceService(byte[] resourceService) {
        this.resourceService = resourceService;
    }
}
