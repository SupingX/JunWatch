package com.mycj.junsda.bean;

import org.litepal.crud.DataSupport;

import android.os.Parcel;
import android.os.Parcelable;

public class HistorySleep extends DataSupport implements Parcelable{
	private String date;
	private int deep;// 单位：分钟
	private int light;// 单位：分钟

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getDeep() {
		return deep;
	}

	public void setDeep(int deep) {
		this.deep = deep;
	}

	public int getLight() {
		return light;
	}

	public void setLight(int light) {
		this.light = light;
	}

	public HistorySleep(String date, int deep, int light) {
		super();
		this.date = date;
		this.deep = deep;
		this.light = light;
	}

	public HistorySleep() {
		super();
	}

	@Override
	public String toString() {
		return "HistorySleep [date=" + date + ", deep=" + deep + ", light=" + light + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.date);
		dest.writeInt(this.deep);
		dest.writeInt(this.light);
	}
	
	public static final Parcelable.Creator<HistorySleep> CREATOR = new Creator<HistorySleep>() {
		
		@Override
		public HistorySleep[] newArray(int size) {
			return new HistorySleep[size];
		}
		
		@Override
		public HistorySleep createFromParcel(Parcel source) {
			String date = source.readString();
			int deep = source.readInt();
			int light = source.readInt();
			return new HistorySleep(date, deep, light);
		}
	};

}
