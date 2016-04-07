package lab.mars.dc;

import lab.mars.dc.exception.DCException;
import lab.mars.dc.impl.LogResourceServiceImpl;
import lab.mars.dc.reflection.ResourceReflection;
import org.junit.Test;

import java.io.IOException;

/**
 * Author:yaoalong.
 * Date:2016/4/1.
 * Email:yaoalong@foxmail.com
 */
public class DCTest {
    /**
     * 168s 10万条服务
     * @return
     */
    public static RequestPacket generateDCRequestPacket() {
        RequestPacket requestPacket = new RequestPacket();
        requestPacket.setId("11133");

        LogResourceServiceImpl logResourceService = new LogResourceServiceImpl();
        logResourceService.setId(1222);
        byte[] bytes = ResourceReflection.serializeKryo(logResourceService);
        requestPacket.setResourceService(bytes);
        requestPacket.setOperateType(OperateType.SERVICE);
        return requestPacket;
    }

    @Test
    public void test() throws IOException, DCConfig.ConfigException {
        DC dc = new DC();
        dc.start(new String[]{"zoo1.cfg"});
        int number = 100000;
        long start = System.nanoTime();

        for (int i = 0; i < number; i++) {
            dc.send(generateDCRequestPacket(), new AsyncCallback.ServiceCallback() {
                @Override
                public void processResult(DCException.Code code, String id, ResultDO resultDO) {
                    if (resultDO instanceof NameResultDO) {
                        // System.out.println(((NameResultDO) resultDO).getName());
                    }
                    // System.out.println("id:" + id + ":code:" + code.getCode() + ":resultDO:" + resultDO.toString());
                    Util.atomicInteger.getAndIncrement();
                }
            });
        }
        while (Util.atomicInteger.get() != number) {
            System.out.println(Util.atomicInteger.get());
        }
        System.out.println("cost time:" + (System.nanoTime() - start));
        dc.shutDown();
    }
}
