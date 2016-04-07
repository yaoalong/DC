package lab.mars.dc;

import org.junit.Test;

/**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */
public class DCSendPacketTest extends  DCTestBase{

    /**
     * 异步发送请求
     */
    @Test
    public void testAsynchronousSend(){
        RequestPacket requestPacket=new RequestPacket();
        requestPacket.setId("/ces1/ae2");
        requestPacket.setOperateType(OperateType.CREATE);
        requestPacket.setAsyncCallback(asyncCallback);
        dc.send(requestPacket);
    }
}
