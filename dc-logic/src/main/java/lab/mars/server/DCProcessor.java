package lab.mars.server;

import io.netty.channel.Channel;
import lab.mars.dc.*;
import lab.mars.dc.exception.DCException;
import lab.mars.dc.exception.DCException.Code;
import lab.mars.dc.persistence.DCDatabaseService;
import lab.mars.dc.reflection.ResourceReflection;
import lab.mars.dc.server.ResourceServiceDO;

import java.util.concurrent.ConcurrentHashMap;

import static lab.mars.dc.exception.DCException.Code.OK;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 * DC的处理逻辑
 */

public class DCProcessor {

    private ConcurrentHashMap<String, ResourceService> resourceServices = new ConcurrentHashMap<>();
    private DCDatabaseService dcDatabaseService;

    public DCProcessor(DCDatabaseService dcDatabaseService) {
        this.dcDatabaseService = dcDatabaseService;
    }

    public void receiveMessage(DCPacket dcPacket, Channel channel) {
        Code code = OK;
        ResponsePacket responsePacket = new ResponsePacket();
        if (dcPacket == null || dcPacket.getRequestPacket() == null) {
            code = Code.PARAM_ERROR;
        } else {
            RequestPacket requestPacket = dcPacket.getRequestPacket();

            if (requestPacket.getOperateType().getCode() == OperateType.CREATE.getCode()) {
                ResourceServiceDO resourceServiceDO = new ResourceServiceDO();
                resourceServiceDO.setData(requestPacket.getResourceService());
                resourceServiceDO.setId(requestPacket.getId());
                try {
                    dcDatabaseService.create(resourceServiceDO);
                } catch (DCException e) {
                    code = e.getCode();
                }

                ResourceService resourceService = (ResourceService) ResourceReflection.deserializeKryo(requestPacket.getResourceService());
                resourceService.start();
                resourceServices.put(requestPacket.getId(), resourceService);
            } else if (requestPacket.getOperateType().getCode() == OperateType.UPDATE.getCode()) {
                byte[] service = ResourceReflection.serializeKryo(requestPacket.getResourceService());
                ResourceServiceDO resourceServiceDO = new ResourceServiceDO();
                resourceServiceDO.setId(requestPacket.getId());
                resourceServiceDO.setData(service);
                try {
                    dcDatabaseService.update(requestPacket.getId(), resourceServiceDO);
                } catch (DCException e) {
                    code = e.getCode();
                }
                ResourceService resourceService = (ResourceService) ResourceReflection.deserializeKryo(requestPacket.getResourceService());
                if (resourceServices.contains(requestPacket.getId())) {
                    resourceServices.get(requestPacket.getId()).shutdown();

                    resourceService.start();

                }
                resourceServices.put(requestPacket.getId(), resourceService);
            } else if (requestPacket.getOperateType().getCode() == OperateType.DELETE.getCode()) {
                try {
                    dcDatabaseService.delete(requestPacket.getId());
                } catch (DCException e) {
                    code = e.getCode();
                }
                if (resourceServices.contains(requestPacket.getId())) {
                    resourceServices.get(requestPacket.getId()).shutdown();
                }
            } else if (requestPacket.getOperateType().getCode() == OperateType.RETRIEVE.getCode()) {
                try {
                    ResourceServiceDO resourceServiceDO = dcDatabaseService.retrieve(requestPacket.getId());
                    if (resourceServiceDO != null) {
                        responsePacket.setResourceService(ResourceReflection.serializeKryo(resourceServiceDO));
                    }

                } catch (DCException e) {
                    code = e.getCode();
                }

            } else if (requestPacket.getOperateType().getCode() == OperateType.SERVICE.getCode()) {
                if (!resourceServices.containsKey(requestPacket.getId())) {
                    try {
                        ResourceServiceDO resourceServiceDO = dcDatabaseService.retrieve(requestPacket.getId());
                        if (resourceServiceDO != null) {
                            ResourceService resourceService = (ResourceService) ResourceReflection.deserializeKryo(resourceServiceDO.getData());
                            ResultDO resultDO = resourceService.service(null);
                            responsePacket.setResult(ResourceReflection.serializeKryo(resultDO));
                        }
                        else{
                            code=Code.RESOURCE_NOT_EXISTS;
                        }
                    } catch (DCException e) {
                        code = e.getCode();
                    }

                } else {
                   ResultDO resultDO= resourceServices.get(requestPacket.getId()).service(null);
                    responsePacket.setResult(ResourceReflection.serializeKryo(resultDO));
                }
            } else {
                code = Code.OPERATE_TYPE_NOT_SUPPORT;
            }
        }

        responsePacket.setCode(code);
        DCPacket result = new DCPacket();
        result.setResponsePacket(responsePacket);
        channel.writeAndFlush(result);
    }

    public void close() {
        dcDatabaseService.close();
    }

}
