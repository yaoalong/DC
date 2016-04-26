package lab.mars.dc.connectmanage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */

/**
 * 连接管理
 */
public class LRUManage implements ConnectManager {
    private int size = 16;
    
    
    /**
     * 利用了LinkedHashMap实现了LRU 连接池
     */
    public final LinkedHashMap<ChannelHandlerContext, Boolean> connectMessages = new LinkedHashMap<ChannelHandlerContext, Boolean>(
            size, 0.5f, true) {
        /**
         *
         */
        private static final long serialVersionUID = 3033453005289310613L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<ChannelHandlerContext, Boolean> eldest) {
            if (size() > size) {
                eldest.getKey().close();
                return true;
            }
            return false;
        }
    };

    public LRUManage() {

    }

    public LRUManage(int size) {
        this.size = size;
    }

    @Override
    public synchronized void refresh(ChannelHandlerContext ctx) {
        connectMessages.get(ctx);
    }

    @Override
    public synchronized void add(ChannelHandlerContext ctx) {
        connectMessages.put(ctx, true);
    }
    public synchronized  void remove(ChannelHandlerContext ctx){
        connectMessages.remove(ctx);
    }
}
