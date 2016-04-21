package lab.mars.dc;

import lab.mars.dc.exception.DCException;
import lab.mars.dc.impl.LogResourceServiceImpl;
import lab.mars.dc.reflection.ResourceReflection;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */
public class DCTestBase {

    protected Logger LOG = LoggerFactory.getLogger(getClass());
    protected DC dc = new DC();
    AsyncCallback asyncCallback = new AsyncCallback.VoidCallback() {
        @Override
        public void processResult(DCException.Code code, String id) {
            if (code == DCException.Code.OK) {
                LOG.info("success");
            } else {
                LOG.info("error" + code);
            }
        }
    };

    @Before
    public void testBefore() throws IOException, DCConfig.ConfigException {
        dc.start(new String[]{"zoo1.cfg"});
        RequestPacket requestPacket = new RequestPacket();
        requestPacket.setId("/root");
        requestPacket.setOperateType(OperateType.CREATE);
        LogResourceServiceImpl logResourceService = new LogResourceServiceImpl();
        logResourceService.setId(1111);
        logResourceService.setRelatedResources(new String[]{"/cse/alle"});
        byte[] bytes = ResourceReflection.serializeKryo(logResourceService);
        requestPacket.setResourceService(bytes);
        requestPacket.setAsyncCallback(asyncCallback);
        dc.send(requestPacket);
    }

    @After
    public void shutDown() throws Exception {
        RequestPacket requestPacket = new RequestPacket();
        requestPacket.setId("/root");
        requestPacket.setOperateType(OperateType.DELETE);
        requestPacket.setAsyncCallback(asyncCallback);
        dc.send(requestPacket);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dc.shutDown();
    }
}
