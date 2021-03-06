package com.ospicon.koalafinaltestapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.CountDownTimer;
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
import android.widget.Toast;

import com.ospicon.koalasdk.KoalaSDK;
import com.ospicon.koalasdk.command.KoalaInterface;
import com.ospicon.koalasdk.dataObject.KSensorUpdate;
import com.ospicon.koalasdk.dataObject.KStatusUpdate;

import java.io.IOException;
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
    public LinearLayout l_matmodel;
//    public TextView tv_matrssi;
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
    public String mLogModel="";
    public String text="";
    ConfigInfo configInfo=new ConfigInfo();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btConnect = (Button) findViewById(R.id.bt_connect);
        configInfo.createExternalStorageDir(this, configInfo.mDirPath);
        configInfo.creatLogFile(this, configInfo.mLogFileFail);
        configInfo.creatLogFile(this,configInfo.mLogFilePass);
        if(configInfo.fileExist(configInfo.mFilePath)) {
            configInfo.ReadTxtFile(configInfo.mFilePath);
            if(configInfo.mStrModel.equals("1")){
                mLogModel="MODEL\t\t";
            }else {
                mLogModel="";
            }
            try {
                configInfo.writeFileTitle(this,configInfo.mLogFileFail,"BTAddress\t\t\t\t\tName\t\t\t\t\t\tMATFW\t\tBTFW\t"+mLogModel+"SOUNDL0\tSOUNDL1\tBR\tTEMP\tNOBR\tOUTOFMAT\tFACRESET\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                configInfo.writeFileTitle(this,configInfo.mLogFilePass,"BTAddress\t\t\t\t\tName\t\t\t\t\t\tMATFW\t\tBTFW\t"+mLogModel+"SOUNDL0\tSOUNDL1\tBR\tTEMP\tNOBR\tOUTOFMAT\tFACRESET\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            koalaSDK=KoalaSDK.getKoalaSDK(this);
            koalaSDK.setListener(this);
            mConnectStatus=1;// means disconnect
        }else {
            btConnect.setVisibility(View.INVISIBLE);
            Toast toast=Toast.makeText(this, "config file is not exist", Toast.LENGTH_SHORT);
            toast.show();
        }
        initAccessoryWindow();
        initResultWindow();
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
        l_matmodel=(LinearLayout)findViewById(R.id.l_matmodel);
        tv_matmodel=(TextView)findViewById(R.id.tv_matmodel);
//        tv_matrssi=(TextView)findViewById(R.id.tv_matrssi);
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
        final CountDownTimer timer4 = new CountDownTimer((Integer.parseInt(configInfo.mStrOutOfMatTime)*1000),1000){

            @Override
            public void onTick(long millisUntilFinished) {
                tv_outofmat.setText("" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                if(mSleepState==0){
                    text+="PASS\t";
                    tv_outofmat.setText("PASS");
                    if(koalaSDK.resetKoala()){
                        tv_factroyreset.setText("PASS");
                        text+="PASS\n";
                        tv_result.setText("PASS");
                        configInfo.writeLog(MainActivity.this, configInfo.mLogFilePass, text);
                    }else {
                        text+="Fail\n";
                        tv_factroyreset.setText("Fail\n");
                        tv_factroyreset.setTextColor(Color.RED);
                        configInfo.writeLog(MainActivity.this, configInfo.mLogFileFail, text);
                    }
                }else{
                    text+="Fail...Error 9\n";
                    tv_outofmat.setText("Fail...Error 9");
                    tv_outofmat.setTextColor(Color.RED);
                    configInfo.writeLog(MainActivity.this, configInfo.mLogFileFail, text);
                }
            }
        };
        final CountDownTimer timer3 = new CountDownTimer((Integer.parseInt(configInfo.mStrWaitTime)*1000),1000){

            @Override
            public void onTick(long millisUntilFinished) {
                tv_outofmat.setText("" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("請移除重量");
                builder.setTitle("提示");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                timer4.start();
            }
        };
        final CountDownTimer timer2 = new CountDownTimer((Integer.parseInt(configInfo.mStrNoBreathTime))*1000,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                tv_nobreath.setText(""+millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                if(mSleepState==3){
                    text+="PASS\t";
                    tv_nobreath.setText("PASS");
                    bt_test2.setTextColor(Color.BLACK);
                    bt_test2.setClickable(true);
                }else {
                    text+="Fail...Error 8\n";
                    tv_nobreath.setText("Fail...Error 8");
                    tv_nobreath.setTextColor(Color.RED);
                    configInfo.writeLog(MainActivity.this, configInfo.mLogFileFail, text);
                }
            }

        };
        final CountDownTimer timer1=new CountDownTimer(Integer.parseInt(configInfo.mStrBreathCountDown)*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_breath.setText("" + millisUntilFinished / 1000);
            }
            @Override
            public void onFinish() {
                if(mBpm<=Integer.parseInt(configInfo.mStrBreathHigh)&&mBpm>=Integer.parseInt(configInfo.mStrBreathLow)){
                    text+=Integer.toString(mBpm)+"\t";
                    tv_breath.setText(Integer.toString(mBpm));
                    if(mTemperature>Integer.parseInt(configInfo.mStrTemperatureLow)&&mTemperature<Integer.parseInt(configInfo.mStrTemperatureHigh)){
                        text+=Integer.toString(mTemperature)+"\t";
                        tv_temperature.setText(Integer.toString(mTemperature));
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("请关闭马达");
                        builder.setTitle("提示");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                timer2.start();
                            }
                        });
                        builder.create().show();
                    }else {
                        text+=Integer.toString(mTemperature)+"...Error 5\n";
                        tv_temperature.setText(Integer.toString(mTemperature)+"...Error 5");
                        tv_temperature.setTextColor(Color.RED);
                        configInfo.writeLog(MainActivity.this, configInfo.mLogFileFail, text);
                    }
                }else {
                    text+=Integer.toString(mBpm)+"...Error 7\n";
                    tv_breath.setText(Integer.toString(mBpm)+"...Error 7");
                    tv_breath.setTextColor(Color.RED);
                    configInfo.writeLog(MainActivity.this, configInfo.mLogFileFail, text);
                }
            }
        };

        bt_test1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                bt_test1.setTextColor(Color.GRAY);
                bt_test1.setClickable(false);
                if(mSound<Integer.parseInt(configInfo.mStrSoundLow)){
                    text+=Integer.toString(mSound)+"\t";
                    tv_soundlvllow.setText(Integer.toString(mSound));
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("请打开音箱");
                    builder.setTitle("提示");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if(mSound>=Integer.parseInt(configInfo.mStrSoundHigh)){
                                tv_soundlvlhigh.setText(Integer.toString(mSound));
                                text+=Integer.toString(mSound)+"\t";
                                timer1.start();
                            }else {
                                text+=Integer.toString(mSound) + "...Error 6.2\n";
                                tv_soundlvlhigh.setText(Integer.toString(mSound) + "...Error 6.2");
                                tv_soundlvlhigh.setTextColor(Color.RED);
                                configInfo.writeLog(MainActivity.this, configInfo.mLogFileFail, text);
                            }
                        }
                    });
                    builder.create().show();
                }else{
                    text+=Integer.toString(mSound)+"...Error 6.1\n";
                    tv_soundlvllow.setText(Integer.toString(mSound)+"...Error 6.1");
                    tv_soundlvllow.setTextColor(Color.RED);
                    configInfo.writeLog(MainActivity.this,configInfo.mLogFileFail,text);
                }
            }
        });
        bt_test2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                timer3.start();
            }
        });
    }
    public void showResultWindow(){
        layoutResutWindow.setVisibility(View.VISIBLE);
        bt_test1.setTextColor(Color.GRAY);
        bt_test1.setClickable(false);
        bt_test2.setTextColor(Color.GRAY);
        bt_test2.setClickable(false);
        if(configInfo.mStrModel.equals("1")){
            l_matmodel.setVisibility(View.VISIBLE);
        }else {
            l_matmodel.setVisibility(View.INVISIBLE);
        }
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
        handler.post(new Runnable(){
            @Override
            public void run() {
                if (mConnectStatus == 0) {
                    layoutAccessoryWindow.setVisibility(View.GONE);
                    showResultWindow();
                    verifyTask();
                } else if( mConnectStatus == 2 ) {
                    layoutAccessoryWindow.setVisibility(View.GONE);
                    btConnect.setVisibility(View.VISIBLE);
                } else if(mConnectStatus == 1 ) {
                    btConnect.setVisibility(View.VISIBLE);
                    layoutAccessoryWindow.setVisibility(View.GONE);
                    layoutResutWindow.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void verifyTask(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                koalaSDK.enableLiveUpdate(true);
            }
        },200);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mKoalaMcuVersion=koalaSDK.getKoalaMcuVersion();
            }
        },400);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mKoalaBtVersion=koalaSDK.getKoalaBtVersion();
            }
        },800);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mModelName=koalaSDK.getKoalaModel();
            }
        },1000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                StartTestFlow();
            }
        }, 7000);
    }

    private void StartTestFlow() {
        text="";
        tv_matname.setText(deviceAdapter.getDeviceName());
        tv_btaddr.setText(koalaSDK.getKoalaAddress());
        text+=koalaSDK.getKoalaAddress()+"\t";
        text+=deviceAdapter.getDeviceName()+"\t";
        if(configInfo.mStrMatFwVersion.equals(mKoalaMcuVersion)){
            text+=mKoalaMcuVersion+"\t";
            tv_matfw.setText(mKoalaMcuVersion);
            if(configInfo.mStrBtFwversion.equals(mKoalaBtVersion)){
                text+=mKoalaBtVersion+"\t";
                tv_btfw.setText(mKoalaBtVersion);
                if(configInfo.mStrModel.equals("1")) {
                    if(configInfo.mStrMatModelName.equals(mModelName)){
                        text+=mModelName+"\t";
                        tv_matmodel.setText(mModelName);
                        bt_test1.setClickable(true);
                        bt_test1.setTextColor(Color.BLACK);
                    }else {
                        text+=mModelName+"...Error 3\n";
                        tv_matmodel.setText(mKoalaBtVersion+"...Error 3");
                        tv_matmodel.setTextColor(Color.RED);
                        configInfo.writeLog(this,configInfo.mLogFileFail,text);
                    }
                }else {
                    bt_test1.setClickable(true);
                    bt_test1.setTextColor(Color.BLACK);
                }
            }else{
                text+=mKoalaBtVersion+"...Error 2\n";
                tv_btfw.setText(mKoalaBtVersion+"...Error 2");
                tv_btfw.setTextColor(Color.RED);
                configInfo.writeLog(this,configInfo.mLogFileFail,text);
            }
        }else{
            text+=mKoalaMcuVersion+"...Error 1\n";
            tv_matfw.setText(mKoalaMcuVersion+"...Error 1");
            tv_matfw.setTextColor(Color.RED);
            configInfo.writeLog(this,configInfo.mLogFileFail,text);
        }

    }

    @Override
    public void koalaTime(int year, int month, int day, int hour, int minute, int second) {
         mKoalaTime = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
    }

    @Override
    public void koalaBtVersion(String version) {
        mKoalaBtVersion=version;
        tv_btfw.setText(version);
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
            BleDevice bleDevice = new BleDevice(Parcel.obtain());
            bleDevice.name=name;
            bleDevice.addr=mac;
            MainActivity.refreshDeviceList(bleDevice);
        }
    }
}
