package lab.mars.dc.test;

import lab.mars.dc.DataContent;
import lab.mars.dc.OperateType;
import lab.mars.dc.RequestPacket;
import org.junit.Test;

/**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */
public class ResourceOperateTest extends  DCTestBase{


    @Test
    public void testAddResource(){
        RequestPacket requestPacket=new RequestPacket();
        requestPacket.setId("/ces1/ae1");
        requestPacket.setOperateType(OperateType.CREATE);
        requestPacket.setResourceService(new LogResourceService());
        dc.send(requestPacket,asyncCallback);
    }

    @Test
    public void testDeleteResource(){
        RequestPacket requestPacket=new RequestPacket();
        requestPacket.setId("/ces1/ae1");
        requestPacket.setOperateType(OperateType.DELETE);
        dc.send(requestPacket,asyncCallback);
    }
    @Test
    public void testUpdateResource(){
        RequestPacket requestPacket=new RequestPacket();
        requestPacket.setId("/ces1/ae1");
        requestPacket.setOperateType(OperateType.UPDATE);
        requestPacket.setResourceService(new LogResourceService());
        dc.send(requestPacket,asyncCallback);
    }
    @Test
    public void retrieveResource(){
        RequestPacket requestPacket=new RequestPacket();
        requestPacket.setId("/ces1/ae1");
        requestPacket.setOperateType(OperateType.RETRIEVE);
        requestPacket.setResourceService(new LogResourceService());
        dc.send(requestPacket,asyncCallback);
    }
    @Test
    public void testCalcuateResource(){
        RequestPacket requestPacket=new RequestPacket();
        requestPacket.setId("/ces1/ae1");
        requestPacket.setOperateType(OperateType.SERVICE);
        requestPacket.setData(new DataContent() {
        });
        dc.send(requestPacket,asyncCallback);
    }
}
