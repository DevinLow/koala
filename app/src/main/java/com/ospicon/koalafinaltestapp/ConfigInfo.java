package com.ospicon.koalafinaltestapp;

/**
 * Created by DevinLiu on 2016/6/12.
 */
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2016/6/4.
 */
public class ConfigInfo {
    //file path
    static public String mFilePath="/KOAFinalconfig";
    static public String mDirPath="/KOAFinalLog";
    static public String mLogFilePass="/KOAFinalLog/KOAFinalP.txt";
    static public String mLogFileFail="/KOAFinalLog/KOAFinalF.txt";

    //config file info
    static public String mStrSts="";
    static public String mStrNoBreath="";
    static public String mStrOutOfMat="";
    static public String mStrFactoryReset="";
    static public String mStrMatFwVersion="";
    static public String mStrBtFwversion="";
    static public String mStrMatModelName="";
    static public String mStrMatRssi="";
    static public String mStrSoundLevel="";
    static public String mStrTemperatureLow="";
    static public String mStrTemperatureHigh="";
    static public String mStrBreathLow="";
    static public String mStrBreathHigh="";
    static public String mStrNoBreathTime="";
    static public String mStrWaitTime="";
    static public String mStrOutOfMatTime="";
    static public String mStrSoundLow="";
    static public String mStrSoundHigh="";
    static public String mStrBreathCountDown="";
    static public String mStrModel="";

    //写文件
    private void writeSDFile(String fileName, String write_str) throws IOException {

        File file = new File(fileName);

        FileOutputStream fos = new FileOutputStream(file);

        byte [] bytes = write_str.getBytes();

        fos.write(bytes);

        fos.close();
    }
    private void method(Context context,String fileName, String content) {
        String path=Environment.getExternalStorageDirectory().getPath();
        path=path+fileName;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            File file=new File(path);
            FileWriter writer = new FileWriter(path,true);
            writer.write(content);
            writer.close();
            MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null,null);
            String parrentPtach=Environment.getExternalStorageDirectory().getPath();
            parrentPtach=parrentPtach+mDirPath;
            File dir=new File(parrentPtach);
            MediaScannerConnection.scanFile(context, new String[]{dir.toString()}, null,null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void creatLogFile(Context context,String filename){
        String path=Environment.getExternalStorageDirectory().getPath();
        path=path+filename;
        File file=new File(path);
        if(!file.exists()){
            try {
                file.createNewFile();
                MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null,null);
                String parrentPtach=Environment.getExternalStorageDirectory().getPath();
                parrentPtach=parrentPtach+mDirPath;
                File dir=new File(parrentPtach);
                MediaScannerConnection.scanFile(context, new String[]{dir.toString()}, null,null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null,null);
        }
    }
    public boolean ReadTxtFile(String strFilePath)
    {
        //打开文件
        String path=Environment.getExternalStorageDirectory().getPath();
        path=path+strFilePath;
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory())
        {
            Log.e("TestFile", "The File doesn't not exist.");
            return false;
        }
        else
        {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null)
                {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while (( line = buffreader.readLine()) != null) {
                        int location=line.indexOf('=');
                        String left=line.substring(0,location);
                        String right=line.substring(location + 1, line.length());
                        left.trim();
                        if(left.equals("STS")){
                            mStrSts=right;
                        }else if(left.equals("NOBREATH")){
                            mStrNoBreath=right;
                        }else if(left.equals("OUTOFMAT")){
                            mStrOutOfMat=right;
                        }else if(left.equals("FACTORYRESET")){
                            mStrFactoryReset=right;
                        }else if(left.equals("MATFW")){
                            mStrMatFwVersion=right;
                            Log.e("MATFW",mStrMatFwVersion);
                        }else if(left.equals("BTFW")){
                            mStrBtFwversion=right;
                        }else if(left.equals("MATMODEL")){
                            mStrMatModelName=right;
                        }else if(left.equals("RSSI")){
                            mStrMatRssi=right;
                        }else if(left.equals("SOUNDLVL")){
                            mStrSoundLevel=right;
                        }else if(left.equals("TEMPERATURELO")){
                            mStrTemperatureLow=right;
                            Log.e("TEMPERATURELO",mStrTemperatureLow);
                        }else if(left.equals("TEMPERATUREHI")){
                            mStrTemperatureHigh=right;
                            Log.e("TEMPERATUREHI",mStrTemperatureHigh);
                        } else if(left.equals("BR_LO")){
                            mStrBreathLow=right;
                        }else if(left.equals("BR_HI")){
                            mStrBreathHigh=right;
                        }else if(left.equals("NOBR_TIME")){
                            mStrNoBreathTime=right;
                        }else if(left.equals("WAITTIME")){
                            mStrWaitTime=right;
                        }else if(left.equals("OUTOFMATTIME")){
                            mStrOutOfMatTime=right;
                        }
                        else if(left.equals("SOUNDLO")){
                            mStrSoundLow = right;
                            Log.e("mStrSoundLow",mStrSoundLow);
                        }
                        else if(left.equals("SOUNDHI")){
                            mStrSoundHigh=right;
                            Log.e("mStrSoundLow",mStrSoundLow);
                        }else if(left.equals("BRCOUNTDN")){
                            mStrBreathCountDown=right;
                        }else if(left.equals("MODEL")){
                            mStrModel=right;
                        }
                    }
                    instream.close();

                }
            }
            catch (java.io.FileNotFoundException e) {
                Log.e("TestFile", "The File doesn't not exist.");
            }
            catch (IOException e)
            {
                Log.e("TestFile", e.getMessage());
            }
        }
        return true;
    }
    public boolean fileExist(String filename) {
        String path=Environment.getExternalStorageDirectory().getPath();
        path=path+filename;
        File myFile = new File(path);
        return myFile.exists();
    }
    public void writeFileTitle(Context context, String filename ,String title) throws IOException {
        String path=Environment.getExternalStorageDirectory().getPath();
        path=path+filename;
        File file = new File(path);
        InputStream instream = new FileInputStream(file);
        if (instream != null) {
            InputStreamReader inputreader = new InputStreamReader(instream);
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line=buffreader.readLine();
            if(line==null){
                method(context,filename,title);
            }
        }
    }
    public void writeLog(Context context,String filename ,String log){
        method(context,filename,log);
    }
    void createExternalStorageDir(Context context,String dir) {
        String path=Environment.getExternalStorageDirectory().getPath();
        path=path+mDirPath;
        File file = new File(path);
        if(!file.exists()) {
            file.mkdirs();
            MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null,null);
        }
    }
}
