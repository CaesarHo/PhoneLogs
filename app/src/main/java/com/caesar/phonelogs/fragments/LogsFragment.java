package com.caesar.phonelogs.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.caesar.phonelogs.R;
import com.caesar.phonelogs.adapters.CallLogAdapter;
import com.caesar.phonelogs.utils.CallInfoLog;
import com.caesar.phonelogs.utils.ContactUtil;
import com.caesar.phonelogs.utils.StickyDecoration;
import com.caesar.phonelogs.utils.TransitionTime;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LogsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private RecyclerView rv;
    private ArrayList<CallInfoLog> callInfoLogs = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private CallLogAdapter callLogAdapter;

    public LogsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlaceholderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogsFragment newInstance(String param1, String param2) {
        LogsFragment fragment = new LogsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_logs, container, false);

        initView(rootView);
        initData();

        return rootView;
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


    private void initView(View view) {
        rv = (RecyclerView) view.findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        callLogAdapter = new CallLogAdapter(getActivity(), callInfoLogs);
        rv.setAdapter(callLogAdapter);
        rv.addItemDecoration(new StickyDecoration(getActivity(), callInfoLogs));
    }

    private void initData() {
        //权限允许就获取通话记录
        getCallLog(getActivity());
//        //权限允许
//        try {
//            ContactUtil contactUtil = new ContactUtil(getActivity());
//            contactUtil.getContactInfo();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 获取所有的通话记录
     *
     * @param context
     */
    public void getCallLog(Context context) {
        try {
            callInfoLogs.clear();
            ContentResolver cr = context.getContentResolver();
            Uri uri = CallLog.Calls.CONTENT_URI;
            String[] projection = new String[]{CallLog.Calls.NUMBER, CallLog.Calls.DATE,
                    CallLog.Calls.TYPE, CallLog.Calls.CACHED_NAME, CallLog.Calls.DURATION, CallLog.Calls.GEOCODED_LOCATION};
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Cursor cursor = cr.query(uri, projection, null, null, CallLog.Calls.DATE + " DESC");
            while (cursor.moveToNext()) {
                CallInfoLog callInfoLog = new CallInfoLog();
                String number = cursor.getString(0);//电话号码
                long date = cursor.getLong(1);//通话时间
                int type = cursor.getInt(2);//通话类型
                String name = cursor.getString(3);//名字
                String duration = cursor.getString(4);//通话时长
                String areaCode = cursor.getString(5);//归属地
                String callTime = TransitionTime.convertTimeFirstStyle(date);
                if (TransitionTime.getTodayData().equals(callTime)) {//如果是今天的话
                    callInfoLog.setCallTime("今天");
                } else if (TransitionTime.getYesData().equals(callTime)) {
                    callInfoLog.setCallTime("昨天");
                } else {
                    callInfoLog.setCallTime(callTime);
                }
                callInfoLog.setNumber(number);
                callInfoLog.setDate(date);
                callInfoLog.setType(type);
                callInfoLog.setName(name);
                callInfoLog.setCountType(1);
                callInfoLog.setDuration(duration);
                callInfoLog.setCode(areaCode);
                //筛选数据
                if (TextUtils.isEmpty(number)) {
                    callInfoLogs.add(callInfoLog);
                    continue;
                }
                boolean isadd = screenData(callInfoLogs, callInfoLog);
                if (isadd) {
                    callInfoLogs.add(callInfoLog);
                }
            }
            cursor.close();
            callLogAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 筛选数据
     *
     * @param callInfoLogs
     * @param info
     * @return
     */
    private boolean screenData(ArrayList<CallInfoLog> callInfoLogs, CallInfoLog info) {
        if (callInfoLogs.size() > 0) {
            for (int i = 0; i < callInfoLogs.size(); i++) {
                CallInfoLog callInfoLog = callInfoLogs.get(i);
                //如果说是日期和类型全部一样的话那么这个通话记录就不要,变成一个数量归为最近一次记录里面
                if (callInfoLog.getCallTime().equals(info.getCallTime()) && callInfoLog.getType() == info.getType() && info.getNumber().equals(callInfoLog.getNumber())) {
                    callInfoLog.setCountType(callInfoLog.getCountType() + 1);//递增一次
                    //结束这次数据查找
                    return false;
                }
            }
        }
        return true;
    }
}
