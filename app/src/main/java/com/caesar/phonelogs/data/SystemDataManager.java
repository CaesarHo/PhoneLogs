package com.caesar.phonelogs.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.text.TextUtils;

import com.caesar.phonelogs.R;
import com.caesar.phonelogs.base.App;
import com.caesar.phonelogs.global.Constants;
import com.caesar.phonelogs.utils.DLog;
import com.caesar.phonelogs.utils.TransitionTime;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 系统数据管理
 * Created by heshengfang on 2017/5/15.
 */
public class SystemDataManager {
    public final static String TAG = App.class.getSimpleName() + SystemDataManager.class.getSimpleName();
    private static SystemDataManager manager = null;

    private SystemDataManager() {
    }

    public synchronized static SystemDataManager getInstance() {
        if (null == manager) {
            synchronized (SystemDataManager.class) {
                if (null == manager) {
                    manager = new SystemDataManager();
                }
            }
        }
        return manager;
    }

    /**
     * 获取系统联系人
     */
    public List<Contact> getContacts(Context context, Cursor cursor) {
        if (cursor != null) {
            String name = null, number = null;
            ContentResolver resolver = context.getContentResolver();
            while (cursor.moveToNext()) {
                int contactsId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                DLog.d(TAG, "contactsId = " + contactsId);
                Uri uri = Uri.parse("content://com.android.contacts/raw_contacts/" + contactsId + "/data");
                Cursor dataCursor = resolver.query(uri, new String[]{
                        Phone.MIMETYPE, Phone.PHOTO_ID, Phone.DATA1, Phone.DATA2,
                        Phone.SORT_KEY_PRIMARY}, null, null, null);
                if (dataCursor != null) {
                    while (dataCursor.moveToNext()) {
                        Long photo_id = dataCursor.getLong(dataCursor.getColumnIndex(Phone.PHOTO_ID));
                        Long phone_type = dataCursor.getLong(dataCursor.getColumnIndex(Phone.DATA2));
                        String data = dataCursor.getString(dataCursor.getColumnIndex(Phone.DATA1));
                        String type = dataCursor.getString(dataCursor.getColumnIndex(Phone.MIMETYPE));
                        String sort = dataCursor.getString(dataCursor.getColumnIndex(Phone.SORT_KEY_PRIMARY));
//                        Bitmap photo = BitmapFactory.decodeStream(openPhoto(Long.valueOf(contactsId)));
                        if (StructuredName.CONTENT_ITEM_TYPE.equals(type)) {
                            name = data;
                        } else if (Phone.CONTENT_ITEM_TYPE.equals(type)) {
                            number = data;
                        }
                    }
                    dataCursor.close();
                }
//                getContractDetails(String.valueOf(contactsId));
                if (name != null & number != null) {
                    Contact contact = new Contact();
                    contact.setId(contactsId);
                    contact.setName(name);
                    contact.setNumber(number);
                    //去重
                    for (int i = 0; i < App.getInstance().getContacts().size(); i++) {
                        if (contact.getName().equals(App.getInstance().getContacts().get(i).getName()) &
                                contact.getNumber().equals(App.getInstance().getContacts().get(i).getNumber())) {
//                            DLog.d(TAG, "重复 = " + btContact.getName() + "," + btContact.getNumber());
                            App.getInstance().getContacts().remove(i);
                        }
                    }
                    App.getInstance().getContacts().add(contact);
                }
            }
            cursor.close();
        }
        App.getInstance().getMainHandler().sendEmptyMessage(Constants.CONTACTS_UPDATE_UI_MSG);
        DLog.d(TAG, "Contacts size = " + App.getInstance().getContacts().size());
        return App.getInstance().getContacts();
    }

