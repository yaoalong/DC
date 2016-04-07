package lab.mars.dc.network;

import lab.mars.dc.DCPacket;
import lab.mars.dc.ResponsePacket;
import lab.mars.dc.exception.DCException;
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

    private final LinkedList<DCPacket> pendingQueue;
    private SendThread sendThread;

    public TcpClient() {
        this(new LinkedList<>());

    }

    public TcpClient(LinkedList<DCPacket> m2mPacket) {
        this.pendingQueue = m2mPacket;
        setSocketChannelChannelInitializer(new PacketClientChannelInitializer(
                this));

    }

    /**
     * @param msg
     * @throws Exception
     */
    public void write(Object msg) throws Exception {
        if (channel == null) {
            try {
                reentrantLock.lock();
                condition.await();
            } catch (InterruptedException e) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("write error:", e);
                }
            } finally {
                reentrantLock.unlock();
            }
        }
        if (isSuccess == false) {
            throw new Exception("Not successfully connect to the server");
        }
        if (pendingQueue != null) {
            synchronized (pendingQueue) {
                pendingQueue.add((DCPacket) msg);
            }
        }
        if (!channel.isActive()) {
            throw new Exception("channel is closed");
        }
        channel.writeAndFlush(msg);
        System.out.println("发送了");
        synchronized (msg) {
            if (!((DCPacket) msg).isFinished()) {
                try{
                    msg.wait(3000);
                }catch (InterruptedException e){

                }
            }
        }
        if (!((DCPacket) msg).isFinished()) {
            synchronized (pendingQueue) {
                pendingQueue.remove();
            }
            ResponsePacket responsePacket=new ResponsePacket();
            responsePacket.setCode(DCException.Code.SYSTEM_ERROR);
            ((DCPacket)msg).setResponsePacket(responsePacket);
        }
        System.out.println("OK");
        if (sendThread != null) {
            sendThread.readResponse((DCPacket) msg);
        }

    }

    public LinkedList<DCPacket> getPendingQueue() {
        return pendingQueue;
    }


    public void setSendThread(SendThread sendThread) {
        this.sendThread = sendThread;
    }
}
