package com.beetle;

import android.content.Context;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class AsyncSSLTCP implements AsyncTCPInterface {
    private int sock;
    private int events;
    NettyClient mNettyClient;
    private byte[] data;
    private boolean connecting;
    private TCPConnectCallback connectCallback;
    private TCPReadCallback readCallback;
    private long self;
    public void AsyncTCP(){

    }
    private Context context;

    public void setContext(Context context) {
        context = context;
    }
    public void setConnectCallback(TCPConnectCallback cb) {
        connectCallback = cb;
    }
    public void setReadCallback(TCPReadCallback cb) {
        readCallback = cb;
    }
    public  boolean connect(String host, int port,Context context){
        this.context =context;
        connectNettyServer(host, port);
        return true;
    }
    public  boolean connect(String host, int port){
        connectNettyServer(host, port);
        return true;
    }
    public  void close(){
        this.mNettyClient.disconnect();
    };

    public  void writeData(byte[] bytes)  {
         if(this.mNettyClient ==null){
             NettyLog.e( "mNettyClient is null");
             return;
         }
        this.mNettyClient.sendMsgToServer(bytes, new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {                //4
                    NettyLog.i( "Send data successful");
                } else {
                    NettyLog.e( "Send data error");
                }
            }
        });
    };

    public  void startRead(){

    };


    /**
     * 连接Netty 服务端
     *
     * @param host 服务端地址
     * @param port 服务端端口 默认两端约定一致
     */
    private void connectNettyServer(String host, int port) {

        this.mNettyClient = new NettyClient(host, port,this.context);

        NettyLog.i("connectNettyServer");
        if (!this.mNettyClient.getConnectStatus()) {
            this.mNettyClient.setListener(new NettyListener() {
                @Override
                public void onMessageResponse(byte[] msg) {
                    NettyLog.i("onMessageResponse <-");
                    /**
                     *   接收服务端发送过来的 json数据解析
                     */
                    // TODO: 2018/6/1  do something
                    // QueueShowBean    queueShowBean = JSONObject.parseObject((String) msg, QueueShowBean.class);
                    readCallback.onRead(this,msg);

                }

                @Override
                public void onServiceStatusConnectChanged(int statusCode) {
                    /**
                     * 回调执行还在子线程中
                     */
                    connectCallback.onConnect(this,0);

                }
            });

            this.mNettyClient.connect();//连接服务器
        }
    }
}
