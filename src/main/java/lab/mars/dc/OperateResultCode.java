package lab.mars.dc;

/**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */
public enum OperateResultCode {
    OK(0),SERVICE_NOT_EXISTS(-110),SERVICE_EXISTS(-109),PARAM_ERROR(-108);
    int code;
    OperateResultCode(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
