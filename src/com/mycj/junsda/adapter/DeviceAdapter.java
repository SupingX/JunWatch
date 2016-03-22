package com.mycj.junsda.adapter;


import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mycj.junsda.R;

/**
 * Created by Administrator on 2015/11/20.
 */
public class DeviceAdapter extends BaseAdapter{
    private Context context;
    private List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
    public DeviceAdapter(Context context,List<BluetoothDevice> devices){
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
            view = LayoutInflater.from(context).inflate(R.layout.item_device,viewGroup,false);
            holder = new ViewHodler();
            holder.tvName = (TextView)view.findViewById(R.id.tv_device_name);
            holder.tvAddress = (TextView)view.findViewById(R.id.tv_device_address);
            view.setTag(holder);
        }else{
            holder = (ViewHodler)view.getTag();
        }

        BluetoothDevice device = devices.get(i);
        String name = device.getName();;
        if (device.getName()==null||device.getName().equals("")) {
			name=viewGroup.getContext().getString(R.string.nuknow);
		}
        holder.tvName.setText(name);
        holder.tvAddress.setText(device.getAddress());

        return view;
    }


    class ViewHodler {
        TextView tvName;
        TextView tvAddress;
    }
}
