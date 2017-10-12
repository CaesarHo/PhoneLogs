package com.caesar.phonelogs.adapters;

import android.content.Context;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.caesar.phonelogs.R;
import com.caesar.phonelogs.data.ContactInfo;

import java.util.List;

/**
 * Created by heshengfang on 2017/5/4.
 */
public class ContactInfoAdapter extends RecyclerView.Adapter<ContactInfoAdapter.ViewHolder> {

    /**
     * ItemClick的回调接口
     *
     * @author zhy
     */
    private interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private Context context;
    private LayoutInflater mInflater;
    private List<ContactInfo> mDatas;

    public ContactInfoAdapter(Context context, List<ContactInfo> datats) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ViewHolder(View arg0) {
            super(arg0);
        }

        TextView tvType;
        TextView tvNumber;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.list_item_contactinfo, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.tvType = (TextView) view.findViewById(R.id.txt_type);
        viewHolder.tvNumber = (TextView) view.findViewById(R.id.txt_number);

        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.tvNumber.setText(mDatas.get(i).getNumber());
        switch (mDatas.get(i).getPhoneType()) {
            case 0:
                viewHolder.tvType.setText(R.string.txt_mobile);
                break;
            case Phone.TYPE_HOME://住宅电话
                viewHolder.tvType.setText(R.string.txt_home);
                break;
            case Phone.TYPE_MOBILE://手机
                viewHolder.tvType.setText(R.string.txt_mobile);
                break;
            case Phone.TYPE_WORK://单位
                viewHolder.tvType.setText(R.string.txt_work);
                break;
            case Phone.TYPE_FAX_WORK://单位传真
                viewHolder.tvType.setText(R.string.txt_work_fax);
                break;
            case Phone.TYPE_FAX_HOME://住宅传真
                viewHolder.tvType.setText(R.string.txt_home_fax);
                break;
            case Phone.TYPE_PAGER://寻呼机
                viewHolder.tvType.setText(R.string.txt_pager);
                break;
            case Phone.TYPE_OTHER://其他
                viewHolder.tvType.setText(R.string.txt_other);
                break;
            case Phone.TYPE_CALLBACK://回拨电话
                viewHolder.tvType.setText(R.string.txt_call_back);
                break;
            case Phone.TYPE_CAR://车载电话
                viewHolder.tvType.setText(R.string.txt_type_car);
                break;
            case Phone.TYPE_COMPANY_MAIN://公司总机
                viewHolder.tvType.setText(R.string.txt_company_main);
                break;
            case Phone.TYPE_ISDN://ISDN
                viewHolder.tvType.setText(R.string.txt_isdn);
                break;
            case Phone.TYPE_MAIN://总机
                viewHolder.tvType.setText(R.string.txt_main);
                break;
            case Phone.TYPE_OTHER_FAX:
                viewHolder.tvType.setText(R.string.txt_other_fax);
                break;
            case Phone.TYPE_RADIO://无线装置
                viewHolder.tvType.setText(R.string.txt_radio);
                break;
            case Phone.TYPE_TELEX://电报
                viewHolder.tvType.setText(R.string.txt_telex);
                break;
            case Phone.TYPE_TTY_TDD://TTY-TDD
                viewHolder.tvType.setText(R.string.txt_tty_tdd);
                break;
            case Phone.TYPE_WORK_MOBILE://单位手机
                viewHolder.tvType.setText(R.string.txt_work_mobile);
                break;
            case Phone.TYPE_WORK_PAGER://单位寻呼机
                viewHolder.tvType.setText(R.string.txt_work_pager);
                break;
            case Phone.TYPE_ASSISTANT://助理
                viewHolder.tvType.setText(R.string.txt_assistant);
                break;
            case Phone.TYPE_MMS://彩信
                viewHolder.tvType.setText(R.string.txt_mms);
                break;
        }
    }
}
