/*                                                                            
  Copyright (c) 2014-2019, GoBelieve     
    All rights reserved.		    				     			
 
  This source code is licensed under the BSD-style license found in the
  LICENSE file in the root directory of this source tree. An additional grant
  of patent rights can be found in the PATENTS file in the same directory.
*/


package com.beetle;
import java.util.Arrays;

/**
 * Created by houxh on 14-7-23.
 */

class Command{
    public static final int MSG_HEARTBEAT = 1;
    public static final int MSG_AUTH = 2;
    public static final int MSG_AUTH_STATUS = 3;
    public static final int MSG_IM = 4;
    public static final int MSG_ACK = 5;
    public static final int MSG_RST = 6;
    public static final int MSG_GROUP_NOTIFICATION = 7;
    public static final int MSG_GROUP_IM = 8;
    public static final int MSG_PEER_ACK = 9;
    public static final int MSG_INPUTTING = 10;
    public static final int MSG_SUBSCRIBE_ONLINE_STATE = 11;
    public static final int MSG_ONLINE_STATE = 12;
    public static final int MSG_PING = 13;
    public static final int MSG_PONG = 14;
    public static final int MSG_AUTH_TOKEN = 15;
    public static final int MSG_LOGIN_POINT = 16;
    public static final int MSG_RT = 17;
    public static final int MSG_ENTER_ROOM = 18;
    public static final int MSG_LEAVE_ROOM = 19;
    public static final int MSG_ROOM_IM = 20;
    public static final int MSG_SYSTEM = 21;
    public static final int MSG_CUSTOMER_SERVICE = 23;
    public static final int MSG_CUSTOMER = 24;
    public static final int MSG_CUSTOMER_SUPPORT = 25;

    public static final int MSG_SYNC = 26;
    public static final int MSG_SYNC_BEGIN = 27;
    public static final int MSG_SYNC_END = 28;
    public static final int MSG_SYNC_NOTIFY = 29;

    public static final int MSG_SYNC_GROUP = 30;
    public static final int MSG_SYNC_GROUP_BEGIN = 31;
    public static final int MSG_SYNC_GROUP_END = 32;
    public static final int MSG_SYNC_GROUP_NOTIFY = 33;

    public static final int MSG_SYNC_KEY = 34;
    public static final int MSG_GROUP_SYNC_KEY = 35;

    public static final int MSG_METADATA = 37;
}





public class Message {

    private static final int VERSION = 2;

    public static final int HEAD_SIZE = 8;
    public int cmd;
    public int seq;
    public int flag;
    public Object body;

    public int failCount;//发送失败的次数

    public byte[] pack() {
        int pos = 0;
        byte[] buf = new byte[64*1024];
        BytePacket.writeInt32(seq, buf, pos);
        pos += 4;
        buf[pos++] = (byte)cmd;
        buf[pos++] = (byte)VERSION;
        buf[pos++] = (byte)flag;
        pos += 1;

        if (cmd == Command.MSG_HEARTBEAT || cmd == Command.MSG_PING) {
            return Arrays.copyOf(buf, HEAD_SIZE);
        }
        else if (cmd == Command.MSG_ACK) {
            MessageACK ack = (MessageACK)body;
            BytePacket.writeInt32(ack.seq, buf, pos);
            pos += 4;
            buf[pos++] = (byte)ack.status;
            return Arrays.copyOf(buf, HEAD_SIZE+5);
        }
        else {
            return null;
        }
    }

    public boolean unpack(byte[] data) {
        int pos = 0;
        this.seq = BytePacket.readInt32(data, pos);
        pos += 4;
        cmd = data[pos];
        flag = data[pos + 2];
        pos += 4;
        if (cmd == Command.MSG_PONG) {
            return true;
        }
        else {
                return true;
            }
    }
}
