package lab.mars.dc;

/**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */
public class DC {
    /**
     * 发送数据包的同步接口
     * @param requestPacket
     * @return
     */
    public ResponsePacket send(RequestPacket requestPacket) {
        return null;
    }

    /**
     * 发送数据包的异步接口
     * @param requestPacket
     * @param asyncCallback
     */
    public void send(RequestPacket requestPacket, AsyncCallback asyncCallback) {

    }
    /**
     * 从配置文件中启动
     * @param args
     */
    public  void start(String args[]){

    }

    public void shutDown(){

    }
}
