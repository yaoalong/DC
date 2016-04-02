package lab.mars.dc;

import lab.mars.dc.impl.LogResourceServiceImpl;
import lab.mars.dc.network.SendThread;
import lab.mars.dc.reflection.ResourceReflection;

/**
 * Author:yaoalong.
 * Date:2016/4/1.
 * Email:yaoalong@foxmail.com
 */
public class DCTest {
    SendThread sendThread=new SendThread();
    public void send(){
         DCPacket dcPacket=new DCPacket();
        RequestPacket requestPacket=new RequestPacket();
        requestPacket.setId("11133");
        requestPacket.setAsyncCallback(new AsyncCallback() {
            /**
			 *
			 */
			private static final long serialVersionUID = -7737777178136621457L;

			@Override
            public void processResult(OperateResultCode operateResultCode, String id, ResultDO resultDO, ResourceService resourceService) {
                System.out.println("ha");
            }
        });
        LogResourceServiceImpl logResourceService=new LogResourceServiceImpl();
        logResourceService.setId(1222);
        byte[] bytes= ResourceReflection.serializeKryo(logResourceService);
        requestPacket.setResourceService(bytes);
        requestPacket.setOperateType(OperateType.SERVICE);
       dcPacket.setRequestPacket(requestPacket);
        sendThread.start();
        sendThread.send(dcPacket);
        System.out.println("发送成功");
    }
    public static void main(String args[]){
        DCTest dcTest=new DCTest();
        dcTest.send();
    }
}
