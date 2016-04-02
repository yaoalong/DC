package lab.mars.dc.test;

/**
 * Author:yaoalong.
 * Date:2016/4/2.
 * Email:yaoalong@foxmail.com
 */
public class Man implements Person {

    public String name;

    @Override
    public void say() {
        System.out.println("Man");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
