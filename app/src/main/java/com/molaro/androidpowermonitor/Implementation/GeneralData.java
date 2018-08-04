package com.molaro.androidpowermonitor.Implementation;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/*
*   Class
* ------------------------
*   Description:
*
*/
public class GeneralData extends GatherData{

    Context context;
    PhoneValues pv;

    //===================================== CONSTRUCTOR =====================================
    public GeneralData(Context context) {
        this.context = context;
        pv = new PhoneValues();
        retrieveInfo();
    }


    /*
    *   Function retrieveInfo
    * ------------------------
    *   @Overrides function from GatherData
    *
    *   Description:
    *       Gets general/random data and sets for
    *           later getters. Sets in a "PhoneValues"
    *           instance.
    *
    *   @params
    *       none
    *
    *   @returns
    *       void
    *
    */
    @Override
    protected void retrieveInfo() {

        pv.gpuInfo = gpuInfo();
        pv.internetConnection = retrieveInternetConnType();
        pv.screenBrightness = screenLevel();
        pv.gpsOn = isGpsOn();

    }

    private int screenLevel(){
        int ret = 0;
        try {
            ret=android.provider.Settings.System.getInt(
                    context.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
        return ret;
    }

    /*
    *   Function retrieveInternetConnType
    * ------------------------
    *   Description:
    *       Gets and return the type of connection to the internet
    *           currently being used.
    *
    *   @params
    *       Context context - context of caller.
    *
    *   @returns
    *       String - type of connection being used.
    *
    */
    public String retrieveInternetConnType(){

        String ret = new String();

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            switch (activeNetwork.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    ret = "wifi";
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    ret = "mobile data";
                    break;
                default:
                    break;
            }
        } else {
            ret = "Not Connected To Internet";
        }

        return ret;
    }

    private boolean isGpsOn(){
        final LocationManager manager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
        if (manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            return true;
        return false;
    }

    private String gpuInfo(){
        String s;
        final ActivityManager activityManager =  (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager
                .getDeviceConfigurationInfo();
        s = "GL version: " + configurationInfo.getGlEsVersion() + "\n";

        return s;
    }

    public String getGpuInfo(){
        return pv.gpuInfo;
    }

    public String getInternetConn(){
        return pv.internetConnection;
    }

    public int getScreenBrightness(){
        return pv.screenBrightness;
    }

    public boolean getGpsOn(){
        return pv.gpsOn;
    }













    private class PhoneValues{
        String gpuInfo;
        String internetConnection;
        int screenBrightness;
        boolean gpsOn;
        boolean audioOn;
    }
}
