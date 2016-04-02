package lab.mars.dc.network;

import lab.mars.dc.DCPacket;
import lab.mars.dc.RequestPacket;
import lab.mars.dc.network.initializer.PacketClientChannelInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public class TcpClient extends TcpClientNetwork {

    private static final Logger LOG = LoggerFactory.getLogger(TcpClient.class);

    private LinkedList<DCPacket> pendingQueue;
    private SendThread sendThread;
    public TcpClient() {
    	this.pendingQueue=new LinkedList<>();
        setSocketChannelChannelInitializer(new PacketClientChannelInitializer(
                this));
    }
    public TcpClient(LinkedList<DCPacket> m2mPacket) {
        this();
        this.pendingQueue = m2mPacket;

    }

    public void write(Object msg) {
        while (channel == null) {
            try {
                reentrantLock.lock();
                condition.await();
            } catch (InterruptedException e) {
                LOG.info("write error:{}", e);
                e.printStackTrace();
            } finally {
                reentrantLock.unlock();
            }
        }
        if (pendingQueue != null) {
            synchronized (pendingQueue) {
                pendingQueue.add((DCPacket) msg);
            }

        }
        channel.writeAndFlush(msg);
        synchronized (msg) {
            while (!((DCPacket) msg).isFinished()) {
                try {
                    msg.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if(sendThread!=null){
            sendThread.readResponse((DCPacket)msg);
        }
    }

    public LinkedList<DCPacket> getPendingQueue() {
        return pendingQueue;
    }

    public SendThread getSendThread() {
        return sendThread;
    }

    public void setSendThread(SendThread sendThread) {
        this.sendThread = sendThread;
    }
}
