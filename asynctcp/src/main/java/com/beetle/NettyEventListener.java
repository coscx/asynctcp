package com.beetle;


/**
 * Netty事件监听器
 * Created by hais1992 on 2016/8/18/018.
 */
public interface NettyEventListener {
    public void onConnectSuccess();
    public void onConnections();
    public void onConnectError(Exception e);
    public void onEventMessage(byte[] s);
}
