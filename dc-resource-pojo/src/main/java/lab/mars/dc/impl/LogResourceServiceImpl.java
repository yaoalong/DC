package lab.mars.dc.impl;

import lab.mars.dc.DataContent;
import lab.mars.dc.NameResultDO;
import lab.mars.dc.ResourceService;
import lab.mars.dc.ResultDO;

/**
 * Author:yaoalong.
 * Date:2016/4/2.
 * Email:yaoalong@foxmail.com
 */
public class LogResourceServiceImpl extends ResourceService {

    public int id;

    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    @Override
    public ResultDO service(DataContent dataContent) {
        System.out.println("my Id:" + id);
        NameResultDO nameResultDO = new NameResultDO();
        nameResultDO.setName("id+" + "long");
        return nameResultDO;
    }

    @Override
    public void shutdown() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
