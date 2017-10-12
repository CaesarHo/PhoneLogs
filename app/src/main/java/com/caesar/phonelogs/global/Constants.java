package com.caesar.phonelogs.global;

/**
 *
 * Created by heshengfang on 2017/4/17.
 */
public class Constants {

    /**
     * 广播action
     */
    public final static String ACTION_ANSWER = "external.intent.action.bt.ACTION_ANSWER";
    public final static String ACTION_IN_HANGUP = "external.intent.action.bt.ACTION_IN_HANGUP";
    public final static String ACTION_OUT_HANGUP = "external.intent.action.bt.ACTION_OUT_HANGUP";
    public final static String ACTION_SWITCH = "external.intent.action.bt.ACTION_SWITCH";
    public final static String ACTION_MUTE = "external.intent.action.bt.ACTION_MUTE";

    private static final int FIRST_MSG = 60;
    public final static int FAVORITES_ADD_DELETE_UPDATE_MSG = FIRST_MSG;
    public final static int HFP_AUDIO_TRANSFER_MSG = FIRST_MSG + 1;
    public final static int CALLLOGS_SYNC_SUCCESS_MSG = FIRST_MSG + 2;
    public final static int CLEARE_CONTACES_MSG = FIRST_MSG + 3;
    public final static int CLEARE_CALLLOGS_MSG = FIRST_MSG + 4;
    public final static int CONTACT_INFO_SYNC_MSG = FIRST_MSG + 5;
    public final static int FAVORITES_UI_UPDATE_MSG = FIRST_MSG + 6;
    //UI相关
    public final static int BT_SWITCH_STATUS_MSG = FIRST_MSG + 7;
    public final static int UPDATE_NOTIFICATION_TIME_MSG = FIRST_MSG + 8;
    public final static int SYNC_FAILED_MSG = FIRST_MSG + 9;
    public final static int MAIN_MSG_UPDATE_TIME = FIRST_MSG + 10;
    public final static int HFP_CONNECT_STATUS_MSG= FIRST_MSG + 11;
    public final static int UPDATE_FAVORITE_TO_CONTACTS_MSG = FIRST_MSG + 12;
    public final static int CALL_INFO_MSG = FIRST_MSG + 13;
    public final static int PHONE_BOOK_DATA_MSG = FIRST_MSG + 14;

    public final static int CALLLOGS_UPDATE_UI_MSG = FIRST_MSG + 15;
    public final static int CONTACTS_SYNC_SUCCESS_MSG = FIRST_MSG + 16;

    public final static int CONTACTS_SYNC_PROGRESS_MSG = FIRST_MSG + 17;
    //发送消息更新联系人
    public final static int CONTACTS_UPDATE_UI_MSG = FIRST_MSG + 18;
}
