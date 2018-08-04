package com.molaro.androidpowermonitor.Implementation;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;

/*
*   Class BatteryData
* ------------------------
*   Description:
*       Here we have functions that find and get the values showing the battery usage
*           on one's phone.
*/
public class BatteryData extends GatherData{

    //===================================== VARIABLES =====================================
    double batteryCapacity;
    double batteryCurrent;
    double batteryPercent;
    double batteryVoltage;
    double mAh;
    double currentCurrent;
    double avgCurrent;

    Context context;

    //===================================== CONSTRUCTOR =====================================
    public BatteryData(Context context){
        batteryCapacity = 0;
        batteryCurrent = 0;
        batteryPercent = 0;
        batteryVoltage = 0;
        mAh = 0;
        double currentCurrent = 0;
        double avgCurrent = 0;
        this.context = context;
        retrieveInfo();
    }



    /*
    *   Function retrieveInfo
    * ------------------------
    *   @Overrides function from GatherData
    *
    *   Description:
    *       Finds data relating to the battery usage/capacity. Sets
    *       this data for getters later on.
    *
    *   @returns
    *       void
    *
    */
    @Override
    protected void retrieveInfo() {

        batteryCurrent = 0;
        batteryCapacity = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager mBatteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, iFilter);

            Integer chargeCounter = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            Integer capacity = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            Integer voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            Long averageCurrent = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
            Long currentNow = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);

            currentCurrent = currentNow;
            avgCurrent = averageCurrent;
            batteryCurrent = chargeCounter;
            batteryPercent = capacity;
            batteryVoltage = voltage;

            if (chargeCounter == Integer.MIN_VALUE || capacity == Integer.MIN_VALUE)
                chargeCounter = 0;

            batteryCapacity = (chargeCounter / capacity) / 10;
        }

        if(batteryCapacity <= 0){
            secondPowerMethod();
        }
        calcmAh();
    }

    /*
    *   Function secondPowerMethod
    * ------------------------
    *   Description:
    *
    *
    *   @returns
    *       void
    *
    */
    private void secondPowerMethod(){
        Object mPowerProfile;
        double cap = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(context);

            cap = (double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile);

        } catch (Exception e) {
            e.printStackTrace();
        }

        batteryCapacity = cap;
    }

    /*
    *   Function calcmAh
    * ------------------------
    *   Description:
    *
    *
    *   @returns
    *       void
    *
    */
    private void calcmAh(){
        mAh = (batteryCapacity * batteryPercent * 0.01);
        mAh = Math.round(mAh * 1000.0) / 1000.0;
        mAh = Math.round(mAh * 1000.0) / 1000.0;


        batteryPercent = mAh/(double)batteryCapacity;
        batteryPercent *= 100;
        batteryPercent = Math.round(batteryPercent * 1000.0) / 1000.0;

        batteryCapacity = Math.round(batteryCapacity * 1000.0) / 1000.0;
        batteryVoltage = Math.round(batteryVoltage * 1000.0) / 1000.0;

    }


    //====================================== GETTERS ======================================

    public double getBatteryCapacity(){
        return batteryCapacity;
    }

    public double getBatteryPercent(){
        return batteryPercent;
    }

    public double getBatteryVoltage(){
        return batteryVoltage;
    }

    public double getmAh(){ return mAh; }

    public double getCurrentCurrent(){ return Math.abs(currentCurrent); }

    public double getAvgCurrent(){ return Math.abs(avgCurrent); }



}
