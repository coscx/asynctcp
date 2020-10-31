package com.beetle;


import android.util.Log;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 心跳监控
 * Created by hais1992 on 2016/8/17/017.
 */
public class StateCheckChannelHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object _evt) throws Exception {
        if (_evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) _evt;
            if (event.state().equals(IdleState.ALL_IDLE)) {
                NettyLog.e("发送心跳："+ NettyClient.endFlag);
                Message msg = new Message();
                msg.cmd = Command.MSG_PING;
                msg.seq = 0;
                byte[] p = msg.pack();
                int l = p.length - Message.HEAD_SIZE;
                byte[] buf = new byte[p.length + 4];
                BytePacket.writeInt32(l, buf, 0);
                System.arraycopy(p, 0, buf, 4, p.length);
                ctx.channel().writeAndFlush(Unpooled.copiedBuffer(buf));   // 发送心跳消息

            } else if (event.state().equals(IdleState.READER_IDLE)) {
                NettyLog.e("读取信息超时：READER_IDLE，准备重连");
                //NettyClient.getInstance().isConnections = false;
               // NettyClient.getInstance().loopConnect(ctx.channel(),new Exception("读取信息超时：READER_IDLE，准备重连"));
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                NettyLog.e("写入信息超时：WRITER_IDLE，准备重连");
               // NettyClient.getInstance().isConnections = false;
                //NettyClient.getInstance().loopConnect(ctx.channel(),new Exception("写入信息超时：WRITER_IDLE，准备重连"));
            }
        }
        super.userEventTriggered(ctx, _evt);
    }

}
