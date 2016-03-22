package com.mycj.junsda.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.litepal.crud.DataSupport;

import android.util.Log;

import com.mycj.junsda.view.DateUtil;

public class LitePalManager {
	private static LitePalManager litePal;

	public static LitePalManager instance() {
		if (litePal == null) {
			litePal = new LitePalManager();
		}
		return litePal;
	}

	private LitePalManager() {

	}
	
	private void log(String msg){
		System.out.println(msg);
	}
	
	public int getTotalStep(){
		Integer sum = DataSupport.sum(HistorySport.class, "step", Integer.class);
		log("累计步数:"+sum);
		return sum;
	}
	public int getTotalSportTime(){
		Integer sum = DataSupport.sum(HistorySport.class, "sportTime", Integer.class);
		log("累计时间:"+sum);
		return sum;
	}
	

	
	public List<HistorySleep> findHistroySleepByDate(Date date) {
		String dateCondition = DateUtil.dateToString(date, "yyyyMM") + "%";
		List<HistorySleep> datas = DataSupport.where("date like ?", dateCondition).find(HistorySleep.class);
		sys("日期 ："+DateUtil.dateToString(date, "yyyyMM") + "下的睡眠数据-->" + datas);
		return datas;
	}

	public int findHistroySportStepsByDate(Date date) {
		String dateCondition = DateUtil.dateToString(date, "yyyyMM") + "%";
		int sum = DataSupport.where("date like ?", dateCondition).sum(HistorySport.class, "step", Integer.class);
		return sum;
	}

	public int findHistroySportTimeByDate(Date date) {
		String dateCondition = DateUtil.dateToString(date, "yyyyMM") + "%";
		int sum = DataSupport.where("date like ?", dateCondition).sum(HistorySport.class, "sportTime", Integer.class);
		return sum;
	}

	public float findHistroySleepDeepByDate(Date date) {
		String dateCondition = DateUtil.dateToString(date, "yyyyMM") + "%";
		int sum = DataSupport.where("date like ?", dateCondition).sum(HistorySleep.class, "deep", Integer.class);
		return sum;
	}

	public float findHistroySleepLightByDate(Date date) {
		String dateCondition = DateUtil.dateToString(date, "yyyyMM") + "%";
		int sum = DataSupport.where("date like ?", dateCondition).sum(HistorySleep.class, "light", Integer.class);
		return sum;
	}

	public List<HistorySport> findHistroySportByDate(Date date) {
		String dateCondition = DateUtil.dateToString(date, "yyyyMM") + "%";
		List<HistorySport> datas = DataSupport.where("date like ?", dateCondition).find(HistorySport.class);
		return datas;
	}

	public int findHistroySportCountByDate(Date date) {
		String dateCondition = DateUtil.dateToString(date, "yyyyMM") + "%";
		int count = DataSupport.where("date like ?", dateCondition).count(HistorySport.class);
		return count;
	}
	
	
	private void sys(String msg){
		System.out.println(msg);
	}

}
