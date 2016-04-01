package lab.mars.dc.network;

import lab.mars.dc.DCPacket;
import lab.mars.dc.RequestPacket;
import lab.mars.dc.ResponsePacket;

import java.util.LinkedList;

/**
 * Author:yaoalong.
 * Date:2016/4/1.
 * Email:yaoalong@foxmail.com
 */
public class SendThread extends Thread {


    private final LinkedList<DCPacket> pendingQueue = new LinkedList<DCPacket>();
    private final LinkedList<DCPacket> outgoingQueue = new LinkedList<DCPacket>();
    private TcpClient tcpClient = new TcpClient();

    public SendThread() {
        tcpClient.connectionOne("192.168.10.131", 2181);
    }

    public void send(DCPacket dcPacket) {
        synchronized (outgoingQueue) {
            outgoingQueue.add(dcPacket);
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
                DCPacket requestPacket = outgoingQueue.getFirst();
                tcpClient.write(requestPacket);
                synchronized (pendingQueue) {
                    pendingQueue.add(requestPacket);
                }
            }
        }
    }
    public void readResponse(DCPacket dcPacket){
            synchronized (pendingQueue){
                DCPacket dcPacket1=pendingQueue.remove();
                System.out.println("kankan"+dcPacket1.getRequestPacket().getAsyncCallback()==null);
                if(dcPacket1.getRequestPacket().getAsyncCallback()!=null){
                    System.out.println("不为空");
                    dcPacket1.getRequestPacket().getAsyncCallback().processResult(null,null,null,null);
                }

            }
    }
}
