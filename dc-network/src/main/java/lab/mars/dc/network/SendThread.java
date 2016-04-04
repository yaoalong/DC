package lab.mars.dc.network;

import lab.mars.dc.*;
import lab.mars.dc.reflection.ResourceReflection;

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

    public SendThread(String serverIp, Integer port) {
        tcpClient.connectionOne(serverIp, port);
        tcpClient.setSendThread(this);
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
                while (outgoingQueue.isEmpty()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                DCPacket requestPacket = outgoingQueue.remove();
                synchronized (pendingQueue) {
                    pendingQueue.add(requestPacket);
                }
                tcpClient.write(requestPacket);
            }
        }
    }

    public void readResponse(DCPacket dcPacket) {
        synchronized (pendingQueue) {
            System.out.println("Pending:" + pendingQueue.size());
            DCPacket dcPacket1 = pendingQueue.remove();
            ResponsePacket responsePacket = dcPacket.getResponsePacket();
            System.out.println("kankan" + dcPacket1.getRequestPacket().getAsyncCallback() == null);
            if (dcPacket1.getRequestPacket().getAsyncCallback() != null) {

                if (dcPacket1.getRequestPacket().getOperateType() == OperateType.CREATE || dcPacket1.getRequestPacket().getOperateType() == OperateType.DELETE || dcPacket1.getRequestPacket().getOperateType() == OperateType.UPDATE) {
                    AsyncCallback.VoidCallback voidCallback = (AsyncCallback.VoidCallback) dcPacket1.getRequestPacket().getAsyncCallback();
                    voidCallback.processResult(responsePacket.getCode(), dcPacket1.getRequestPacket().getId());
                } else if (dcPacket1.getRequestPacket().getOperateType() == OperateType.RETRIEVE) {
                    AsyncCallback.DataCallback dataCallback = (AsyncCallback.DataCallback) dcPacket1.getRequestPacket().getAsyncCallback();
                    ResourceService resourceService = (ResourceService) ResourceReflection.deserializeKryo(responsePacket.getResourceService());
                    dataCallback.processResult(responsePacket.getCode(), dcPacket1.getRequestPacket().getId(), resourceService);
                } else if (dcPacket1.getRequestPacket().getOperateType() == OperateType.SERVICE) {
                    AsyncCallback.ServiceCallback serviceCallback = (AsyncCallback.ServiceCallback) dcPacket1.getRequestPacket().getAsyncCallback();

                    serviceCallback.processResult(responsePacket.getCode(), dcPacket1.getRequestPacket().getId(), responsePacket.getResult());
                }
            }

        }
    }
}
