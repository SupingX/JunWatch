package com.mycj.junsda.fragment;



import com.mycj.junsda.R;
import com.mycj.junsda.base.BaseApp;
import com.mycj.junsda.base.BaseFragment;
import com.mycj.junsda.view.FangTextView;

import android.app.Application;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;



public class SettingFragment extends BaseFragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private RelativeLayout rlSettingAbout;
    private RelativeLayout rlSettingRemind;
    private RelativeLayout rlSettingSync;
    private RelativeLayout rlSettingInformation;
    private RelativeLayout rlSettingDevice;
	private RelativeLayout rlSettingCamera;


    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View settingView = inflater.inflate(R.layout.fragment_setting, container, false);
        rlSettingDevice = (RelativeLayout) settingView.findViewById(R.id.rl_setting_device);
        rlSettingInformation = (RelativeLayout) settingView.findViewById(R.id.rl_setting_information);
        rlSettingSync = (RelativeLayout) settingView.findViewById(R.id.rl_setting_sync);
        rlSettingRemind = (RelativeLayout) settingView.findViewById(R.id.rl_setting_remind);
        rlSettingAbout = (RelativeLayout) settingView.findViewById(R.id.rl_setting_about);
        rlSettingCamera = (RelativeLayout) settingView.findViewById(R.id.rl_setting_camera);
        tvSync = (FangTextView) settingView.findViewById(R.id.tv_sync);
        rlSettingDevice.setOnClickListener(this);
        rlSettingInformation.setOnClickListener(this);
        rlSettingSync.setOnClickListener(this);
        rlSettingRemind.setOnClickListener(this);
        rlSettingAbout.setOnClickListener(this);
        rlSettingCamera.setOnClickListener(this);
        
        updateSyncText(isConnected());
        
        //test
//        ProgressBar pbTest = (ProgressBar) settingView.findViewById(R.id.pb_t);
//        pbTest.setMax(100);
//        pbTest.setProgress(70);
        
        return settingView;
    }

    @Override
    public void onClick(View view) {
        if (mOnSettingFragmentListener!=null){
            mOnSettingFragmentListener.onClick(view);
        }
    }

    public interface  OnSettingFragmentListener{
        void onClick(View v);
    }
    private OnSettingFragmentListener mOnSettingFragmentListener;
	private FangTextView tvSync;
    public void setOnSettingFragmentListener(OnSettingFragmentListener mOnSettingFragmentListener){
        this.mOnSettingFragmentListener = mOnSettingFragmentListener;
    }
    
    public void updateSyncText(boolean isConnected){
    	String connectInfo = "未连接";
    	int bg = R.drawable.bg_sync_text_off;
    	int color = Color.argb(99, 255, 255, 255);
    	if (isConnected) {
			connectInfo = "Mefit";
			bg = R.drawable.bg_sync_text_on;
			color = Color.argb(255, 255, 255, 255);
		}
    	if (tvSync!=null) {
    		tvSync.setText(connectInfo);
        	tvSync.setTextColor(color);
        	tvSync.setBackgroundResource(bg);
		}
    	
    }
    
}
