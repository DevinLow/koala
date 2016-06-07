package com.ospicon.koalafinaltestapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.Handler;

import com.ospicon.koalasdk.KoalaSDK;
import com.ospicon.koalasdk.command.KoalaInterface;
import com.ospicon.koalasdk.dataObject.KSensorUpdate;
import com.ospicon.koalasdk.dataObject.KStatusUpdate;


public class MainActivity extends AppCompatActivity implements KoalaInterface {

    public static KoalaSDK koalaSDK;

    public Button btConnect;

    public static int mConnectStatus = 1; // 0 = connect (no error), 1 = disconnect

    public static final String DISCONNECT = "Disconnect";
    public static final String CONNECT = "Connect";
    public static final String CONNECTING = "Connecting";

    Handler handler=new Handler();

    public String mModelName="";
    public String mKoalaMcuVersion="";
    public String mKoalaBtVersion="";
    public String mKoalaTime="";
    public String mCountkoalaTime="";
    public int mSleepState;
    public int mBpm;
    public int mTemperature;
    public int mFiber;
    public int mSound;
    public int mPacketCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        koalaSDK=KoalaSDK.getKoalaSDK(this);
        koalaSDK.setListener(this);

        mConnectStatus=1;// means disconnect

        btConnect = (Button) findViewById(R.id.bt_connect);
        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mConnectStatus==0){
                    koalaSDK.disconnectKoala();
                } else if(mConnectStatus==1 || mConnectStatus==2){
                    koalaSDK.scanKoala(true);
                }
            }
        });
    }
    //Notify the download completed for the day (year, month, day) (callback)
    @Override
    public void sleepLogCompleted(int year, int month, int day) {

    }

    @Override
    public void sleepLogDataError(int year, int month, int day) {
        koalaSDK.downloadSleepLogByDay(year, month, day);
    }

    @Override
    public void sleepLogUpdatePacketCount(int year, int month, int day, int count) {
        mPacketCount=count;
        mCountkoalaTime=year+"-"+month+"-"+day;
    }

    @Override
    public void statusUpdate(KStatusUpdate status) {
        mSleepState=status.sleepState;
        mBpm=status.bpm;
        mTemperature=status.temperature;
    }

    @Override
    public void sensorUpdate(KSensorUpdate sensor) {
        mFiber=sensor.fiber;
        mSound=sensor.sound;
    }
//    Message = description of status
//    Status 0 = connected, 1 = disconnected, 2 = failed
    @Override
    public void connectionStatus(int status, String message) {
        mConnectStatus = status;
        handler.post(new Runnable(){
            @Override
            public void run() {
                if (mConnectStatus == 0) {
                    btConnect.setText(DISCONNECT);
                } else if(mConnectStatus == 1 || mConnectStatus == 2 ) {
                    btConnect.setText(CONNECT);
                } else if(mConnectStatus == 3 ) {
                    btConnect.setText(CONNECTING);
                }
            }
        });
    }

    @Override
    public void koalaTime(int year, int month, int day, int hour, int minute, int second) {
         mKoalaTime = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
    }

    @Override
    public void koalaBtVersion(String version) {
        mKoalaBtVersion=version;
    }

    @Override
    public void koalaMcuVersion(String version) {
        mKoalaMcuVersion=version;
    }

    @Override
    public void koalaModelName(String modelName) {
        mModelName=modelName;
    }

    @Override
    public void koalaDeviceFound(String name, String mac) {
        if(name.toLowerCase().contains("safetosleep")){
            Log.e("DEVICE" , "Device Found with mac: "+ name + " " + mac);
            // Toast.makeText(context ,  "Device Found with mac: "+ name + " " + mac, Toast.LENGTH_SHORT).show();
            koalaSDK.connectKoala(mac);
        }
    }
}
