package com.caesar.phonelogs.base;

import android.content.Context;
import android.util.Log;

import com.caesar.phonelogs.utils.DLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 蓝牙电话拨号器
 * Created by heshengfang on 2017/4/14.
 */
public class Dialer {
    public static final String TAG = "Dialer.java";
    private StringBuffer mNumBuffer;
    private List<OnNumChangeListener> mOnNumChangeListeners;

    public interface OnNumChangeListener {
        void onNumChanged(Dialer dialer, String num);
    }

    public Dialer(Context context) {
        mNumBuffer = new StringBuffer();
    }

    public void registerOnNumChangeListener(OnNumChangeListener l) {
        if (mOnNumChangeListeners == null)
            mOnNumChangeListeners = new ArrayList<>();

        if (!mOnNumChangeListeners.contains(l))
            mOnNumChangeListeners.add(l);
        DLog.d(TAG, "registerOnNumChangeListener");
    }

    public void unregisterOnNumChangeListener(OnNumChangeListener l) {
        if (mOnNumChangeListeners == null)
            return;
        DLog.d(TAG, "unregisterOnNumChangeListener");
        mOnNumChangeListeners.remove(l);
    }

    public boolean dial() {
        String num = mNumBuffer.toString();
        Log.d(TAG, "updateUI dial() num:" + num);

        if (mNumBuffer.length() > 1) {
            DLog.d(TAG,"拨号");
        }
        return false;
    }

    public Dialer backspace() {
        DLog.d(TAG, "backspace");
        final int len = mNumBuffer.length();
        if (len > 0) {
            mNumBuffer.delete((len - 1), len);
            notifyNumChaned();
        }
        return this;
    }

    public Dialer append(char c) {
        if (check(c)) {
            DLog.d(TAG, c + "append");
            mNumBuffer.append(c);
            notifyNumChaned();
        }
        return this;
    }

    public Dialer clear() {
        DLog.d(TAG, "clear");
        final int len = mNumBuffer.length();
        if (len > 0) {
            mNumBuffer.delete(0, len);
            notifyNumChaned();
        }
        return this;
    }

    public Dialer setNumber(String num) {
        DLog.d(TAG, "setNumber");
        int len = mNumBuffer.length();
        if (len > 0) {
            mNumBuffer.delete(0, len);
        }
        len = ((num != null) ? num.length() : 0);
        for (int i = 0; i < len; i++) {
            char c = num.charAt(i);
            if (check(c)) {
                mNumBuffer.append(c);
            }
        }
        notifyNumChaned();

        return this;
    }

    public String getNumber() {
        DLog.d(TAG, "getNumber");
        return mNumBuffer.toString();
    }

    public int length() {
        DLog.d(TAG, "length");
        return mNumBuffer.length();
    }

    private void notifyNumChaned() {
        DLog.d(TAG, "notifyNumChaned");
        if (mOnNumChangeListeners != null) {
            for (int i = 0, s = mOnNumChangeListeners.size(); i < s; i++) {
                mOnNumChangeListeners.get(i).onNumChanged(this, mNumBuffer.toString());
            }
        }
    }

    private boolean check(char c) {
        DLog.d(TAG, "check");
        if ((c >= '0' && c <= '9') || (c == '*' || c == '#' || c == '+'))
            return mNumBuffer.length() <= 255;

        return false;
    }
}
