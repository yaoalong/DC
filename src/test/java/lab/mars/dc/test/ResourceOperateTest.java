package lab.mars.dc.test;

import lab.mars.dc.*;
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
        requestPacket.setResourceService(new LogResourceService());
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
        requestPacket.setResourceService(new LogResourceService());
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
        requestPacket.setResourceService(new LogResourceService());
        dc.send(requestPacket, asyncCallback);
    }

    /**
     * 服务资源计算
     */
    @Test
    public void testCalcuateResource() {
        RequestPacket requestPacket = new RequestPacket();
        requestPacket.setId("/ces1/ae1");
        requestPacket.setOperateType(OperateType.SERVICE);
        requestPacket.setData(new DataContent() {
        });
        ResponsePacket responsePacket = dc.send(requestPacket);
        ResultDO resultDO = responsePacket.getResult();
        //TODO 对计算结果进行处理
    }
}
