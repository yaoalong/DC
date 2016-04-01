package lab.mars.dc.network;

import lab.mars.dc.RequestPacket;
import lab.mars.dc.ResponsePacket;

import java.util.LinkedList;

/**
 * Author:yaoalong.
 * Date:2016/4/1.
 * Email:yaoalong@foxmail.com
 */
public class SendThread extends Thread {


    private final LinkedList<RequestPacket> pendingQueue = new LinkedList<RequestPacket>();
    private final LinkedList<RequestPacket> outgoingQueue = new LinkedList<RequestPacket>();
    private TcpClient tcpClient = new TcpClient();

    public SendThread() {
        tcpClient.connectionOne("", 2);
    }

    public void send(RequestPacket requestPacket) {
        synchronized (outgoingQueue) {
            outgoingQueue.add(requestPacket);
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (outgoingQueue) {
                if (outgoingQueue.isEmpty()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                RequestPacket requestPacket = outgoingQueue.getFirst();
                tcpClient.write(requestPacket);
                synchronized (pendingQueue) {
                    pendingQueue.add(requestPacket);
                }
            }
        }
    }
    public void readResponse(ResponsePacket responsePacket){

    }
}
