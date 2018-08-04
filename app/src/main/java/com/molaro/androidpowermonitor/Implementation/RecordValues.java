package com.molaro.androidpowermonitor.Implementation;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.widget.Toast;

import java.io.File;

public class RecordValues extends IntentService {

    // Defines a custom Intent action
    public static final String BROADCAST_ACTION = "com.molaro.androidpowermonitor.BROADCAST";
    // Defines the key for the status "extra" in an Intent
    public static final String EXTENDED_DATA_STATUS = "com.molaro.androidpowermonitor.STATUS";

    private final int BRIGHTNESS_INTERVAL = 15;
    private final int SAMPLE_SIZES = 2;
    private int SAMPLE_SECONDS = 12;

    private double avg_mA;
    private double avg_mV;

    private String packageName;
    private boolean isApp;

    private Context context;
    //private StoreData sd;

    private int brightness = 0;
    private int time = 0;

    public RecordValues(){
        super(RecordValues.class.getName());
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        onTaskRemoved(intent);
        context = getApplicationContext();
        packageName = intent.getStringExtra("packagename");
        boolean isApp = intent.getBooleanExtra("isapp", false);

        String collectStats = intent.getStringExtra("collectstats");
        String sampleTime = intent.getStringExtra("sampletime");
        if(!sampleTime.isEmpty())
            SAMPLE_SECONDS = Integer.parseInt(sampleTime);
        if(collectStats.equals("collectstats")){
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            baseStats();
            //testBrightness();
            //testOther("Wifi");
            //testOther("Data");
            //testOther("GPS");
            //testOther("Long");
        }
        else{
            applicationStats();
        }
        Intent returnIntent = context.getPackageManager().getLaunchIntentForPackage("com.molaro.androidpowermonitor");
        startActivity(returnIntent);
    }

    private void baseStats(){

        StoreData sd = new StoreData(context);
        BatteryData bd = new BatteryData(context);

        powerTest();

        double avg_mAh = bd.getmAh();

        String toWrite = "";

        toWrite = "mA: " + avg_mA;

        sd.replaceLine("BaseUsage.txt", toWrite, "mA");
        sd.writeToFile("BaseUsage.txt", toWrite);

        toWrite = "mV: " + avg_mV;

        sd.replaceLine("BaseUsage.txt", toWrite, "mV");
        sd.writeToFile("BaseUsage.txt", toWrite);

        toWrite = "mW: " + ((avg_mV * avg_mA)/1000);

        sd.replaceLine("BaseUsage.txt", toWrite, "mW");
        sd.writeToFile("BaseUsage.txt", toWrite);

        toWrite = "mAh: " + avg_mAh;

        sd.replaceLine("BaseUsage.txt", toWrite, "mAh");
        sd.writeToFile("BaseUsage.txt", toWrite);

    }


    private void applicationStats(){

        StoreData sd = new StoreData(context);
        BatteryData bd = new BatteryData(context);

        String toWrite = "";

        try {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            startActivity(launchIntent);
        } catch(NullPointerException npe) {
            Toast openErr = Toast.makeText(context, "This app cannot be opened", Toast.LENGTH_SHORT);
            openErr.show();
            return;
        }

        powerTest();
        double app_mA = avg_mA;
        double app_mV = avg_mV;

        String[] tokens1;
        String[] tokens2;
        String[] tokens3;
        String[] tokens4;
        File file = new File(context.getFilesDir(), "BaseUsage.txt");

        double timeRemaining = Math.abs(bd.getmAh()) / Math.abs(app_mA);
        timeRemaining = Math.round(timeRemaining * 100.0) / 100.0;

        String line1 = bd.fileSingleSearch(file.getAbsolutePath(), "mA");
        String line2 = bd.fileSingleSearch(file.getAbsolutePath(), "mV");
        String line3 = bd.fileSingleSearch(file.getAbsolutePath(), "mW");
        String line4 = bd.fileSingleSearch(file.getAbsolutePath(), "mAh");
        if(line1 != null && line2 != null && line4 != null) {
            tokens1 = line1.split(" ");
            tokens2 = line2.split(" ");
            tokens4 = line4.split(" ");
            double base_mA = Double.parseDouble(tokens1[1]);
            double base_mV = Double.parseDouble(tokens2[1]);
            double base_remaining = Double.parseDouble(tokens4[1]);

            double mV_diff = base_mV - app_mV;

            double base_mW = (base_mA * base_mV) / 1000;
            double app_mW = (app_mA * app_mV) / 1000;

            double mW_diff = app_mW - base_mW;
            double mW_percent = (mW_diff/app_mW) * 100;

            //mV_diff = Math.round(mV_diff * 10000.0) / 10000.0;
            mW_diff = Math.round(mW_diff * 10.0) / 10.0;
            base_remaining = Math.round(base_remaining * 100.0) / 100.0;




            toWrite = packageName + " " + Double.toString(timeRemaining) + " " + Double.toString(base_remaining) + " " + Double.toString(mW_percent);

            //sd.replaceLine("AppUsage.txt", toWrite, packageName);
            //sd.writeToFile("AppUsage.txt", toWrite);
        } else {
            toWrite = packageName + Double.toString(timeRemaining) + " - -";
        }


        sd.replaceLine("AppUsage.txt", toWrite, packageName);
        sd.writeToFile("AppUsage.txt", toWrite);


    }


