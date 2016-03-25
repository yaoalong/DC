package lab.mars.dc.test;

import lab.mars.dc.*;
import org.junit.Test;

/**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */
public class DCSendPacketTest extends  DCTestBase{

    /**
     * 同步发送请求
     */
    @Test
    public void testSynchronizeSend(){
        RequestPacket requestPacket=new RequestPacket();
        requestPacket.setId("/ces1/ae1");
        requestPacket.setOperateType(OperateType.CREATE);
        requestPacket.setResourceService(new LogResourceService());
        ResponsePacket responsePacket= dc.send(requestPacket);
        if(responsePacket.getOperateResultCode().getCode()== OperateResultCode.OK.getCode()){
            //请求正常
        }
        else{
            //处理异常
        }
    }

    /**
     * 异步发送请求
     */
    @Test
    public void testAsynchronousSend(){
        RequestPacket requestPacket=new RequestPacket();
        requestPacket.setId("/ces1/ae2");
        requestPacket.setOperateType(OperateType.CREATE);
        requestPacket.setResourceService(new LogResourceService());

        dc.send(requestPacket,asyncCallback);
    }
}
