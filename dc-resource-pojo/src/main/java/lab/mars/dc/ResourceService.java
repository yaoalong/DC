package lab.mars.dc;

import java.io.Serializable;

/**
 * Author:yaoalong.
 * Date:2016/3/25.
 * Email:yaoalong@foxmail.com
 */
public abstract class ResourceService implements Serializable {

    public String[] relatedResource;

    public abstract void init();


    public abstract void start();

    public abstract ResultDO service(DataContent dataContent);

    public abstract void shutdown();

    public String[] getRelatedResource() {
        return relatedResource;
    }

    public void setRelatedResource(String[] relatedResource) {
        this.relatedResource = relatedResource;
    }
}
