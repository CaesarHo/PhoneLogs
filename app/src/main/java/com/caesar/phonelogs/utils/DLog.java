package com.caesar.phonelogs.utils;

import android.util.Log;

/**
 * Created by heshengfang on 2017/4/14.
 */
public class DLog {
    private final static boolean DEBUG_D = true;
    private final static boolean DEBUG_E = true;
    private final static boolean DEBUG_I = true;
    private final static boolean DEBUG_W = true;

    public static void d(String tag, String msg) {
        if (DEBUG_D) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (DEBUG_E) {
            Log.e(tag, msg, tr);
        }
    }

    public static void e(String tag, Throwable tr) {
        if (DEBUG_E) {
            Log.e(tag, "", tr);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG_I) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (DEBUG_W) {
            Log.w(tag, msg, tr);
        }
    }
}