    private void powerTest(){
        avg_mA = 0.0;
        avg_mV = 0.0;
        StoreData sd = new StoreData(context);
        BatteryData bd = new BatteryData(context);

        bd.updateInfo();
        for(int i = 0; i < SAMPLE_SIZES; i++){
            SystemClock.sleep(SAMPLE_SECONDS/SAMPLE_SIZES * 1000);
            time += SAMPLE_SECONDS/2;
            bd.updateInfo();
            double tempmA = bd.getCurrentCurrent();
            double tempmV = bd.getBatteryVoltage();
            avg_mA += tempmA;
            avg_mV += tempmV;
        }
        avg_mA /= SAMPLE_SIZES;
        avg_mV /= SAMPLE_SIZES;
    }








    private void testBrightness(){
        avg_mA = 0.0;
        avg_mV = 0.0;
        StoreData sd = new StoreData(context);
        BatteryData bd = new BatteryData(context);





        sd.storeStr("LCD.txt", "======== NEW DATASET ========");

        while(brightness <= 255) {
            setBrightness(brightness);
            String printStr = "Brightness: " + brightness;
            sd.storeStr("LCD.txt", printStr);

            bd.updateInfo();

            double prev_mA = bd.getCurrentCurrent();
            double prev_mV = bd.getBatteryVoltage();
            double prev_mW = (prev_mA * prev_mV) / 1000;
            double diff_mA = 0;
            double diff_mV = 0;
            double diff_mW = 0;

            for(int i = 0; i < SAMPLE_SIZES; i++){
                SystemClock.sleep(SAMPLE_SECONDS/SAMPLE_SIZES * 1000);
                time += SAMPLE_SECONDS;
                bd.updateInfo();
                double temp_mA = bd.getCurrentCurrent();
                double temp_mV = bd.getBatteryVoltage();
                double temp_mW = (temp_mA * temp_mV) / 1000;
                prev_mA -= temp_mA;
                prev_mV -= temp_mV;
                prev_mW -= temp_mW;
                sd.storeStr("LCD.txt", Integer.toString(time) + "s    " + Double.toString(prev_mA) + "mA    " + Double.toString(prev_mV) + "mV    " +Double.toString(prev_mW) + "mW    " + bd.getBatteryPercent() + "%");

                diff_mA += prev_mA;
                diff_mV += prev_mV;
                diff_mW += prev_mW;


                prev_mA = temp_mA;
                prev_mV = temp_mV;
                prev_mW = temp_mW;

            }
            diff_mA /= SAMPLE_SIZES;
            diff_mV /= SAMPLE_SIZES;
            diff_mW /= SAMPLE_SIZES;
            sd.storeStr("LCD.txt", "Average: " + Integer.toString(time) + "s    " + Double.toString(diff_mA) + "mA    " + Double.toString(diff_mV) + "mV    " + Double.toString(diff_mW) + "mW    " + bd.getBatteryPercent() + "%");
            brightness += BRIGHTNESS_INTERVAL;
        }
    }


    private void testOther(String s){
        String fileName = s + ".txt";

        avg_mA = 0.0;
        avg_mV = 0.0;
        StoreData sd = new StoreData(context);
        BatteryData bd = new BatteryData(context);
        sd.storeStr(fileName, "======== NEW DATASET ========");

        setBrightness(255);

        bd.updateInfo();

        double prev_mA = bd.getCurrentCurrent();
        double prev_mV = bd.getBatteryVoltage();
        double prev_mW = (prev_mA * prev_mV) / 1000;
        double diff_mA = 0;
        double diff_mV = 0;
        double diff_mW = 0;

        for(int i = 0; i < SAMPLE_SIZES; i++){
            SystemClock.sleep(SAMPLE_SECONDS/SAMPLE_SIZES * 1000);
            time += SAMPLE_SECONDS;
            bd.updateInfo();
            double temp_mA = bd.getCurrentCurrent();
            double temp_mV = bd.getBatteryVoltage();
            double temp_mW = (temp_mA * temp_mV) / 1000;
            prev_mA -= temp_mA;
            prev_mV -= temp_mV;
            prev_mW -= temp_mW;
            sd.storeStr(fileName, Integer.toString(time) + "s    " + Double.toString(prev_mA) + "mA    " + Double.toString(prev_mV) + "mV    " +Double.toString(prev_mW) + "mW    " + bd.getBatteryPercent() + "%");

            diff_mA += prev_mA;
            diff_mV += prev_mV;
            diff_mW += prev_mW;


            prev_mA = temp_mA;
            prev_mV = temp_mV;
            prev_mW = temp_mW;

        }
        diff_mA /= SAMPLE_SIZES;
        diff_mV /= SAMPLE_SIZES;
        diff_mW /= SAMPLE_SIZES;
        sd.storeStr(fileName, "Average: " + Integer.toString(time) + "s    " + Double.toString(diff_mA) + "mA    " + Double.toString(diff_mV) + "mV    " + Double.toString(diff_mW) + "mW    " + bd.getBatteryPercent() + "%");
        brightness += BRIGHTNESS_INTERVAL;
        setBrightness(100);


    }






    private void setBrightness(int b){
        android.provider.Settings.System.putInt(context.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, b);
    }

}
