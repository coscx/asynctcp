/*                                                                            
  Copyright (c) 2014-2019, GoBelieve     
    All rights reserved.		    				     			
 
  This source code is licensed under the BSD-style license found in the
  LICENSE file in the root directory of this source tree. An additional grant
  of patent rights can be found in the PATENTS file in the same directory.
*/


package com.beetle;
import java.io.UnsupportedEncodingException;


public class AsyncTCP implements NettyEventListener , AsyncTCPInterface{
    private int sock;
    private int events;
    NettyClient nettyClient;
    private byte[] data;
    private boolean connecting;
    
    private TCPConnectCallback connectCallback;
    private TCPReadCallback readCallback;
    private long self;
    public void AsyncTCP(){

    }

    public void setConnectCallback(TCPConnectCallback cb) {
	connectCallback = cb;
    }
    public void setReadCallback(TCPReadCallback cb) {
	readCallback = cb;
    }
    public  boolean connect(String host, int port){
        this.nettyClient = new NettyClient(host, port,this);
        nettyClient.connect();
        return true;
    }
    public  void close(){
        this.nettyClient.closeAll();
    };

    public  void writeData(byte[] bytes)  {
        this.nettyClient.send(bytes);
    };
    
    public  void startRead(){

    };
  

    //static {
        //System.loadLibrary("async_tcp");
    //}

    @Override
    public void onConnectSuccess() {
        connectCallback.onConnect(this,0);
    }

    @Override
    public void onConnections() {

    }

    @Override
    public void onConnectError(Exception e) {
        connectCallback.onConnect(this,3);
    }

    @Override
    public void onEventMessage(byte[] s) {

        readCallback.onRead(this,s);
    }
}
