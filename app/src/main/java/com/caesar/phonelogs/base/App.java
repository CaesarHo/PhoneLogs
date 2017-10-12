package com.caesar.phonelogs.base;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.HandlerThread;
import android.os.Message;

import com.caesar.phonelogs.data.CallLog;
import com.caesar.phonelogs.data.Contact;
import com.caesar.phonelogs.data.ContactInfo;
import com.caesar.phonelogs.utils.DLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heshengfang on 2017/4/14.
 */
public class App extends Application implements DistributedHandler.HandlerPart {
    private static final String TAG = App.class.getSimpleName();
    private final int NOTIFICATION_ID = 8888;
    private NotificationManager notificationManager;
    public static App app;
    private List<Contact> mContacts;
    private List<CallLog> mCalllogs;
    private List<Contact> mFavorites;
    private List<ContactInfo> mContactInfos;

    private Dialer mDialer;
    private HandlerThread mThread;
    private DistributedHandler mMainHandler;
    private DistributedHandler mBackgroundHandler;

    public static App getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        DLog.i(TAG, "onCreate");
        super.onCreate();
        app = this;
        mThread = new HandlerThread("yf.bt.BackgroundThread");
        mThread.start();

        Context context = getApplicationContext();
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mContacts = new ArrayList<>();
        mContactInfos = new ArrayList<>();
        mCalllogs = new ArrayList<>();
        mFavorites = new ArrayList<>();
        mDialer = new Dialer(context);

        mMainHandler = new DistributedHandler();
        mMainHandler.addHandlerPart(this);
        mBackgroundHandler = new DistributedHandler(mThread.getLooper());
        mBackgroundHandler.addHandlerPart(this);
    }

    public List<Contact> getContacts() {//联系人
        return mContacts;
    }

    public List<ContactInfo> getContactInfo(){
        return mContactInfos;
    }

    public List<CallLog> getCalllogs() {//　电话记录列表
        return mCalllogs;
    }

    public List<Contact> getFavorites() {//收藏夹列表
        return mFavorites;
    }

    public Dialer getDialer() {
        return mDialer;
    }

    public DistributedHandler getMainHandler() {
        return mMainHandler;
    }

    public DistributedHandler getBackgroundHandler() {
        return mBackgroundHandler;
    }

    @Override
    public boolean dispatchHandleMessage(Message msg) {
        return false;
    }

    public static Bitmap createCircleBitmap(Bitmap bitmap, int radio) {
        int min = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(min / 2, min / 2, min / 2 - radio, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return target;
    }

    @Override
    public void onTerminate() {// 程序终止的时候执行
        DLog.d(TAG, "onTerminate");
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {// 低内存的时候执行
        DLog.d(TAG, "onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {// 程序在内存清理的时候执行
        DLog.d(TAG, "onTrimMemory");
        super.onTrimMemory(level);
    }
}
