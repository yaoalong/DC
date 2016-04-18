package lab.mars.dc;

import lab.mars.dc.exception.DCException;
import lab.mars.dc.impl.LogResourceServiceImpl;
import lab.mars.dc.reflection.ResourceReflection;
import org.junit.Test;

import static lab.mars.dc.exception.DCException.Code.*;

/**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */


public class ResourceOperateTest extends DCTestBase {


    /**
     * 更新服务资源测试
     */
    @Test
    public void testUpdateResource() {
        RequestPacket requestPacket = new RequestPacket();
        requestPacket.setId("/root");
        requestPacket.setOperateType(OperateType.UPDATE);
        LogResourceServiceImpl logResourceService = new LogResourceServiceImpl();
        logResourceService.setId(1222);
        byte[] bytes = ResourceReflection.serializeKryo(logResourceService);
        requestPacket.setResourceService(bytes);
        requestPacket.setAsyncCallback(asyncCallback);
        dc.send(requestPacket);

    }

    /**
     * 检索服务资源测试
     */
    @Test
    public void retrieveResource() {
        RequestPacket requestPacket = new RequestPacket();
        requestPacket.setId("/root");
        requestPacket.setOperateType(OperateType.RETRIEVE);
        requestPacket.setAsyncCallback(new AsyncCallback.DataCallback() {
            @Override
            public void processResult(DCException.Code code, String id, ResourceService resoureService) {
                if (OK == code) {
                    System.out.println("success");
                    for (String string : resoureService.getRelatedResource()) {
                        System.out.println("related resource:" + string);
                    }
                } else {
                    System.out.println("error" + code);
                }
            }
        });
        dc.send(requestPacket);

    }

    /**
     * 服务资源计算
     */
    @Test
    public void testCalcuateResource() {

        RequestPacket requestPacket = new RequestPacket();
        requestPacket.setId("/root");
        requestPacket.setOperateType(OperateType.SERVICE);
        requestPacket.setAsyncCallback(new AsyncCallback.ServiceCallback() {
            @Override
            public void processResult(DCException.Code code, String id, ResultDO resultDO) {
                if (OK == code) {
                    System.out.println("success");
                    if(resultDO instanceof  NameResultDO){
                        System.out.println("name:"+((NameResultDO)resultDO).getName());
                    }

                } else {
                    System.out.println("error" + code);
                }
            }
        });
        dc.send(requestPacket);
    }
}
