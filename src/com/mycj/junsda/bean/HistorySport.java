package com.mycj.junsda.bean;

import org.litepal.crud.DataSupport;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * <p>step: 步</P>
 * <p>sportTime:秒</p>
 * <p>dete:19491203</p>
 * @author Administrator
 *
 */
public class HistorySport extends DataSupport implements Parcelable {
	private int step;
	private String date;
	private int sportTime;// 单位:秒

	public HistorySport() {
		super();
	}

	public HistorySport(int step, String date, int sportTime) {
		super();
		this.step = step;
		this.date = date;
		this.sportTime = sportTime;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getSportTime() {
		return sportTime;
	}

	public void setSportTime(int sportTime) {
		this.sportTime = sportTime;
	}

	@Override
	public String toString() {
		return "HistorySport [step=" + step + ", date=" + date + ", sportTime=" + sportTime + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.date);
		dest.writeInt(this.step);
		dest.writeInt(this.sportTime);
	}
	
	public static final Parcelable.Creator<HistorySport> CREATOR = new Creator<HistorySport>() {
		
		@Override
		public HistorySport[] newArray(int size) {
			return new HistorySport[size];
		}
		
		@Override
		public HistorySport createFromParcel(Parcel source) {
			HistorySport historySport =new HistorySport();
			historySport.setDate(source.readString());
			historySport.setStep(source.readInt());
			historySport.setSportTime(source.readInt());
			return historySport;
		}
	};
	

}
