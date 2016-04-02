package lag.mars.server;

import io.netty.channel.Channel;
import lab.mars.dc.DCPacket;
import lab.mars.dc.OperateType;
import lab.mars.dc.RequestPacket;
import lab.mars.dc.ResourceService;
import lab.mars.dc.persistence.DCDatabaseInterface;
import lab.mars.dc.reflection.ResourceReflection;
import lab.mars.dc.server.ResourceServiceDO;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public class DCDatabase {

    private ConcurrentHashMap<String, ResourceService> resourceServices = new ConcurrentHashMap<>();
    private DCDatabaseInterface dcDatabaseInterface;

    public DCDatabase(DCDatabaseInterface dcDatabaseInterface) {
        this.dcDatabaseInterface = dcDatabaseInterface;
    }

    public void receiveMessage(DCPacket dcPacket, Channel channel) {
        System.out.println("开始处理");
        RequestPacket requestPacket = dcPacket.getRequestPacket();
        if (requestPacket.getOperateType().getCode() == OperateType.CREATE.getCode()) {
            ResourceServiceDO resourceServiceDO = new ResourceServiceDO();
            resourceServiceDO.setData(requestPacket.getResourceService());
            resourceServiceDO.setId(requestPacket.getId());
            dcDatabaseInterface.create(resourceServiceDO);
            ResourceService resourceService = (ResourceService) ResourceReflection.deserializeKryo(requestPacket.getResourceService());
            resourceService.start();
            resourceServices.put(requestPacket.getId(), resourceService);
        } else if (requestPacket.getOperateType().getCode() == OperateType.UPDATE.getCode()) {
            byte[] service = ResourceReflection.serializeKryo(requestPacket.getResourceService());
            ResourceServiceDO resourceServiceDO = new ResourceServiceDO();
            resourceServiceDO.setId(requestPacket.getId());
            resourceServiceDO.setData(service);
            dcDatabaseInterface.update(requestPacket.getId(), resourceServiceDO);
            ResourceService resourceService = (ResourceService) ResourceReflection.deserializeKryo(requestPacket.getResourceService());
            if (resourceServices.contains(requestPacket.getId())) {
                resourceServices.get(requestPacket.getId()).shutdown();

                resourceService.start();

            }
            resourceServices.put(requestPacket.getId(), resourceService);
        } else if (requestPacket.getOperateType().getCode() == OperateType.DELETE.getCode()) {
            dcDatabaseInterface.delete(requestPacket.getId());
            if (resourceServices.contains(requestPacket.getId())) {
                resourceServices.get(requestPacket.getId()).shutdown();
            }
        } else if (requestPacket.getOperateType().getCode() == OperateType.RETRIEVE.getCode()) {
            dcDatabaseInterface.retrieve(requestPacket.getId());
        }
        else if(requestPacket.getOperateType().getCode()==OperateType.SERVICE.getCode()){//TODO 需要加入错误
            System.out.println("执行服务");
            if(!resourceServices.containsKey(requestPacket.getId())){
                 ResourceServiceDO resourceServiceDO=dcDatabaseInterface.retrieve(requestPacket.getId());
                 if(resourceServiceDO!=null){
                     ResourceService resourceService=(ResourceService)ResourceReflection.deserializeKryo(resourceServiceDO.getData());

                     resourceService.service(null);
                 }
            }
        }
        channel.writeAndFlush(new DCPacket());
        System.out.println("OKI");
    }

}
