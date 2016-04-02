package lab.mars.dc.test;

import lab.mars.dc.reflection.ResourceReflection;

/**
 * Author:yaoalong.
 * Date:2016/4/2.
 * Email:yaoalong@foxmail.com
 */
public class Test {


    @org.junit.Test
    public void test() {

        Woman woman = new Woman();
        woman.setId("1223");
        byte[] bytes = ResourceReflection.serializeKryo(woman);

        System.out.println("length:" + bytes.length);

        Person person = (Person) ResourceReflection.deserializeKryo(bytes);
        if (person instanceof Woman) {
            woman.say();
        }
        if (person instanceof Man) {
            person.say();
        }
    }
}
