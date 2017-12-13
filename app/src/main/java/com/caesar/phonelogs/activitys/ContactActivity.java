package com.caesar.phonelogs.activitys;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.caesar.phonelogs.R;
import com.caesar.phonelogs.adapters.ContactInfoAdapter;
import com.caesar.phonelogs.base.App;
import com.caesar.phonelogs.base.DistributedHandler;
import com.caesar.phonelogs.data.Contact;
import com.caesar.phonelogs.data.ContactInfo;
import com.caesar.phonelogs.data.SystemDataManager;
import com.caesar.phonelogs.global.Constants;
import com.caesar.phonelogs.utils.ContactInfoUtils;
import com.caesar.phonelogs.utils.DLog;
import com.caesar.phonelogs.utils.MyDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactActivity extends AppCompatActivity implements View.OnClickListener, DistributedHandler.HandlerPart {
    public final static String TAG = App.class.getSimpleName() + ContactActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private ContactInfoAdapter mAdapter;
    private ImageButton iBtnLeft;
    private TextView tvTitle;
    private TextView tvName;
    private Contact btContact;
    private Bitmap bitmap;
    private String contactData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        App.getInstance().getContactInfo().clear();
        btContact = this.getIntent().getParcelableExtra("BTContact");
        contactData = this.getIntent().getStringExtra("BTContactInfo");
        initView();
    }

    public void initView() {
        iBtnLeft = (ImageButton) findViewById(R.id.ibtn_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText("电话分类");
        tvName = (TextView) findViewById(R.id.txt_name);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new ContactInfoAdapter(this, App.getInstance().getContactInfo());
        mRecyclerView.setAdapter(mAdapter);
        //自定义的分隔线
        mRecyclerView.addItemDecoration(new MyDecoration(this, MyDecoration.VERTICAL_LIST));

        initData();

//        try {
//            ContactInfoUtils contactInfoUtils = new ContactInfoUtils(this);
//            contactInfoUtils.getContactInfo(btContact.getId());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public void initData() {
        if (btContact.getName().length() >= 10) {
            tvName.setText(btContact.getName().substring(0, 10) + "...");
        } else {
            tvName.setText(btContact.getName());
        }
        bitmap = SystemDataManager.getInstance().getPhotoForId(btContact.getId());
        if (bitmap != null) {
            iBtnLeft.setImageBitmap(App.getInstance().createCircleBitmap(bitmap, 0));
        }

        DLog.d(TAG, "BTContact = " + btContact.toString() + "," + btContact.getId());
        try {
            if(contactData != null){
                JSONObject hostObject = new JSONObject(contactData);
                Iterator<String> sIterator = hostObject.keys();
                while (sIterator.hasNext()) {
                    // 获得key
                    String key = sIterator.next();
                    // 根据key获得value, value也可以是JSONObject,JSONArray,使用对应的参数接收即可
                    String number = hostObject.getString(key);
                    System.out.println("key: " + key + ",number:" + number);
                    if (isNumeric(key)){
                        int id = Integer.parseInt(key);
                        ContactInfo btContactInfo = new ContactInfo();
                        btContactInfo.setPhoneType(id);
                        btContactInfo.setNumber(number);
                        App.getInstance().getContactInfo().add(btContactInfo);
                        DLog.d(TAG,"add for contactInfos");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setListener();
    }
    /**
     * 利用正则表达式判断字符串是否是数字
     *
     * @param str
     * @return
     */
    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public void setListener() {
        iBtnLeft.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_left:
                finish();
                break;
        }
    }

    @Override
    public boolean dispatchHandleMessage(Message msg) {
        switch (msg.what) {
            case Constants.CONTACT_INFO_SYNC_MSG:
                mAdapter.notifyDataSetChanged();
                break;
        }
        return false;
    }
}
