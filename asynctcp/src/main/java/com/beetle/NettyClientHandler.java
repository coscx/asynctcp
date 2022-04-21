package com.beetle;

import android.util.Log;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;


public class NettyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final String TAG = "NettyClientHandler";
    private NettyListener listener;

//    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat"+System.getProperty("line.separator"),
//            CharsetUtil.UTF_8));
//    byte[] requestBody = {(byte) 0xFE, (byte) 0xED, (byte) 0xFE, 5,4, (byte) 0xFF,0x0a};


    public NettyClientHandler(NettyListener listener) {
        this.listener = listener;
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                NettyLog.e("send heart");
                Message msg = new Message();
                msg.cmd = Command.MSG_PING;
                msg.seq = 0;
                byte[] p = msg.pack();
                int l = p.length - Message.HEAD_SIZE;
                byte[] buf = new byte[p.length + 4];
                BytePacket.writeInt32(l, buf, 0);
                System.arraycopy(p, 0, buf, 4, p.length);
                ctx.channel().writeAndFlush(Unpooled.copiedBuffer(buf));
            }
        }
    }

    /**
     * 连接成功
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Log.e(TAG, "channelActive");
//        NettyClient.getInstance().setConnectStatus(true);
        listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_SUCCESS);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.e(TAG, "channelInactive");
//        NettyClient.getInstance().setConnectStatus(false);
//        listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_CLOSED);
       // NettyClient.getInstance().reconnect();
    }

    /**
     * 客户端收到消息
     *
     * @param channelHandlerContext
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        Log.e(TAG, "channelRead0");
        if (byteBuf.hasArray()) {
            int length = byteBuf.readableBytes();
            byte[] array = new byte[length];
            byteBuf.getBytes(byteBuf.readerIndex(), array);
            listener.onMessageResponse(array);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
//        NettyClient.getInstance().setConnectStatus(false);
        Log.e(TAG, "exceptionCaught");
        listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_ERROR);
        cause.printStackTrace();
        ctx.close();
    }


}
