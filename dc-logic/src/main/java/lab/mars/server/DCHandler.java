package lab.mars.server;

import io.netty.channel.Channel;
import lab.mars.dc.DCPacket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author:yaoalong.
 * Date:2016/4/7.
 * Email:yaoalong@foxmail.com
 */

/**
 * DC处理器，进行线程管理
 */
public class DCHandler {

    private static final ExecutorService executor=Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors());
    private DCProcessor dcProcessor;
    public DCHandler(DCProcessor dcProcessor){
        this.dcProcessor=dcProcessor;
    }
    public void receiveMessage(DCPacket dcPacket, Channel channel) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                 dcProcessor.receiveMessage(dcPacket, channel);
            }
        });
    }
    public void close(){

    }
}
