package com.caesar.phonelogs.fragments;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.caesar.phonelogs.R;
import com.caesar.phonelogs.adapters.ContactsAdapter;
import com.caesar.phonelogs.base.App;
import com.caesar.phonelogs.base.BTService;
import com.caesar.phonelogs.base.DistributedHandler;
import com.caesar.phonelogs.data.Contact;
import com.caesar.phonelogs.global.Constants;
import com.caesar.phonelogs.utils.ContactUtil;
import com.caesar.phonelogs.utils.DLog;
import com.caesar.phonelogs.utils.MyDecoration;
import com.caesar.phonelogs.utils.SlideBar;
import com.caesar.phonelogs.utils.TransitionTime;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.caesar.phonelogs.base.BTService.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment implements DistributedHandler.HandlerPart, View.OnClickListener, SlideBar.OnTouchLetterChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final static String TAG = ContactsFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private RecyclerView mRecyclerView;
    private ContactsAdapter contactsAdapter;
    private ProgressBar progressBar;
    private Button tvBtnLoad;
    private TextView tvLetter;
    private SlideBar slideBar;
    private LinearLayoutManager linearLayoutManager;
    private List<Contact> contacts = new ArrayList<>();
    int lastVisibleItem;
    boolean isLoading = false;//用来控制进入getdata()的次数

    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance(String param1, String param2) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        App.getInstance().getMainHandler().addHandlerPart(this);
        App.getInstance().getBackgroundHandler().addHandlerPart(this);
        initView(view);
        initListener();
        initData();
        return view;
    }

    protected void initView(View view) {
        slideBar = (SlideBar) view.findViewById(R.id.slideBar);
        tvBtnLoad = (Button) view.findViewById(R.id.txt_load);
        tvLetter = (TextView) view.findViewById(R.id.tvLetter);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        contactsAdapter = new ContactsAdapter(getActivity(), contacts);
        mRecyclerView.setAdapter(contactsAdapter);
        //自定义的分隔线
        mRecyclerView.addItemDecoration(new MyDecoration(getActivity(), MyDecoration.VERTICAL_LIST));
        //给recyclerView添加滑动监听
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                /**
                 * 当请求完成后,在把isLoading赋值为false,下次滑倒底又能进入这个方法了
                 */
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == contactsAdapter.getItemCount() && !isLoading) {
                    //到达底部之后如果footView的状态不是正在加载的状态,就将他切换成正在加载的状态
                        DLog.d(TAG, "onScrollStateChanged = Refresh data");
                        isLoading = true;
                        contactsAdapter.changeState(1);
                        App.getInstance().getMainHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getData();
                            }
                        },1500);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //拿到最后一个出现的item的位置
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

    }


    protected void initListener() {
        tvBtnLoad.setOnClickListener(this);
        slideBar.setOnTouchLetterChangeListener(this);
    }

    /**
     * 请求数据
     */
    private void getData() {
        for (int i = 0; i < 30; i++) {//每次加载十项数据
            if (App.getInstance().getContacts().size() == contacts.size()) {
                DLog.d(TAG, "getContacts().size()==contacts.size()");
                contactsAdapter.changeState(2);
                return;
            }
            contacts.addAll(App.getInstance().getContacts().subList(
                    contacts.size(), contacts.size() + 1));
        }
        isLoading = false;
        contactsAdapter.notifyDataSetChanged();
        DLog.d(TAG, "contactsAdapter != null");
    }

    public void initData() {
        BTService.onContactLoaded(App.getInstance().getApplicationContext());
        onUpdateUI();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_load:
                BTService.onContactLoaded(App.getInstance().getApplicationContext());
                break;
        }
    }

    @Override
    public boolean dispatchHandleMessage(Message msg) {
        switch (msg.what) {
            case Constants.CONTACTS_UPDATE_UI_MSG:
                onUpdateUI();
                DLog.d(TAG, "CONTACTS_ALL_MSG");
                break;
            case 1000:
                Bundle bundle = msg.getData();
                String letter = bundle.getString("letter");
                App.getInstance().getMainHandler().removeMessages(2000);
                App.getInstance().getMainHandler().sendEmptyMessageDelayed(2000, 500);
                tvLetter.setVisibility(View.VISIBLE);
                tvLetter.setText(letter);
                break;
            case 2000:
                tvLetter.setVisibility(View.GONE);
                break;
        }
        return false;
    }

    public void onUpdateUI() {
        if (App.getInstance().getContacts().size() != 0) {
            DLog.d(TAG, "size = " + App.getInstance().getContacts().size());
            getData();

            int count = App.getInstance().getContacts().size();
            if (count > 50) {
                getData();
            } else {
                contactsAdapter.setListData(contacts);
                contactsAdapter.updateLetters(contacts);
                contactsAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onTouchLetterChange(boolean isTouch, String letter) {
        int pos = contactsAdapter.getPositionByLetter(letter);
        mRecyclerView.getLayoutManager().scrollToPosition(pos);
        Bundle bundle = new Bundle();
        bundle.putString("letter", letter);
        Message message = Message.obtain();
        message.setData(bundle);
        message.what = 1000;
        App.getInstance().getMainHandler().sendMessage(message);
        DLog.d(TAG, String.format("%d|%s", pos, letter));
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
