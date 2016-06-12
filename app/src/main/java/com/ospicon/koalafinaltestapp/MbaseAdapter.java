package com.ospicon.koalafinaltestapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2016/6/10.
 */
public class MbaseAdapter extends BaseAdapter {
    Activity activity;
    List<BleDevice> data;
    String deviceName="";
    String deviceAddress = "";
    static private int selectedIndex = -1;
    public MbaseAdapter(Activity activity, List<BleDevice> data){
        this.activity = activity;
        this.data = data;
    }
    public String getDeviceName(){
        return  deviceName;
    }
    public String getDeviceAddress(){
        return deviceAddress;
    }
    public static void setSelectedIndex(int ind) {
        selectedIndex = ind;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BleDevice device= (BleDevice) getItem(position);
        deviceName = "" + device.name;
        deviceAddress = "" + device.addr;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_accessory_window, null);
        }
        TextView textBluetoothName = (TextView) convertView.findViewById(R.id.text_bluetooth_name);
        //Log.d(TAG, "device : " + deviceName + ", address: " + deviceAddress);
        textBluetoothName.setText(deviceName);

        ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        if(selectedIndex == position){
            progressBar.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}
