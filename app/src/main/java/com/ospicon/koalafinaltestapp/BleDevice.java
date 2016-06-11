package com.ospicon.koalafinaltestapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/6/11.
 */
public class BleDevice implements Parcelable {
    public String name;
    public String addr;

    protected BleDevice(Parcel in) {
        name = in.readString();
        addr = in.readString();
    }

    public static final Creator<BleDevice> CREATOR = new Creator<BleDevice>() {
        @Override
        public BleDevice createFromParcel(Parcel in) {
            return new BleDevice(in);
        }

        @Override
        public BleDevice[] newArray(int size) {
            return new BleDevice[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(addr);
    }
}
