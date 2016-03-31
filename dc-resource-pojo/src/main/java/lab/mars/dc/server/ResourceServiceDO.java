package lab.mars.dc.server;

import java.io.Serializable;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public class ResourceServiceDO implements Serializable{
    private static final long serialVersionUID = 7583850323143877845L;

    public  String id;
    public byte[] data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
