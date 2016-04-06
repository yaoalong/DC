package lab.mars.dc.network;

import lab.mars.dc.*;
import lab.mars.dc.exception.DCException;
import lab.mars.dc.reflection.ResourceReflection;

import java.util.LinkedList;

/**
 * Author:yaoalong.
 * Date:2016/4/1.
 * Email:yaoalong@foxmail.com
 */
public class SendThread extends Thread {


    private final LinkedList<DCPacket> pendingQueue;
    private final LinkedList<DCPacket> outgoingQueue;
    private TcpClient tcpClient = new TcpClient();

    public SendThread(String serverIp, Integer port)  {
        tcpClient.connectionOne(serverIp, port);
        tcpClient.setSendThread(this);
        pendingQueue = new LinkedList<>();
        outgoingQueue = new LinkedList<>();
    }

    public void send(DCPacket dcPacket) {
        synchronized (outgoingQueue) {
            outgoingQueue.add(dcPacket);
            outgoingQueue.notify();
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (outgoingQueue) {
                while (outgoingQueue.isEmpty()) {
                    try {
                        outgoingQueue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                DCPacket requestPacket = outgoingQueue.remove();
                synchronized (pendingQueue) {
                    pendingQueue.add(requestPacket);
                }
                try {
                    tcpClient.write(requestPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    public void readResponse(DCPacket dcPacket) {
        synchronized (pendingQueue) {
            DCPacket dcPacket1 = pendingQueue.remove();
            ResponsePacket responsePacket = dcPacket.getResponsePacket();
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
                    if(dcPacket1.getResponsePacket()==null){
                        ResponsePacket responsePacket1=new ResponsePacket();
                        responsePacket1.setCode(DCException.Code.SYSTEM_ERROR);
                        serviceCallback.processResult(responsePacket1.getCode(),null,null);
                    }
                    else{
                        ResultDO resultDO= (ResultDO) ResourceReflection.deserializeKryo(responsePacket.getResult());
                        serviceCallback.processResult(responsePacket.getCode(), dcPacket1.getRequestPacket().getId(), resultDO);
                    }


                }
            }

        }
    }
    public void close(){
        tcpClient.close();
    }
}
