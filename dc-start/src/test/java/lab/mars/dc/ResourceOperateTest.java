package lab.mars.dc;

import lab.mars.dc.exception.DCException;
import lab.mars.dc.impl.LogResourceServiceImpl;
import lab.mars.dc.reflection.ResourceReflection;

import org.junit.Test;

/**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */


public class ResourceOperateTest extends DCTestBase {

    /**
     * 添加服务资源
     */
    @Test
    public void testAddResource() {
        RequestPacket requestPacket = new RequestPacket();
        requestPacket.setId("/ces1/ae1");
        requestPacket.setOperateType(OperateType.CREATE);
        LogResourceServiceImpl logResourceService = new LogResourceServiceImpl();
        logResourceService.setId(1222);
        byte[] bytes = ResourceReflection.serializeKryo(logResourceService);
        requestPacket.setResourceService(bytes);
        dc.send(requestPacket, asyncCallback);
    }

    /**
     * 删除服务资源测试
     */
    @Test
    public void testDeleteResource() {
        RequestPacket requestPacket = new RequestPacket();
        requestPacket.setId("/ces1/ae1");
        requestPacket.setOperateType(OperateType.DELETE);
        dc.send(requestPacket, asyncCallback);
    }

    /**
     * 更新服务资源测试
     */
    @Test
    public void testUpdateResource() {
        RequestPacket requestPacket = new RequestPacket();
        requestPacket.setId("/ces1/ae1");
        requestPacket.setOperateType(OperateType.UPDATE);
        LogResourceServiceImpl logResourceService = new LogResourceServiceImpl();
        logResourceService.setId(1222);
        byte[] bytes = ResourceReflection.serializeKryo(logResourceService);
        requestPacket.setResourceService(bytes);
        dc.send(requestPacket, asyncCallback);
        
    }

    /**
     * 检索服务资源测试
     */
    @Test
    public void retrieveResource() {
        RequestPacket requestPacket = new RequestPacket();
        requestPacket.setId("/ces1/ae1");
        requestPacket.setOperateType(OperateType.RETRIEVE);
        dc.send(requestPacket, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(DCException.Code code, String id, ResourceService resoureService) {
                if(DCException.Code.OK==code){
                    System.out.println("success");
                }
                else{
                    System.out.println("error"+code);
                }
            }
        });
      
    }

    /**
     * 服务资源计算
     */
    @Test
    public void testCalcuateResource() {

        //TODO 对计算结果进行处理
    }
}
