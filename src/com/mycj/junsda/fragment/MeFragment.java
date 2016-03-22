package com.mycj.junsda.fragment;



import com.mycj.junsda.R;
import com.mycj.junsda.bean.LitePalManager;
import com.mycj.junsda.util.DataUtil;
import com.mycj.junsda.view.DateUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment implements View.OnClickListener {


    private RelativeLayout rlHistrorySleep;
    private RelativeLayout rlHistrorySprot;
    private TextView tvHistoryDistance;
    private TextView tvHistoryTime;
    private TextView tvHistoryStep;
    private TextView tvHistoryCal;

    public MeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View meView =  inflater.inflate(R.layout.fragment_me, container, false);
        rlHistrorySleep = (RelativeLayout) meView.findViewById(R.id.rl_history_sleep);
        rlHistrorySprot = (RelativeLayout) meView.findViewById(R.id.rl_history_sport);
        tvHistoryDistance = (TextView) meView.findViewById(R.id.tv_history_distance );
        tvHistoryTime = (TextView) meView.findViewById(R.id.tv_history_time );
        tvHistoryStep = (TextView) meView.findViewById(R.id.tv_history_step );
        tvHistoryCal = (TextView) meView.findViewById(R.id.tv_history_cal );

        refreshMeFragmentUi();

        rlHistrorySleep.setOnClickListener(this);
        rlHistrorySprot.setOnClickListener(this);


        return meView;
    }

    public void refreshMeFragmentUi(){
    	new MyAsyncTask().execute();
    }
    
    

    

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.rl_history_sleep:
                if (mOnMeFragmentClickListener != null) {
                    mOnMeFragmentClickListener.doLookSleepHistory();
                }
                break;
            case R.id.rl_history_sport:
                if (mOnMeFragmentClickListener != null) {
                    mOnMeFragmentClickListener.doLookSportHistory();
                }
                break;
        }

    }


    public interface OnMeFragmentClickListener{
        void doLookSleepHistory();
        void doLookSportHistory();
    }

    private OnMeFragmentClickListener mOnMeFragmentClickListener;
    public void setOnMeFragmentClickListener(OnMeFragmentClickListener mOnMeFragmentClickListener){
        this.mOnMeFragmentClickListener = mOnMeFragmentClickListener;
    }
    
    private class MyAsyncTask extends AsyncTask<Void, Void, String[]>{

		@Override
		protected String[] doInBackground(Void... params) {
				
		
			int totalStep = LitePalManager.instance().getTotalStep();
	    	int time = LitePalManager.instance().getTotalSportTime();
	    	if (getActivity()!=null) {
//	    	   	String distanceStr = DataUtil.format(DataUtil.getDistanceValue(totalStep))+getActivity().getString(R.string.km);
	    	   	String distanceStr = DataUtil.getDistanceWithUnit(totalStep,getActivity());
		    	String sportTimeStr = DateUtil.formateTime2(time,getActivity());
		    	String kalStr = DataUtil.getKalWithUnit(totalStep, getContext());
		    	String stepStr = String.valueOf(totalStep) + getString(R.string.step);
		    	return new String[]{distanceStr,sportTimeStr,kalStr,stepStr};
			}else{
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			if (result!=null) {
				 tvHistoryDistance.setText(result[0]);
		         tvHistoryTime.setText(result[1]);
		         tvHistoryCal.setText(result[2]);
		         tvHistoryStep.setText(result[3]);
			}
		}

    	
    }
}
