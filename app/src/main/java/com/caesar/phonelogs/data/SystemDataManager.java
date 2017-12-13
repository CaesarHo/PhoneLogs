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

import org.json.JSONObject;

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
    private JSONObject contactData;
    public List<Contact> getContacts(Context context, Cursor cursor) {
        try {
            if (cursor != null) {
                String name = null, number = null;

                ContentResolver resolver = context.getContentResolver();
                while (cursor.moveToNext()) {
                    int contactsId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    DLog.d(TAG, "contactsId = " + contactsId + ", name = " + name);
                    Uri uri = Uri.parse("content://com.android.contacts/raw_contacts/" + contactsId + "/data");
                    Cursor dataCursor = resolver.query(uri, new String[]{
                            Phone.MIMETYPE, Phone.PHOTO_ID, Phone.DATA1, Phone.DATA2,
                            Phone.SORT_KEY_PRIMARY, Phone.DISPLAY_NAME, Phone.DATA15}, null, null, null);
                    contactData = new JSONObject();
                    if (dataCursor != null) {
                        App.getInstance().getContactInfo().clear();
                        while (dataCursor.moveToNext()) {
                            String type = dataCursor.getString(dataCursor.getColumnIndex(Phone.MIMETYPE));
//                            byte[] bytes = dataCursor.getBlob(dataCursor.getColumnIndex(Phone.DATA15));
//                                if (bytes != null) {
//                                    Bitmap photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                                    DLog.d(TAG, "getContacts bytes = " + bytes.length);
//                                }
                            try {
                                // 1.2 获取各种电话信息
                                if (Phone.CONTENT_ITEM_TYPE.equals(type)) {
                                    int phoneType = dataCursor.getInt(dataCursor.getColumnIndex(Phone.TYPE));
                                    // 手机
                                    if (phoneType == Phone.TYPE_MOBILE) {
                                        String mobile = dataCursor.getString(dataCursor.getColumnIndex(Phone.NUMBER));
                                        contactData.put("" + phoneType, mobile);
                                        number = mobile;
                                        DLog.d(TAG, "mobile = " + mobile + "------>" + "phoneType = " + phoneType);
                                    }
                                    // 住宅电话
                                    if (phoneType == Phone.TYPE_HOME) {
                                        String homeNum = dataCursor.getString(dataCursor.getColumnIndex(Phone.NUMBER));
                                        contactData.put("" + phoneType, homeNum);
                                        DLog.d(TAG, "homeNum = " + homeNum + "------>" + "phoneType = " + phoneType);
                                    }
                                    // 单位电话
                                    if (phoneType == Phone.TYPE_WORK) {
                                        String jobNum = dataCursor.getString(dataCursor.getColumnIndex(Phone.NUMBER));
                                        contactData.put("" + phoneType, jobNum);
                                        DLog.d(TAG, "jobNum = " + jobNum + "------>" + "phoneType = " + phoneType);
                                    }
                                    // 单位传真
                                    if (phoneType == Phone.TYPE_FAX_WORK) {
                                        String workFax = dataCursor.getString(dataCursor.getColumnIndex(Phone.NUMBER));
                                        contactData.put("" + phoneType, workFax);
                                        DLog.d(TAG, "workFax = " + workFax + "------>" + "phoneType = " + phoneType);
                                    }
                                    // 住宅传真
                                    if (phoneType == Phone.TYPE_FAX_HOME) {
                                        String homeFax = dataCursor.getString(dataCursor.getColumnIndex(Phone.NUMBER));
                                        contactData.put("" + phoneType, homeFax);
                                        DLog.d(TAG, "homeFax = " + homeFax + "------>" + "phoneType = " + phoneType);
                                    }
                                    // 寻呼机
                                    if (phoneType == Phone.TYPE_PAGER) {
                                        String pager = dataCursor.getString(dataCursor.getColumnIndex(Phone.NUMBER));
                                        contactData.put("" + phoneType, pager);
                                        DLog.d(TAG, "pager = " + pager + "------>" + "phoneType = " + phoneType);
                                    }
                                    // 其他
                                    if (phoneType == Phone.TYPE_OTHER) {
                                        String other = dataCursor.getString(dataCursor.getColumnIndex(Phone.NUMBER));
                                        contactData.put("" + phoneType, other);
                                        DLog.d(TAG, "other = " + other + "------>" + "phoneType = " + phoneType);
                                    }
                                    // 回拨号码
                                    if (phoneType == Phone.TYPE_CALLBACK) {
                                        String quickNum = dataCursor.getString(dataCursor.getColumnIndex(Phone.NUMBER));
                                        contactData.put("" + phoneType, quickNum);
                                        DLog.d(TAG, "quickNum = " + quickNum + "------>" + "phoneType = " + phoneType);
                                    }
                                    // 公司总机
                                    if (phoneType == Phone.TYPE_COMPANY_MAIN) {
                                        String jobTel = dataCursor.getString(dataCursor.getColumnIndex(Phone.NUMBER));
                                        contactData.put("" + phoneType, jobTel);
                                        DLog.d(TAG, "jobTel = " + jobTel + "------>" + "phoneType = " + phoneType);
                                    }
                                    // 车载电话
                                    if (phoneType == Phone.TYPE_CAR) {
                                        String carNum = dataCursor.getString(dataCursor.getColumnIndex(Phone.NUMBER));
                                        contactData.put("" + phoneType, carNum);
                                        DLog.d(TAG, "carNum = " + carNum + "------>" + "phoneType = " + phoneType);
                                    }
                                    // ISDN
                                    if (phoneType == Phone.TYPE_ISDN) {
                                        String isdn = dataCursor.getString(dataCursor.getColumnIndex(Phone.NUMBER));
                                        contactData.put("" + phoneType, isdn);
                                        DLog.d(TAG, "isdn = " + isdn + "------>" + "phoneType = " + phoneType);
                                    }
                                    // 总机
                                    if (phoneType == Phone.TYPE_MAIN) {
                                        String tel = dataCursor.getString(dataCursor.getColumnIndex(Phone.NUMBER));
                                        contactData.put("" + phoneType, tel);
                                        DLog.d(TAG, "tel = " + tel + "------>" + "phoneType = " + phoneType);
                                    }
                                    // 无线装置
                                    if (phoneType == Phone.TYPE_RADIO) {
                                        String wirelessDev = dataCursor.getString(dataCursor.getColumnIndex(Phone.NUMBER));
                                        contactData.put("" + phoneType, wirelessDev);
                                        DLog.d(TAG, "wirelessDev = " + wirelessDev + "------>" + "phoneType = " + phoneType);
                                    }
                                    // 电报
                                    if (phoneType == Phone.TYPE_TELEX) {
                                        String telegram = dataCursor.getString(dataCursor.getColumnIndex(Phone.NUMBER));
                                        contactData.put("" + phoneType, telegram);
                                        DLog.d(TAG, "telegram = " + telegram + "------>" + "phoneType = " + phoneType);
                                    }
                                    // TTY_TDD
                                    if (phoneType == Phone.TYPE_TTY_TDD) {
                                        String tty_tdd = dataCursor.getString(dataCursor.getColumnIndex(Phone.NUMBER));
                                        contactData.put("" + phoneType, tty_tdd);
                                        DLog.d(TAG, "tty_tdd = " + tty_tdd + "------>" + "phoneType = " + phoneType);
                                    }
                                    // 单位手机
                                    if (phoneType == Phone.TYPE_WORK_MOBILE) {
                                        String jobMobile = dataCursor.getString(dataCursor.getColumnIndex(Phone.NUMBER));
                                        contactData.put("" + phoneType, jobMobile);
                                        DLog.d(TAG, "jobMobile = " + jobMobile + "------>" + "phoneType = " + phoneType);
                                    }
                                    // 单位寻呼机
                                    if (phoneType == Phone.TYPE_WORK_PAGER) {
                                        String jobPager = dataCursor.getString(dataCursor.getColumnIndex(Phone.NUMBER));
                                        contactData.put("" + phoneType, jobPager);
                                        DLog.d(TAG, "jobPager = " + jobPager + "------>" + "phoneType = " + phoneType);
                                    }
                                    // 助理
                                    if (phoneType == Phone.TYPE_ASSISTANT) {
                                        String assistantNum = dataCursor.getString(dataCursor.getColumnIndex(Phone.NUMBER));
                                        contactData.put("" + phoneType, assistantNum);
                                        DLog.d(TAG, "assistantNum = " + assistantNum + "------>" + "phoneType = " + phoneType);
                                    }
                                    // 彩信
                                    if (phoneType == Phone.TYPE_MMS) {
                                        String mms = dataCursor.getString(dataCursor.getColumnIndex(Phone.NUMBER));
                                        contactData.put("" + phoneType, mms);
                                        DLog.d(TAG, "mms = " + mms + "------>" + "phoneType = " + phoneType);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        dataCursor.close();
                    }
                    DLog.i("contactData", contactData.toString());
                    if (name != null & number != null) {
                        Contact btContact = new Contact();
                        btContact.setId(contactsId);
                        btContact.setName(name);
                        btContact.setNumber(number);
                        btContact.setJsonNumber(contactData.toString());

                        //去重
                        for (int i = 0; i < App.getInstance().getContacts().size(); i++) {
                            if (btContact.getName().equals(App.getInstance().getContacts().get(i).getName()) &
                                    btContact.getNumber().equals(App.getInstance().getContacts().get(i).getNumber())) {
                                App.getInstance().getContacts().remove(i);
                            }
                        }
                        DLog.d(TAG, "name = " + btContact.getName() + ",number = " + btContact.getNumber());
                        App.getInstance().getContacts().add(btContact);
                    }
                }
            }

            App.getInstance().getMainHandler().sendEmptyMessage(Constants.CONTACTS_UPDATE_UI_MSG);
            DLog.d(TAG, "Contacts size = " + App.getInstance().getContacts().size());
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                    DLog.d(TAG, "cursor = close");
                } catch (Exception e) {
                    //ignore this
                    e.printStackTrace();
                }
            }
        }
        return App.getInstance().getContacts();
    }
    public Bitmap getPhotoForId(long contactsId) {
        Cursor dataCursor = null;
        ContentResolver resolver = App.getInstance().getApplicationContext().getContentResolver();
        try {
            Uri uri = Uri.parse("content://com.android.contacts/raw_contacts/" + contactsId + "/data");
            dataCursor = resolver.query(uri, new String[]{"_id", "mimetype", "data15"}, null, null, null);
            if (dataCursor != null) {
                while (dataCursor.moveToNext()) {
                    String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                    if ("vnd.android.cursor.item/photo".equals(type)) { // 如果他的mimetype类型是photo
                        byte[] photoByte = dataCursor.getBlob(dataCursor.getColumnIndex("data15"));
                        if (photoByte != null) {
                            Bitmap photo = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);
                            DLog.d(TAG, "photoByte != null");
                            return photo;
                        } else {
                            DLog.d(TAG, "photoByte == null");
                            return null;
                        }
                    }
                }
                dataCursor.close();
            }
        } finally {
            if (dataCursor != null) {
                try {
                    dataCursor.close();
                } catch (Exception e) {
                    //ignore this
                    e.printStackTrace();
                }
            }
        }
        return null;
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
