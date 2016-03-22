package com.mycj.junsda.adapter;


import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycj.junsda.R;

/**
 * Created by Administrator on 2015/11/20.
 */
public class DeviceAdapterNew extends BaseAdapter{
    private Context context;
    private int connectingPos = -1;
    private boolean isConnecting = false;
    private List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
	private RotateAnimation animation;
    public DeviceAdapterNew(Context context,List<BluetoothDevice> devices){
        this.context = context;
        this.devices = devices;
    }
    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int i) {
        return 0;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHodler holder =null;
        if (view==null){
            view = LayoutInflater.from(context).inflate(R.layout.item_device_new,viewGroup,false);
            holder = new ViewHodler();
            holder.tvName = (TextView)view.findViewById(R.id.tv_device_name);
            holder.tvConnectingInfo = (TextView)view.findViewById(R.id.tv_connecting_info);
            holder.imgConnecting = (ImageView) view.findViewById(R.id.img_connecting);
            view.setTag(holder);
        }else{
            holder = (ViewHodler)view.getTag();
        }

        BluetoothDevice device = devices.get(i);
        
        String name = device.getName();;
        String address = device.getAddress();
        if (device.getName()==null||device.getName().equals("")) {
			name=viewGroup.getContext().getString(R.string.nuknow);
		}
        holder.tvName.setText(name + " " +address.substring(address.length()-5, address.length()));
        
        if (i==connectingPos) {
			if (isConnecting) {
				holder.tvConnectingInfo.setVisibility(View.INVISIBLE);
			    holder.imgConnecting.setVisibility(View.VISIBLE);
			    startAnimation(holder.imgConnecting);
			}else{
				holder.imgConnecting.setVisibility(View.INVISIBLE);
				stopAnimation(holder.imgConnecting);
				holder.tvConnectingInfo.setVisibility(View.VISIBLE);
				holder.tvConnectingInfo.setText("已连接");
			}
		}else{
			holder.tvConnectingInfo.setVisibility(View.VISIBLE);
			holder.imgConnecting.setVisibility(View.INVISIBLE);
			holder.tvConnectingInfo.setText("未连接");
			stopAnimation(holder.imgConnecting);
		}
        
        return view;
    }

    private void startAnimation(ImageView imgConnecting) {
//		imgConnecting.animate().rotation(360).setDuration(2000).start();
    	ObjectAnimator animation = ObjectAnimator.ofFloat(imgConnecting, "rotation", 0,360);
    	
//    	
//    	animation = new RotateAnimation(0f,360f,Animation.RELATIVE_TO_SELF,
//    			0.5f,Animation.RELATIVE_TO_SELF,0.5f);
    	animation.setDuration(2000);//设置动画持续时间 
    	animation.setInterpolator(new LinearInterpolator());
    	animation.setRepeatCount(-1);
    	animation.start();
    	
	}
    
    public void stopAnimation(ImageView img){
//    	img.animate().cancel();
    	if (animation!=null) {
    		animation.cancel();
		}
    }
    
	public void setConnectPos(int pos){
    	this.connectingPos = pos;
    	notifyDataSetChanged();
    }
    
    public void setIsConecting(boolean isConnecting){
    	this.isConnecting = isConnecting;
    	notifyDataSetChanged();
    }
    
    public void clear(){
    	this.connectingPos = -1;
    	this.isConnecting = false;
    }
    
    class ViewHodler {
        TextView tvName;
        TextView tvConnectingInfo;
        ImageView imgConnecting;
    }
}
