package com.beetle;


import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.concurrent.CopyOnWriteArrayList;
/**
 * Netty客户端
 * Created by hais1992 on 2016/7/5/005.
 */
public class NettyClient {
    protected static final String endFlag = "Δ";        //结束符号
    protected static final String keepaliveFlag = "➹";  //心跳过滤符号
    protected static int reConnectTime = 10;        //重连间隔
    protected static int readerIdleTime = 25;       //读取超时
    protected static int writerIdleTime = 25;       //写入超时
    protected static int allIdleTime = 13;          //全部超时
    private   boolean mayInterruptIfRunning = false;
    private ChannelFuture channelFuture;
    private Channel channel;
    private Bootstrap bootstrap;
    private EventLoopGroup eventLoopGroup;
    protected boolean isConnections = false;    //是否正在连接中.
    private String HOST;
    private int PORT;
    protected NettyEventListener listener;
    private static ScheduledFuture<?> future;
    private static NettyClient NETTY_CLIENT;
    private CopyOnWriteArrayList<byte[]> mCachedRequestList = new CopyOnWriteArrayList();
    protected static NettyClient getInstance() {
        return NETTY_CLIENT;
    }

    /**
     * 建立一个 Netty 客户端
     *
     * @param host     IP地址
     * @param port     端口
     * @param listener 监听器
     */
    public NettyClient(String host, int port, NettyEventListener listener) {
        HOST = host;
        PORT = port;
        NETTY_CLIENT = this;
        this.listener = listener;
    }

    private void initNettyClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ClientInitializer());
        //设置TCP协议的属性
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
//        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
//        bootstrap.option(ChannelOption.TCP_NODELAY, true);
//        bootstrap.option(ChannelOption.SO_BACKLOG, 10000);
//        bootstrap.option(ChannelOption.SO_RCVBUF, 1024);
//        bootstrap.option(ChannelOption.SO_SNDBUF, 1024);
//        bootstrap.option(ChannelOption.SO_TIMEOUT, 5000);
//        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
    }

    /**
     * 开始连接服务器
     *
     * @return
     */
    public NettyClient connect() {
        if (listener != null) listener.onConnections();
        if (channel != null) channel.close();
        else initNettyClient();
        try {
            isConnections = true;
            channelFuture = bootstrap.connect(HOST, PORT);
            channelFuture.addListener(channelFutureListener);
            channel = channelFuture.sync().channel();
            NettyLog.e("建立 Socket 通道：" + HOST + ":" + PORT);
        } catch (Exception e) {
            NettyLog.e(NettyLog.tag, "Socket通道建立出错：" + HOST + ":" + PORT, e);
            loopConnect(channelFuture.channel(), e);
        }
        return this;
    }

    /**
     * 关闭 连接 通道
     */
    public void closeAll() {
        if (channel != null) channel.close();
        channel = null;
//        eventLoopGroup.shutdownGracefully();
    }

    /**
     * 发送信息
     *
     * @param message
     */
    public void send(byte[] message) {
        try {
            if (channel == null) {
                NettyLog.e("未建立 Socket 通道,执行连接任务-" + "连接服务器...");
                NettyClient.getInstance().connect();
                //mCachedRequestList.add(message);

            }
            NettyLog.e("发送：" + message);
            channel.writeAndFlush(Unpooled.copiedBuffer(message)).sync();

        } catch (Exception e) {
            NettyLog.e(NettyLog.tag, "消息发送出错：" + message, e);
        }
    }


    ChannelFutureListener channelFutureListener = new ChannelFutureListener() {
        public void operationComplete(ChannelFuture f) throws Exception {
            if (f.isSuccess()) {
                NettyLog.e("接服务器成功");
                isConnections = false;
                if (listener != null) listener.onConnectSuccess();
                mayInterruptIfRunning = false;
                if (future !=null)
                future.cancel(mayInterruptIfRunning);
            } else {
                loopConnect(f.channel(), new Exception("网络不稳定，和服务器连接中断！"));
            }
        }
    };

    /**
     * 重连 逻辑
     *
     * @param channel
     * @param e
     */
    protected synchronized void loopConnect(final Channel channel, Exception e) {
        if(!isConnections)return;
        isConnections = false;

        NettyLog.e("与服务器 " + HOST + ":" + PORT + " 断开连接, " + reConnectTime + " 秒后 "+ channel.id()+" 重连！");
        if (listener != null) listener.onConnectError(e);
        if (future == null){
            future =channel.eventLoop().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (!isConnections && !channel.isActive()) {
                        NettyLog.e("重连任务" + channel.id() + "正在重连服务器。");
                        NettyClient.getInstance().connect();
                    }
                }
            }, reConnectTime, reConnectTime,TimeUnit.SECONDS);

        }



    }

}
