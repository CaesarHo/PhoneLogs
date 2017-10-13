package com.caesar.phonelogs.base;

import android.Manifest;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.caesar.phonelogs.data.SystemDataManager;
import com.caesar.phonelogs.utils.DLog;

import java.lang.ref.WeakReference;

/**
 * Created by heshengfang on 2017/7/19.
 */

public class BTService extends IntentService implements DistributedHandler.HandlerPart {
    public final static String TAG = App.class.getSimpleName() + BTService.class.getSimpleName();
    private static final String ACTION_INIT_WHEN_APP_CREATE = "com.yftech.action.INIT";
    //    public static final String ACTION_RESYNC_ALL = Define.ACTION_BT_RESYNC_ALL;
//    public static final String ACTION_CLEAR_ALL = Define.ACTION_BT_CLEAR_ALL;
//    public static final String ACTION_CONTACT_SYNC = "external.intent.action.bt.ACTION_SYNC_CONTACT";
//    public static final String ACTION_SYNC_CALLLOG = Define.ACTION_BT_SYNC_CALLLOG;
    public static final String ACTION_CALLLOG_LOADED = "external.intent.action.bt.ACTION_SYNC_CALLLOG";//加载通话记录
    public static final String ACTON_CONTACT_LOADED = "external.intent.action.bt.ACTION_LOADED_CONTACT";// 加载联系人
    private WeakReference<App> mApp;

    public BTService() {
        super("BTService");
        DLog.d(TAG, "BTService");
    }

    public static void onContactLoaded(Context context) {
        Intent intent = new Intent(context, BTService.class);
        intent.setAction(ACTON_CONTACT_LOADED);
        context.startService(intent);
        DLog.d(TAG, "onContactLoaded");
    }

    public static void onCallLogLoaded(Context context) {
        Intent intent = new Intent(context, BTService.class);
        intent.setAction(ACTION_CALLLOG_LOADED);
        context.startService(intent);
        DLog.d(TAG, "onCallLogLoaded");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = new WeakReference<>(App.getInstance());
        App.getInstance().getBackgroundHandler().addHandlerPart(this);
        DLog.d(TAG, "onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        DLog.d(TAG, "onBind" + intent.getPackage());
        return super.onBind(intent);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        DLog.d(TAG, "onStartCommand" + "," + flags + "," + startId);
        String action = null;
        if (intent != null) {
            action = intent.getAction();
            if (intent.getAction().equals(ACTON_CONTACT_LOADED)) {
                DLog.d(TAG, action);
            } else if (intent.getAction().equals(ACTION_CALLLOG_LOADED)) {
                DLog.d(TAG, action);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DLog.d(TAG, "onHandleIntent");
        ContentResolver resolver = this.getContentResolver();
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTON_CONTACT_LOADED.equals(action)) {//加载联系人
                Uri uri = ContactsContract.RawContacts.CONTENT_URI;
                Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, "sort_key COLLATE LOCALIZED ASC");
                SystemDataManager.getInstance().getContacts(this, cursor);
                if (cursor != null) {
                    cursor.close();
                }
                DLog.d(TAG, "onHandleIntent getContacts");
            } else if (ACTION_CALLLOG_LOADED.equals(action)) {//加载通话记录
                Uri uri = CallLog.Calls.CONTENT_URI;
                String[] projection = new String[]{CallLog.Calls.NUMBER, CallLog.Calls.DATE,
                        CallLog.Calls.TYPE, CallLog.Calls.CACHED_NAME,
                        CallLog.Calls.DURATION, CallLog.Calls.GEOCODED_LOCATION};
                if (ActivityCompat.checkSelfPermission(App.getInstance().getApplicationContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                    DLog.d(TAG, "Permission is allowed");
                }
                Cursor cursor = resolver.query(uri, projection, null, null, CallLog.Calls.DATE + " DESC");
                SystemDataManager.getInstance().getCallLogs(cursor);
                if (cursor != null){
                    cursor.close();
                }
                DLog.d(TAG, "onHandleIntent getCallLogs");
            }
        }
    }

    @Override
    public boolean dispatchHandleMessage(Message msg) {

        return false;
    }
}
