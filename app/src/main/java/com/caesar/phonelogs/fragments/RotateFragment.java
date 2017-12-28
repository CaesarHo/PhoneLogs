package com.caesar.phonelogs.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.caesar.phonelogs.R;
import com.caesar.phonelogs.view.CircleProgressView;
import com.caesar.phonelogs.view.GradientArcProgressView;
import com.caesar.phonelogs.view.GradientArcProgressView2;
import com.caesar.phonelogs.view.GradientArcView;
import com.caesar.phonelogs.view.Rotate3dAnimation;
import com.caesar.phonelogs.view.RoundNumProgressView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RotateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RotateFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView mRotateImgv;
    private Button mSwitchAnimBtn1;
    private Button mSwitchAnimBtn2;
    private Button mSwitchAnimBtn3;

    public RotateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RotateFragment newInstance(String param1, String param2) {
        RotateFragment fragment = new RotateFragment();
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
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        initView(view);
        // Inflate the layout for this fragment
        return view;
    }

    public void initView(View view) {
        mRotateImgv = (ImageView) view.findViewById(R.id.rotateview);
        mSwitchAnimBtn1 = (Button) view.findViewById(R.id.rotateanim_btn1);
        mSwitchAnimBtn2 = (Button) view.findViewById(R.id.rotateanim_btn2);
        mSwitchAnimBtn3 = (Button) view.findViewById(R.id.rotateanim_btn3);
        mSwitchAnimBtn1.setOnClickListener(this);
        mSwitchAnimBtn2.setOnClickListener(this);
        mSwitchAnimBtn3.setOnClickListener(this);

        initRoundNumProgressViews(view);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rotateanim_btn1:
                rotateAnimHorizon();
                break;
            case R.id.rotateanim_btn2:
                rotateOnXCoordinate();
                break;
            case R.id.rotateanim_btn3:
                rotateOnYCoordinate();
                break;
        }
    }

    private int mCurPic = 0;
    private Runnable mEndFlagRunnable = new Runnable() {
        @Override
        public void run() {
            mRotateImgv.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCurPic = mCurPic % 3;
                    switch (mCurPic) {
                        case 0:
                            mRotateImgv.setImageResource(R.mipmap.car_mune_bg);
                            break;
                        case 1:
                            mRotateImgv.setImageResource(R.mipmap.ic_launcher_round);
                            break;
                        case 2:
                            mRotateImgv.setImageResource(R.mipmap.ic_launcher);
                            break;
                    }
                    mCurPic++;
                }
            }, 50);
        }
    };

    private void initRoundNumProgressViews(View view) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final int numProgressView1Size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, metrics);
        final int numProgressView2Size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, metrics);

        final RoundNumProgressView numProgressView = (RoundNumProgressView)view. findViewById(R.id.roundnumprogressview);
        numProgressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numProgressView.setCurProgress(10);
                numProgressView.startFlickerArcProgress(mEndFlagRunnable);
            }
        });
        LinearLayout parent = (LinearLayout)view. findViewById(R.id.proress_parent);
        final RoundNumProgressView numProgressView1 = new RoundNumProgressView(getActivity());
        numProgressView1.setLayoutParams(new ViewGroup.LayoutParams(numProgressView1Size, numProgressView1Size));
        parent.addView(numProgressView1);
        ((LinearLayout.LayoutParams) numProgressView1.getLayoutParams()).leftMargin = 30;
        numProgressView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numProgressView1.setCurProgress(50);
                numProgressView1.reRefreshToOriginProgress();
            }
        });
        final RoundNumProgressView numProgressView2 = new RoundNumProgressView(getActivity());
        numProgressView2.setLayoutParams(new ViewGroup.LayoutParams(numProgressView2Size, numProgressView2Size));
        parent.addView(numProgressView2);
        ((LinearLayout.LayoutParams) numProgressView2.getLayoutParams()).leftMargin = 30;
        numProgressView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numProgressView2.setCurProgress(0);
                numProgressView2.startFlickerArcProgress(mEndFlagRunnable);
            }
        });
        final GradientArcView gradientArcView = new GradientArcView(getActivity());
        gradientArcView.setLayoutParams(new ViewGroup.LayoutParams(numProgressView2Size, numProgressView2Size));
        parent.addView(gradientArcView);
        ((LinearLayout.LayoutParams) gradientArcView.getLayoutParams()).leftMargin = 30;
        gradientArcView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gradientArcView.startFlickerArcProgress(mEndFlagRunnable);
            }
        });

        final GradientArcProgressView gradientArcProgressView = new GradientArcProgressView(getActivity());
        gradientArcProgressView.setLayoutParams(new ViewGroup.LayoutParams(numProgressView2Size, numProgressView2Size));
        parent.addView(gradientArcProgressView);
        ((LinearLayout.LayoutParams) gradientArcProgressView.getLayoutParams()).leftMargin = 30;
        gradientArcProgressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gradientArcProgressView.startFlickerArcProgress(mEndFlagRunnable);
            }
        });

        final GradientArcProgressView2 gradientArcProgressView2 = new GradientArcProgressView2(getActivity());
        gradientArcProgressView2.setLayoutParams(new ViewGroup.LayoutParams(numProgressView2Size, numProgressView2Size));
        parent.addView(gradientArcProgressView2);
        ((LinearLayout.LayoutParams) gradientArcProgressView2.getLayoutParams()).leftMargin = 30;
        gradientArcProgressView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gradientArcProgressView2.startFlickerArcProgress(mEndFlagRunnable);
            }
        });

        final CircleProgressView circleProgressView = new CircleProgressView(getActivity());
        circleProgressView.setRoundProgressBgColor(0xff424045);
        circleProgressView.setRoundProgressBgColor(Color.WHITE);
        circleProgressView.setProgressColors(new int[]{0x00535353, 0xff535353});
        circleProgressView.setLayoutParams(new ViewGroup.LayoutParams(100, 100));
        parent.addView(circleProgressView);
        ((LinearLayout.LayoutParams) circleProgressView.getLayoutParams()).leftMargin = 30;
    }

    private void rotateOnXCoordinate() {
        float centerX = mRotateImgv.getWidth() / 2.0f;
        float centerY = mRotateImgv.getHeight() / 2.0f;
        float depthZ = 0f;
        Rotate3dAnimation rotate3dAnimationX = new Rotate3dAnimation(0, 180, centerX, centerY, depthZ, Rotate3dAnimation.ROTATE_X_AXIS, true);
        rotate3dAnimationX.setDuration(1000);
        mRotateImgv.startAnimation(rotate3dAnimationX);

        // 下面的代码，旋转的时候可以带透明度
//        float centerX = mRotateImgv.getWidth() / 2.0f;
//        float centerY = mRotateImgv.getHeight() / 2.0f;
//        float depthZ = 0f;
//        Rotate3dAnimationXY rotate3dAnimationX = new Rotate3dAnimationXY(0, 180, centerX, centerY, depthZ, true, (byte) 0);
//        rotate3dAnimationX.setDuration(1000);
//        mRotateImgv.startAnimation(rotate3dAnimationX);
    }

    private void rotateOnYCoordinate() {
        float centerX = mRotateImgv.getWidth() / 2.0f;
        float centerY = mRotateImgv.getHeight() / 2.0f;
        float centerZ = 0f;

        Rotate3dAnimation rotate3dAnimationX = new Rotate3dAnimation(0, 180, centerX, centerY, centerZ, Rotate3dAnimation.ROTATE_Y_AXIS, true);
        rotate3dAnimationX.setDuration(1000);
        mRotateImgv.startAnimation(rotate3dAnimationX);
    }

    private void rotateAnimHorizon() {
        float centerX = mRotateImgv.getWidth() / 2.0f;
        float centerY = mRotateImgv.getHeight() / 2.0f;
        float centerZ = 0f;

        Rotate3dAnimation rotate3dAnimationX = new Rotate3dAnimation(180, 0, centerX, centerY, centerZ, Rotate3dAnimation.ROTATE_Z_AXIS, true);
        rotate3dAnimationX.setDuration(1000);
        mRotateImgv.startAnimation(rotate3dAnimationX);

        // 下面是使用android自带的旋转动画
//        RotateAnimation rotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        rotateAnimation.setDuration(1000);
//        mRotateImgv.startAnimation(rotateAnimation);
    }

}
