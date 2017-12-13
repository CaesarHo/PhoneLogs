package com.caesar.phonelogs.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.caesar.phonelogs.R;
import com.caesar.phonelogs.activitys.ContactActivity;
import com.caesar.phonelogs.base.App;
import com.caesar.phonelogs.data.Contact;
import com.caesar.phonelogs.data.SystemDataManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author heshengfang
 * @since 2017/4/31
 */

public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ContactsAdapter";
    /**
     * ItemClick的回调接口
     *
     * @author zhy
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private Context context = null;
    private LayoutInflater mInflater;
    private List<Contact> mDatas;
    private Map<String, Integer> mapLetter;

    //普通布局的type
    private final int TYPE_ITEM = 0;
    //脚布局
    private final int TYPE_FOOTER = 1;
    //正在加载更多
    private final int LOADING_MORE = 1;
    //没有更多
    private final int NO_MORE = 2;
    //脚布局当前的状态,默认为没有更多
    private int footer_state = 1;

    public ContactsAdapter(Context context, List<Contact> datats) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
        updateLetters(mDatas);
    }

    @Override
    public int getItemViewType(int position) {
        //如果position加1正好等于所有item的总和,说明是最后一个item,将它设置为脚布局
        if (position + 1 == getItemCount()) {//+1实现下拉加载页面
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {//+1实现下拉加载页面
        return mDatas != null ? mDatas.size() + 1 : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateLetters(List<Contact> list) {
        mapLetter = new HashMap<>();
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                Contact c = list.get(i);
                mapLetter.put(c.getKey(), i);
            }
        }
    }

    public int getPositionByLetter(String latter) {
        if (mapLetter.containsKey(latter))
            return mapLetter.get(latter);
        return -1;
    }

    public void setListData(List<Contact> listData) {
        this.mDatas = listData;
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ITEM) {//正常布局
            View view = View.inflate(context, R.layout.list_item_contacts, null);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        } else if (viewType == TYPE_FOOTER) {//脚布局
            View view = View.inflate(context, R.layout.layout_load_more, null);
            FootViewHolder footViewHolder = new FootViewHolder(view);
            return footViewHolder;
        }

        return null;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder instanceof MyViewHolder) {
            final MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
            myViewHolder.mTxtName.setText(mDatas.get(i).getName());
            myViewHolder.mTxtNumber.setText(mDatas.get(i).getNumber());

            //方法一
            Bitmap photo = SystemDataManager.getInstance().getPhotoForId(mDatas.get(i).getId());
            if (photo != null) {
                myViewHolder.mImgHead.setImageBitmap(App.getInstance().createCircleBitmap(photo, 10));
                Log.d(TAG, "photo != null ---> " + "id = " + mDatas.get(i).getId() + mDatas.get(i).getName());
            } else {
                myViewHolder.mImgHead.setImageResource(R.mipmap.ic_launcher_round);
                Log.d(TAG, "photo == null ---> " + "id = " + mDatas.get(i).getId() + mDatas.get(i).getName());
            }
//            Bitmap bitmap = SystemDataManager.getInstance().getContactPhoto(mDatas.get(i).getNumber(), false);
//            if (bitmap != null) {
//                myViewHolder.mImgHead.setImageBitmap(App.createCircleBitmap(bitmap, 10));
//            } else {
//                myViewHolder.mImgHead.setImageResource(R.mipmap.ic_launcher_round);
//            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ContactActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("BTContact", mDatas.get(i));
                    intent.putExtra("BTContactInfo", mDatas.get(i).getJsonNumber());
                    context.startActivity(intent);
                }
            });
            updateLetters(mDatas);
        } else if (viewHolder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) viewHolder;
            if (i == 0) {//如果第一个就是脚布局,,那就让他隐藏
                footViewHolder.mProgressBar.setVisibility(View.GONE);
                footViewHolder.tvLine1.setVisibility(View.GONE);
                footViewHolder.tvLine1.setVisibility(View.GONE);
                footViewHolder.tvState.setText("");
            }
            switch (footer_state) {//根据状态来让脚布局发生改变
//                case PULL_LOAD_MORE://上拉加载
//                    footViewHolder.mProgressBar.setVisibility(View.GONE);
//                    footViewHolder.tv_state.setText("上拉加载更多");
//                    break;
                case LOADING_MORE:
                    footViewHolder.mProgressBar.setVisibility(View.VISIBLE);
                    footViewHolder.tvLine1.setVisibility(View.GONE);
                    footViewHolder.tvLine2.setVisibility(View.GONE);
                    footViewHolder.tvState.setText("正在加载...");
                    break;
                case NO_MORE:
                    footViewHolder.mProgressBar.setVisibility(View.GONE);
                    footViewHolder.tvLine1.setVisibility(View.VISIBLE);
                    footViewHolder.tvLine2.setVisibility(View.VISIBLE);
                    footViewHolder.tvState.setText("我是有底线的");
                    footViewHolder.tvState.setTextColor(Color.parseColor("#ff00ff"));
                    break;
            }
        }
    }

    /**
     * 正常布局的ViewHolder
     */
    private class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView mImgHead;
        TextView mTxtName;
        TextView mTxtNumber;

        private MyViewHolder(View view) {
            super(view);
            mImgHead = (ImageView) view.findViewById(R.id.img_head);
            mTxtName = (TextView) view.findViewById(R.id.txt_name);
            mTxtNumber = (TextView) view.findViewById(R.id.txt_number);
        }
    }

    /**
     * 脚布局的ViewHolder
     */
    private class FootViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar mProgressBar;
        private TextView tvState;
        private TextView tvLine1;
        private TextView tvLine2;

        private FootViewHolder(View view) {
            super(view);
            mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
            tvState = (TextView) view.findViewById(R.id.foot_view_item_tv);
            tvLine1 = (TextView) view.findViewById(R.id.tv_line1);
            tvLine2 = (TextView) view.findViewById(R.id.tv_line2);
        }
    }

    /**
     * 改变脚布局的状态的方法,在activity根据请求数据的状态来改变这个状态
     *
     * @param state
     */
    public void changeState(int state) {
        this.footer_state = state;
        notifyDataSetChanged();
    }
}
