package lab.mars.dc.server;

import io.netty.channel.Channel;
import lab.mars.dc.*;
import lab.mars.dc.exception.DCException;
import lab.mars.dc.exception.DCException.Code;
import lab.mars.dc.persistence.DCDatabaseService;
import lab.mars.dc.reflection.ResourceReflection;
import lab.mars.dc.server.RangeDO;
import lab.mars.dc.server.ResourceServiceDO;
import lab.mars.dc.util.MD5Hash;

import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
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
    private TreeMap<Long, RangeDO> endRangeDOMap = new TreeMap<Long, RangeDO>();

    public DCProcessor(DCDatabaseService dcDatabaseService) {
        this.dcDatabaseService = dcDatabaseService;
    }


    /**
     * 执行逻辑处理
     * 1.如果是创建服务资源的话，那么首先会创建,然后在内存中加载服务
     * 2.如果是更新服务资源，首先会关闭内存中的服务，然后更新数据库中的服务，再在内存中重新加载
     * 3.如果是删除操作，关闭内存中的服务，删除数据库中的服务。
     * 4.调用服务资源
     *
     * @param dcPacket
     * @param channel
     */
    public DCPacket receiveMessage(DCPacket dcPacket, Channel channel) {
        Code code = OK;
        ResponsePacket responsePacket = new ResponsePacket();
        RequestPacket requestPacket = dcPacket.getRequestPacket();

        if (requestPacket.getOperateType().getCode() == OperateType.CREATE.getCode()) {
            ResourceServiceDO resourceServiceDO = new ResourceServiceDO();
            resourceServiceDO.setData(requestPacket.getResourceService());
            resourceServiceDO.setId(requestPacket.getId());
            try {
                dcDatabaseService.create(resourceServiceDO);
                ResourceService resourceService = (ResourceService) ResourceReflection.deserializeKryo(requestPacket.getResourceService());
                resourceService.start();
                resourceServices.put(requestPacket.getId(), resourceService);
            } catch (DCException e) {
                code = e.getCode();
            } catch (Exception e) {
                code = Code.SYSTEM_ERROR;
            }
        } else if (requestPacket.getOperateType().getCode() == OperateType.UPDATE.getCode()) {
            byte[] service = ResourceReflection.serializeKryo(requestPacket.getResourceService());
            ResourceServiceDO resourceServiceDO = new ResourceServiceDO();
            resourceServiceDO.setId(requestPacket.getId());
            resourceServiceDO.setData(service);
            try {
                dcDatabaseService.update(requestPacket.getId(), resourceServiceDO);
                ResourceService resourceService = (ResourceService) ResourceReflection.deserializeKryo(requestPacket.getResourceService());
                if (resourceServices.containsKey(requestPacket.getId())) {
                    resourceServices.get(requestPacket.getId()).shutdown();
                    resourceService.start();
                }
                resourceServices.put(requestPacket.getId(), resourceService);
            } catch (DCException e) {
                code = e.getCode();
            } catch (Exception e) {
                code = Code.SYSTEM_ERROR;
            }

        } else if (requestPacket.getOperateType().getCode() == OperateType.DELETE.getCode()) {
            try {
                dcDatabaseService.delete(requestPacket.getId());
            } catch (DCException e) {
                code = e.getCode();
            }
            if (resourceServices.get(requestPacket.getId()) != null) {
                resourceServices.get(requestPacket.getId()).shutdown();
                resourceServices.remove(requestPacket.getId());
            }
        } else if (requestPacket.getOperateType().getCode() == OperateType.RETRIEVE.getCode()) {
            try {
                ResourceServiceDO resourceServiceDO = dcDatabaseService.retrieve(requestPacket.getId());
                if (resourceServiceDO != null) {
                    responsePacket.setResourceService(resourceServiceDO.getData());
                }

            } catch (DCException e) {
                code = e.getCode();
            } catch (Exception e) {
                code = Code.SYSTEM_ERROR;
            }

        } else if (requestPacket.getOperateType().getCode() == OperateType.SERVICE.getCode()) {
            if (!resourceServices.containsKey(requestPacket.getId())) {
                try {
                    ResourceServiceDO resourceServiceDO = dcDatabaseService.retrieve(requestPacket.getId());
                    if (resourceServiceDO != null) {
                        ResourceService resourceService = (ResourceService) ResourceReflection.deserializeKryo(resourceServiceDO.getData());
                        ResultDO resultDO = resourceService.service(null);
                        responsePacket.setResult(ResourceReflection.serializeKryo(resultDO));
                    } else {
                        code = Code.RESOURCE_NOT_EXISTS;
                    }
                } catch (DCException e) {
                    code = e.getCode();
                } catch (Exception e) {
                    code = Code.SYSTEM_ERROR;
                }
            } else {
                ResultDO resultDO = resourceServices.get(requestPacket.getId()).service(null);
                responsePacket.setResult(ResourceReflection.serializeKryo(resultDO));
            }
        } else {
            code = Code.OPERATE_TYPE_NOT_SUPPORT;
        }
        responsePacket.setCode(code);
        DCPacket result = new DCPacket();
        result.setResponsePacket(responsePacket);
        if (channel != null) {
            channel.writeAndFlush(result);
        }
        return result;
    }

    public void update(List<RangeDO> rangeDOs) {
        endRangeDOMap.clear();
        for (RangeDO rangeDO : rangeDOs) {
            endRangeDOMap.put(rangeDO.getEnd(), rangeDO);
        }
        Iterator<String> iterator = resourceServices.keySet().iterator();
        while (iterator.hasNext()) {
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (!judgeIsHandle(MD5Hash.md5HashingAlg(key))) {
                    ResourceService resourceService = resourceServices.get(key);
                    resourceService.shutdown();
                    iterator.remove();
                }

            }
        }

    }

    private boolean judgeIsHandle(long zxid) {
        SortedMap<Long, RangeDO> tmap = this.endRangeDOMap.tailMap(zxid);

        Long position = (tmap.isEmpty()) ? this.endRangeDOMap.firstKey() : tmap
                .firstKey();
        RangeDO rangeDO = endRangeDOMap.get(position);
        if (rangeDO != null && rangeDO.getStart() < zxid) {
            return true;
        }
        return false;
    }

    public void close() {
        dcDatabaseService.close();
    }


}
