package com.mycj.junsda.fragment;

import java.util.Date;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycj.junsda.R;
import com.mycj.junsda.util.DataUtil;
import com.mycj.junsda.view.DateUtil;
import com.mycj.junsda.view.SportCircleView;

public class HomeFragment extends Fragment implements View.OnClickListener {

	private SportCircleView circleSport;
	private TextView tvInfoDate;
	private TextView tvInfoTime;
	private TextView tvCircleDistance;
	private TextView tvCircleTime;
	private TextView tvCircleStep;
	private TextView tvInfoCal;
	private TextView tvInfoSpeed;
	private TextView tvInfoHeartRate;
	private ImageView imgRefresh;
	private ImageView imgShare;
	Handler mHandler = new Handler(){};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View homeView = inflater.inflate(R.layout.fragment_home, container, false);
		circleSport = (SportCircleView) homeView.findViewById(R.id.circle_sport);
		tvInfoDate = (TextView) homeView.findViewById(R.id.tv_info_date);
		tvInfoTime = (TextView) homeView.findViewById(R.id.tv_info_time);
		tvCircleDistance = (TextView) homeView.findViewById(R.id.tv_circle_distance);
		tvCircleTime = (TextView) homeView.findViewById(R.id.tv_circle_hour);
		tvCircleStep = (TextView) homeView.findViewById(R.id.tv_circle_step);
		tvInfoCal = (TextView) homeView.findViewById(R.id.tv_info_cal);
		tvInfoSpeed = (TextView) homeView.findViewById(R.id.tv_info_speed);
		tvInfoHeartRate = (TextView) homeView.findViewById(R.id.tv_info_heart_rate);
		imgRefresh = (ImageView) homeView.findViewById(R.id.img_refresh);
		imgShare = (ImageView) homeView.findViewById(R.id.img_share);

		freshHomeFragmentUi();

		imgRefresh.setOnClickListener(this);
		imgShare.setOnClickListener(this);

		Log.e("homeFragment", "homeFragment--onCreateView()");
		return homeView;
	}
	

	public void freshHomeFragmentUi() {
		// 刷新时间信息
		freshTime();

		// 刷新圆环信息
		freshCircleSport(2000, 1);

		// 刷新运动信息
		freshSportInfo(0, 0);

		// 刷新心率信息
		freshHeartRateInfo(0);
	}
	
	private Runnable timeRunnable = new Runnable() {
		
		@Override
		public void run() {
			Date date = new Date();
			tvInfoDate.setText(DateUtil.dateToString(date, "yyyy/MM/dd"));
			tvInfoTime.setText(DateUtil.dateToString(date, "HH:mm"));
			mHandler.postDelayed(timeRunnable, 2000);
		}
	};
	public void freshTime() {
		mHandler.post(timeRunnable);
	
	}

	public void freshCircleSport(int max, int progress) {
		circleSport.setMax(max);
		circleSport.setProgressWithAnimation(progress, 1000);
	}

	public void freshSportInfo(int step, int time) {
		if (getActivity()!=null) {
			String stepStr = String.valueOf(step);
//			String distanceStr = DataUtil.getDistanceValue(step)+ getActivity().getString(R.string.km);
			String distanceStr = DataUtil.getDistanceWithUnit(step, getContext());
			String timeStr = DateUtil.formateTime(time);
			String kalStr = DataUtil.getKalWithUnit(step,getActivity());
			String speedStr = DataUtil.getAvgSpeed(step, time,getActivity());
			tvCircleStep.setText(stepStr);
			tvCircleDistance.setText(distanceStr);
			tvCircleTime.setText(timeStr);
			tvInfoCal.setText(kalStr);
			tvInfoSpeed.setText(speedStr);
		}
	}

	public void freshHeartRateInfo(int hr) {
		String heartRateStr = String.valueOf(hr) + getActivity().getString(R.string.times_per_min);
		tvInfoHeartRate.setText(heartRateStr);

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.img_refresh:
			if (mOnHomeFragmentClickListener != null) {
				mOnHomeFragmentClickListener.doRefresh();
			}
			test();
			break;
		case R.id.img_share:
			if (mOnHomeFragmentClickListener != null) {
				mOnHomeFragmentClickListener.doShare();
			}
		
			break;
		}
	}

	public interface OnHomeFragmentClickListener {
		void doShare();

		void doRefresh();
	}

	public OnHomeFragmentClickListener mOnHomeFragmentClickListener;

	public void setOnHomeFragmentClickListener(OnHomeFragmentClickListener mOnHomeFragmentClickListener) {
		this.mOnHomeFragmentClickListener = mOnHomeFragmentClickListener;
	}
	
	private void test(){
		ColorDrawable colorDrawable1 = new ColorDrawable(0xFFFF0000);//必须制定alpha否则默认透明
		ColorDrawable colorDrawable2 = new ColorDrawable(0xFF0000FF);//必须制定alpha否则默认透明
		ImageView img = (ImageView) getActivity().findViewById(R.id.test);
		TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{colorDrawable1,colorDrawable2});
		img.setImageDrawable(transitionDrawable);
		transitionDrawable.startTransition(5000);
	}
}
