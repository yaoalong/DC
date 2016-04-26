package lab.mars.dc.impl;

import lab.mars.dc.DataContent;
import lab.mars.dc.NameResultDO;
import lab.mars.dc.ResourceService;
import lab.mars.dc.ResultDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author:yaoalong.
 * Date:2016/4/2.
 * Email:yaoalong@foxmail.com
 */
public class LogResourceServiceImpl implements ResourceService {
    private static final Logger LOG= LoggerFactory.getLogger(LogResourceServiceImpl.class);

    public int id;
    private String[] relatedServers;

    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    @Override
    public ResultDO service(DataContent dataContent) {
        NameResultDO nameResultDO = new NameResultDO();
        nameResultDO.setName("id");
        return nameResultDO;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public String[] getRelatedResources() {
        return relatedServers;
    }

    @Override
    public void setRelatedResources(String[] relatedResources) {
        this.relatedServers = relatedResources;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
