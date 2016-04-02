package lab.mars.dc.test;

/**
 * Author:yaoalong.
 * Date:2016/4/2.
 * Email:yaoalong@foxmail.com
 */
public class Woman implements Person {

    public String id;

    @Override
    public void say() {
        System.out.println("woman"+id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
