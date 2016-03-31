package lab.mars.dc.network;

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

    private LinkedList<RequestPacket> pendingQueue;

    public TcpClient() {
        setSocketChannelChannelInitializer(new PacketClientChannelInitializer(
                this));
    }

    public TcpClient(LinkedList<RequestPacket> m2mPacket) {
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
                pendingQueue.add((RequestPacket) msg);
            }

        }
        channel.writeAndFlush(msg);
        synchronized (msg) {
            while (!((RequestPacket) msg).isFinished()) {
                try {
                    msg.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        return;
    }

    public LinkedList<RequestPacket> getPendingQueue() {
        return pendingQueue;
    }

    public void setPendingQueue(LinkedList<RequestPacket> pendingQueue) {
        this.pendingQueue = pendingQueue;
    }
}