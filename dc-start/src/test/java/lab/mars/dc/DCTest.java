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
    SendThread sendThread;

    public static void main(String args[]) {
        DCTest dcTest = new DCTest();
        dcTest.sendThread = new SendThread("192.168.10.131", 2182);
        dcTest.send();

    }

    public void send() {
        DCPacket dcPacket = new DCPacket();
        RequestPacket requestPacket = new RequestPacket();
        requestPacket.setId("11133");

        LogResourceServiceImpl logResourceService = new LogResourceServiceImpl();
        logResourceService.setId(1222);
        byte[] bytes = ResourceReflection.serializeKryo(logResourceService);
        requestPacket.setResourceService(bytes);
        requestPacket.setOperateType(OperateType.SERVICE);
        dcPacket.setRequestPacket(requestPacket);
        sendThread.start();
        for(int i=0;i<2;i++){
            sendThread.send(dcPacket);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("发送成功");
    }
}
