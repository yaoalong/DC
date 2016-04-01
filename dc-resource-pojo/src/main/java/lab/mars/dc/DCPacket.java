package lab.mars.dc;

import java.io.Serializable;

/**
 * Author:yaoalong.
 * Date:2016/4/1.
 * Email:yaoalong@foxmail.com
 */
public class DCPacket implements Serializable{

    private static final long serialVersionUID = -5225246793988318661L;
    private RequestPacket requestPacket;
    private ResponsePacket responsePacket;
    private boolean isFinished;
    public RequestPacket getRequestPacket() {
        return requestPacket;
    }

    public void setRequestPacket(RequestPacket requestPacket) {
        this.requestPacket = requestPacket;
    }

    public ResponsePacket getResponsePacket() {
        return responsePacket;
    }

    public void setResponsePacket(ResponsePacket responsePacket) {
        this.responsePacket = responsePacket;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }
}
