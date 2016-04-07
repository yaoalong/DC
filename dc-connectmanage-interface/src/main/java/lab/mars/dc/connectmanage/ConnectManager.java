package lab.mars.dc.connectmanage;

import io.netty.channel.ChannelHandlerContext;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 * 连接管理
 */
public interface ConnectManager {

    void refresh(ChannelHandlerContext ctx);

    void add(ChannelHandlerContext ctx);
}
