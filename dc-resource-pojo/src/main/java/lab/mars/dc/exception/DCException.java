package lab.mars.dc.exception;

/**
 * Author:yaoalong.
 * Date:2016/4/2.
 * Email:yaoalong@foxmail.com
 */
public class DCException extends Exception {
    private static final long serialVersionUID = 8627527604343801135L;
    private Code code;
    private String id;

    public DCException(Code code) {
        this.code = code;
    }


    public DCException(Code code, String id) {
        this.code = code;
        this.id = id;
    }

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public enum Code {
         OK(0), RESOURCE_EXISTS(-110), RESOURCE_NOT_EXISTS(-109), PARAM_ERROR(-108),OPERATE_TYPE_NOT_SUPPORT(-107);
        private final int code;

        Code(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}
