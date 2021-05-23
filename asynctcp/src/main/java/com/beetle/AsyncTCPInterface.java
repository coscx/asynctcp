package com.beetle;

import android.content.Context;

public interface AsyncTCPInterface {

    public void setConnectCallback(TCPConnectCallback cb);
    public void setReadCallback(TCPReadCallback cb);
    public  boolean connect(String host, int port);
    public  boolean connect(String host, int port,Context context);
    public  void close();
    public void setContext(Context context);
    public  void writeData(byte[] bytes);

    public  void startRead();
}