    /**
     * 根据Id查询该联系人的一个或者多个电话
     *
     * @param contactID
     */
    private void getContractDetails(String contactID) {
        List<HashMap<String, String>> list = new ArrayList<>();
        String[] projection = new String[]{Phone.NUMBER};
        String selection = Phone.CONTACT_ID + " = " + "?";
        String[] selectionArgs = new String[]{contactID};
        ContentResolver resolver = App.getInstance().getContentResolver();
        Cursor cursor = resolver.query(Phone.CONTENT_URI, projection, selection, selectionArgs, Data.RAW_CONTACT_ID);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String num = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                DLog.d(TAG, "联系人的多个电话号码 = " + contactID + "," + num);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static InputStream openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = App.getInstance().getApplicationContext().getContentResolver().query(photoUri, new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    /**
     * 根据号码查找头像
     *
     * @param number
     * @return
     */
    public Bitmap getContactPhoto(String number, boolean isSmartBar) {
        Bitmap photo = null;
        //通话电话号码获取头像uri
        Uri uriNumber2Contacts = Uri.parse("content://com.android.contacts/" + "data/phones/filter/" + number);
        Cursor cursorCantacts = App.getInstance().getApplicationContext().getContentResolver().query(uriNumber2Contacts, null, null, null, null);
        if (cursorCantacts != null) {
            if (cursorCantacts.getCount() > 0) { //若游标不为0则说明有头像,游标指向第一条记录
                cursorCantacts.moveToFirst();
                Long contactID = cursorCantacts.getLong(cursorCantacts.getColumnIndex("contact_id"));
                Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactID);
                InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(
                        App.getInstance().getContentResolver(), uri);
                photo = BitmapFactory.decodeStream(input);
                DLog.i(TAG, "GetContactPhoto =" + photo);
                cursorCantacts.close();
                return photo;
            } else {//么有头像设置默认头像
                if (isSmartBar) {//smartbar头像获取传递true ，避免返回null时，smartbar不显示默认头像
                    photo = BitmapFactory.decodeResource(App.getInstance().getResources(), R.mipmap.ic_launcher_round);
                    cursorCantacts.close();
                    return photo;
                } else {
                    cursorCantacts.close();
                    return null;
                }
            }
        }
        return null;
    }

    /*
     * 根据电话号码取得联系人姓名
     */
    public String getContactNameByPhoneNumber(String number) {
        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME, Phone.NUMBER};

        // 将自己添加到 msPeers 中
        Cursor cursor = App.getInstance().getApplicationContext().getContentResolver().query(
                Phone.CONTENT_URI,
                projection, // Which columns to return.
                Phone.NUMBER + " = '" + number + "'", // WHERE clause.
                null, // WHERE clause value substitution
                null); // Sort order.

        if (cursor == null) {
            DLog.d(TAG, "getContactNameByPhoneNumber cursor null");
            return number;
        }
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            // 取得联系人名字
            int nameIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            String name = cursor.getString(nameIndex);
            cursor.close();
            return name;
        }
        cursor.close();
        return number;
    }

    /**
     * 获取系统通话记录
     */
    public List<CallLog> getCallLogs(Cursor cursor) {
        if (cursor != null) {
            while (cursor.moveToNext()) {
                CallLog calllog = new CallLog();
                String number = cursor.getString(0);//call number
                long date = cursor.getLong(1);//call time
                int type = cursor.getInt(2);//call type
                String name = cursor.getString(3);//name
                String duration = cursor.getString(4);//call duration
                String areaCode = cursor.getString(5);//phone area
                String callTime = TransitionTime.convertTimeFirstStyle(date);
                DLog.d(TAG, "Logs Name = " + name + "," + number);
                if (TransitionTime.getTodayData().equals(callTime)) {//is today
                    calllog.setCallTime("Today");
                } else if (TransitionTime.getYesData().equals(callTime)) {
                    calllog.setCallTime("Yesterday");
                } else {
                    calllog.setCallTime(callTime);
                }
                if (name != null) {
                    if (name.equalsIgnoreCase(number)) {
                        name = "";
                    }
                }
                calllog.setName(name);
                calllog.setNumber(number);
                calllog.setDate(date);
                calllog.setType(type);
                calllog.setCountType(1);
                calllog.setDuration(duration);

                if (TextUtils.isEmpty(number)) {
                    App.getInstance().getMainHandler().sendEmptyMessage(Constants.CALLLOGS_UPDATE_UI_MSG);
                    App.getInstance().getCalllogs().add(calllog);
                    continue;
                }
                boolean isAdd = screenData(App.getInstance().getCalllogs(), calllog);
                if (isAdd) {
                    App.getInstance().getMainHandler().sendEmptyMessage(Constants.CALLLOGS_UPDATE_UI_MSG);
                    App.getInstance().getCalllogs().add(calllog);
                }
            }
            cursor.close();
            App.getInstance().getMainHandler().sendEmptyMessage(Constants.CALLLOGS_UPDATE_UI_MSG);
        }
        return App.getInstance().getCalllogs();
    }

    /**
     * Filter data
     *
     * @param callInfoLogs
     * @param info
     * @return
     */
    private boolean screenData(List<CallLog> callInfoLogs, CallLog info) {
        if (callInfoLogs.size() > 0) {
            for (int i = 0; i < callInfoLogs.size(); i++) {
                CallLog callInfoLog = callInfoLogs.get(i);
                //If the date and type of all the same words then the call record do not, into a number classified as the last record inside
                if (callInfoLog.getCallTime().equals(info.getCallTime()) && callInfoLog.getType() == info.getType() && info.getNumber().equals(callInfoLog.getNumber())) {
                    callInfoLog.setCountType(callInfoLog.getCountType() + 1);//Increment once
                    //end find
                    return false;
                }
            }
        }
        return true;
    }
}
