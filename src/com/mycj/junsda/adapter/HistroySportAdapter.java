package com.mycj.junsda.adapter;

import java.util.ArrayList;
import java.util.List;

import com.mycj.junsda.R;
import com.mycj.junsda.bean.HistorySleep;
import com.mycj.junsda.bean.HistorySport;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HistroySportAdapter extends BaseAdapter{
	private List<HistorySport> datas = new ArrayList<>(); 
	public HistroySportAdapter(List<HistorySport> datas){
		this.datas = datas;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView==null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_sport, parent,false);
			holder = new ViewHolder();
			holder.tvSportDate = (TextView) convertView.findViewById(R.id.tv_sport_date);
			holder.tvSportSteps = (TextView) convertView.findViewById(R.id.tv_sport_steps);
			holder.tvSportTime = (TextView) convertView.findViewById(R.id.tv_sport_time);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		//设置数据
		HistorySport historySport = datas.get(position);
		String date = historySport.getDate();
		int step = historySport.getStep();
		int sportTime = historySport.getSportTime();
		
		holder.tvSportDate.setText(formateDate(date,parent.getContext()));
		holder.tvSportSteps.setText(formateStep(step));
		holder.tvSportTime.setText(formateTime(sportTime));
		
		return convertView;
	}
	
	
	
	private String formateTime(int sportTime) {
		StringBuffer sb = new StringBuffer();
		if (sportTime<60) {
			sb.append("00:00:");
			sb.append(getString(sportTime));
		}else{
			int min = (int) Math.floor(sportTime/60f);
			int second = sportTime%60;
			if (min<60) {
				sb.append("00:")
				.append(getString(min))
				.append(":")
				.append(getString(second));
			}else{
				int hour =(int) Math.floor(min/60f);
				int newMin = min%60;
				sb.append(getString(hour))
					.append(":")
					.append(getString(newMin))
					.append(":")
					.append(getString(second));
			}
		}
		return sb.toString();
	}

	private String formateStep(int step) {
		return String.valueOf(step);
	}

	private String formateDate(String date, Context context) {

		StringBuffer sb = new StringBuffer();
		try {

			String month = date.substring(4, 6);
			String day = date.substring(6, 8);
			Resources resources = context.getResources();
			sb.append(month).append(resources.getString(R.string.month)).append(day).append(resources.getString(R.string.day));
		} catch (Exception e) {
			return "未知";
		}
		return sb.toString();
	}
	
	
	public String getString(int value){
		String valueOf = String.valueOf(value);
		if (valueOf.length()==1) {
			return "0"+valueOf;
		}
		return valueOf;
	}


	public class ViewHolder {
		TextView tvSportDate;
		TextView tvSportSteps;
		TextView tvSportTime;
	}

}
