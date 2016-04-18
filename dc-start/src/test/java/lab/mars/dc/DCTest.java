package lab.mars.dc;

import lab.mars.dc.exception.DCException;
import lab.mars.dc.impl.LogResourceServiceImpl;
import lab.mars.dc.reflection.ResourceReflection;

import java.io.IOException;
import java.util.Random;

/**
 * Author:yaoalong.
 * Date:2016/4/1.
 * Email:yaoalong@foxmail.com
 */
public class DCTest {
    /**
     * 168s 10万条服务
     *
     * @return
     */
    static Random random = new Random();

    public static RequestPacket generateDCRequestPacket() {
        RequestPacket requestPacket = new RequestPacket();
        requestPacket.setId("11133" + (random.nextLong()));

        LogResourceServiceImpl logResourceService = new LogResourceServiceImpl();
        logResourceService.setId(1222);
        byte[] bytes = ResourceReflection.serializeKryo(logResourceService);
        requestPacket.setResourceService(bytes);
        requestPacket.setOperateType(OperateType.CREATE);
        requestPacket.setAsyncCallback(new AsyncCallback.VoidCallback() {
            @Override
            public void processResult(DCException.Code code, String id) {
                Util.atomicInteger.getAndIncrement();
            }
        });
        return requestPacket;
    }

    public static void main(String args[]) throws IOException, DCConfig.ConfigException {
        DC dc = new DC();
        dc.start(args);
        int number = 10000;
        long start = System.nanoTime();

        for (int i = 0; i < number; i++) {
            dc.send(generateDCRequestPacket());
        }
        while (Util.atomicInteger.get() != number) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Util.atomicInteger.get());
        }
        System.out.println("cost time:" + (System.nanoTime() - start));
        dc.shutDown();
    }

    public void test() throws IOException, DCConfig.ConfigException {
        DC dc = new DC();
        dc.start(new String[]{"zoo1.cfg"});
        int number = 10000;
        long start = System.nanoTime();

        for (int i = 0; i < number; i++) {
            dc.send(generateDCRequestPacket());
        }
        while (Util.atomicInteger.get() != number) {
            System.out.println(Util.atomicInteger.get());
        }
        System.out.println("cost time:" + (System.nanoTime() - start));
        dc.shutDown();
    }
}
