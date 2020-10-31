package com.beetle;


import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

/**
 * Created by hais1992 on 2016/7/5/005.
 */
public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 创建分隔符缓冲对象
        //ByteBuf delimiter = Unpooled.copiedBuffer(NettyClient.endFlag, Charset.forName("UTF-16LE"));
        // 当达到最大长度仍没找到分隔符 就抛出异常
//        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, true, false, delimiter));
        //ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, delimiter));
        //心跳
        pipeline.addLast("idleStateHandler", new IdleStateHandler(NettyClient.readerIdleTime, NettyClient.writerIdleTime, NettyClient.allIdleTime, TimeUnit.SECONDS));
        pipeline.addLast(new StateCheckChannelHandler());
        //解码、编码
        //pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_16LE));
        //pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_16LE));
        pipeline.addLast("handler", new ClientHandler());
    }

}
