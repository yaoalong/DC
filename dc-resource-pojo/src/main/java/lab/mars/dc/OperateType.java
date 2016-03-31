package lab.mars.dc; /**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */
/**
 * 操作类型分为增、删、改、查服务资源
 * SERVICE用来进行服务资源计算
 */
public enum OperateType {
    CREATE(1),DELETE(2),UPDATE(3),RETRIEVE(4),SERVICE(5);
    int code;
    OperateType(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
