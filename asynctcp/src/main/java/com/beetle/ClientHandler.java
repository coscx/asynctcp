package com.beetle;

import java.util.Arrays;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 接收信息处理
 * Created by hais1992 on 2016/7/5/005.
 */
public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        super.channelInactive(ctx);
        NettyClient.getInstance().loopConnect(ctx.channel(), new Exception("网络不稳定，和服务器连接失败！"));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        NettyClient.getInstance().isConnections = true; //-.-Bug？
        NettyClient.getInstance().loopConnect(ctx.channel(),new Exception("网络不稳定，和服务器连接中断！"));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {

        if (byteBuf.hasArray()) {
        int length = byteBuf.readableBytes();
        byte[] array = new byte[length];
        byteBuf.getBytes(byteBuf.readerIndex(), array);
        NettyEventListener listener = NettyClient.getInstance().listener;
            if (listener != null) {
                int s=array[8];
                if (s!=14){
                    listener.onEventMessage(array);
                }

            }
        NettyLog.e("收到信息：" + Arrays.toString(array));
        }
    }
}
