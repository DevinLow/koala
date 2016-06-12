package com.ospicon.koalafinaltestapp;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ospicon.koalasdk.KoalaSDK;
import com.ospicon.koalasdk.command.KoalaInterface;
import com.ospicon.koalasdk.dataObject.KSensorUpdate;
import com.ospicon.koalasdk.dataObject.KStatusUpdate;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements KoalaInterface {

    public static KoalaSDK koalaSDK;

    public Button btConnect;

    public static int mConnectStatus = 1; // 0 = connect (no error), 1 = disconnect

    public static final String DISCONNECT = "Disconnect";
    public static final String CONNECT = "Connect";
    public static final String CONNECTING = "Connecting";

    Handler handler=new Handler();
    static LinearLayout layoutAccessoryWindow;
    static ListView listViewBluetoothList;
    static MbaseAdapter  deviceAdapter;
    static List<BleDevice> deviceList = new ArrayList<>();

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

    public LinearLayout layoutResutWindow;
   // public TextView tv_title;
    public TextView tv_matname;
    public TextView tv_btaddr;
    public TextView tv_matfw;
    public TextView tv_btfw;
    public TextView tv_matmodel;
    public TextView tv_matrssi;
    public TextView tv_soundlvllow;
    public TextView tv_soundlvlhigh;
    public TextView tv_breath;
    public TextView tv_temperature;
    public TextView tv_nobreath;
    public TextView tv_outofmat;
    public TextView tv_factroyreset;
    public TextView tv_result;
    public Button bt_test1;
    public Button bt_test2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        koalaSDK=KoalaSDK.getKoalaSDK(this);
        koalaSDK.setListener(this);

        mConnectStatus=1;// means disconnect
        initAccessoryWindow();
        initResultWindow();
        btConnect = (Button) findViewById(R.id.bt_connect);
        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConnectStatus == 0) {
                    koalaSDK.disconnectKoala();
                } else if (mConnectStatus == 1 || mConnectStatus == 2) {
                    koalaSDK.scanKoala(true);
                }
                showAccessoryWindow();
                btConnect.setVisibility(View.GONE);
            }
        });
    }
    private void initAccessoryWindow(){
        layoutAccessoryWindow = (LinearLayout) findViewById(R.id.layout_accessory_window);
        layoutAccessoryWindow.setVisibility(View.GONE);

        TextView textSelectAccessory = (TextView) findViewById(R.id.text_select_accessory);
        textSelectAccessory.setTransformationMethod(null);


        Button btnCancelAccessory = (Button) findViewById(R.id.btn_cancel_accessory);
        btnCancelAccessory.setTransformationMethod(null);
        btnCancelAccessory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btConnect.setVisibility(View.VISIBLE);
                layoutAccessoryWindow.setVisibility(View.INVISIBLE);
            }
        });
        deviceAdapter = new MbaseAdapter(this, deviceList);
        listViewBluetoothList = (ListView) findViewById(R.id.listView_bluetooth_list);
        listViewBluetoothList.setAdapter(deviceAdapter);
        listViewBluetoothList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deviceAdapter.setSelectedIndex(position);
                deviceAdapter.notifyDataSetChanged();
                BleDevice device = deviceList.get(position);
                koalaSDK.connectKoala(device.addr);
            }
        });
    }
    public static void showAccessoryWindow() {
        deviceList.clear();
        deviceAdapter.setSelectedIndex(-1);
        deviceAdapter.notifyDataSetChanged();
        layoutAccessoryWindow.setVisibility(View.VISIBLE);
        layoutAccessoryWindow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }
    public static void refreshDeviceList(BleDevice device){
        //Log.d(TAG, "refreshDeviceList - 1");
        if(!deviceList.contains(device)) {
            deviceList.add(device);
        }
        deviceAdapter.notifyDataSetChanged();
    }
    private void initResultWindow(){
        layoutResutWindow=(LinearLayout)findViewById(R.id.layout_result_window);
        layoutResutWindow.setVisibility(View.INVISIBLE);
        tv_matname=(TextView)findViewById(R.id.tv_matname);
        tv_btaddr=(TextView)findViewById(R.id.tv_btaddr);
        tv_matfw=(TextView)findViewById(R.id.tv_matfw);
        tv_btfw=(TextView)findViewById(R.id.tv_btfw);
        tv_matmodel=(TextView)findViewById(R.id.tv_matmodel);
        tv_matrssi=(TextView)findViewById(R.id.tv_matrssi);
        tv_soundlvllow=(TextView)findViewById(R.id.tv_soundlvllow);
        tv_soundlvlhigh=(TextView)findViewById(R.id.tv_soundlvlhigh);
        tv_breath=(TextView)findViewById(R.id.tv_breath);
        tv_temperature=(TextView)findViewById(R.id.tv_temperature);
        tv_nobreath=(TextView)findViewById(R.id.tv_nobreath);
        tv_outofmat=(TextView)findViewById(R.id.tv_outofmat);
        tv_factroyreset=(TextView)findViewById(R.id.tv_factroyreset);
        tv_result=(TextView)findViewById(R.id.tv_result);
        bt_test1=(Button)findViewById(R.id.bt_test1);
        bt_test2=(Button)findViewById(R.id.bt_test2);
        bt_test1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

            }
        });
        bt_test2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

            }
        });
    }
    public void showResultWindow(){
        layoutResutWindow.setVisibility(View.VISIBLE);
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
    public void connectionStatus(int status, final String message) {
        mConnectStatus = status;
      //  Log.e("mConnectStatus",""+mConnectStatus+message);
        handler.post(new Runnable(){
            @Override
            public void run() {
                if (mConnectStatus == 0) {
                    //btConnect.setText(DISCONNECT);
                    layoutAccessoryWindow.setVisibility(View.GONE);
                    showResultWindow();
                } else if(mConnectStatus == 1 || mConnectStatus == 2 ) {
                    layoutAccessoryWindow.setVisibility(View.GONE);
                    btConnect.setVisibility(View.VISIBLE);
                } else if(mConnectStatus == 3 ) {
                  //  btConnect.setText(CONNECTING);
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
          //  Log.e("DEVICE" , "Device Found with mac: "+ name + " " + mac);
            // Toast.makeText(context ,  "Device Found with mac: "+ name + " " + mac, Toast.LENGTH_SHORT).show();
            BleDevice bleDevice = new BleDevice(Parcel.obtain());
            bleDevice.name=name;
            bleDevice.addr=mac;
            MainActivity.refreshDeviceList(bleDevice);
        }
    }
}
