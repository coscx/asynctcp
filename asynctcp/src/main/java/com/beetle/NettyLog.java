package com.beetle;


/**
 * 日记，吐丝类
 * Created by hais1992 on 15-4-24.
 */
public class NettyLog {
    protected static final boolean debug = true;    //是否开启调试
    protected static final String tag = "Netty";  //缺省的 tag
    protected static final String prefix = "";    //日记输出前缀

    private NettyLog() {
        throw new UnsupportedOperationException("不能实例化该类");
    }

    /*--------------------------------------Log输出-----------------------------------------*/
    public static void i(String msg) {
        if (debug) {
            try {
                android.util.Log.i(tag + prefix, msg);
            } catch (Exception e) {

            }
        }
    }

    public static void i(String tag, String msg) {
        if (debug) {
            try {
                android.util.Log.i(tag + prefix, msg);
            } catch (Exception e) {

            }
        }
    }

    public static void d(String tag, String msg) {
        if (debug) {
            try {
                android.util.Log.d(tag + prefix, msg);
            } catch (Exception e) {

            }
        }
    }

    public static void v(String tag, String msg) {
        if (debug) {
            try {
                android.util.Log.v(tag + prefix, msg);
            } catch (Exception e) {

            }
        }
    }

    public static void w(String tag, String msg) {
        if (debug) {
            try {
                android.util.Log.w(tag + prefix, msg);
            } catch (Exception e) {

            }
        }
    }


    public static void e(String msg) {
        if (debug) {
            try {
                android.util.Log.e(tag + prefix, msg);
            } catch (Exception e) {

            }
        }
    }

    public static void e(String tag, String msg) {
        if (debug) {
            try {
                android.util.Log.e(tag + prefix, msg);
            } catch (Exception e) {

            }
        }
    }


    public static void e(String tag, String msg, Throwable tr) {
        if (debug) {
            try {
                android.util.Log.e(tag + prefix, msg, tr);
            } catch (Exception e) {

            }
        }
    }

}
