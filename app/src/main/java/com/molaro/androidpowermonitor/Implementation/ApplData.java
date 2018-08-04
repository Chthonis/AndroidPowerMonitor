package com.molaro.androidpowermonitor.Implementation;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.ACTIVITY_SERVICE;

/*
*   Class ApplData
* ------------------------
*   Description:
*       Here we have functions that find and get the values relating to applications
*           on one's phone (e.g. app name, package name, icon, etc).
*/
public class ApplData extends GatherData{

    //===================================== VARIABLES =====================================
    private ArrayList<String> nameList;
    private ArrayList<String> packageList;
    private ArrayList<ArrayList<String>> pidList;
    private ArrayList<ArrayList<String>> emptyVoltList;
    private ArrayList<ArrayList<String>> emptyAmpList;
    private ArrayList<ArrayList<String>> emptyTimeList;


    private ArrayList<Drawable> iconList;

    private boolean oldBuild = true;

    private Context context;

    //===================================== CONSTRUCTOR =====================================
    public ApplData(Context context){
        this.context = context;
        nameList = new ArrayList<String>();
        packageList = new ArrayList<String>();
        pidList = new ArrayList<ArrayList<String>>();
        emptyVoltList = new ArrayList<ArrayList<String>>();
        emptyAmpList = new ArrayList<ArrayList<String>>();
        emptyTimeList = new ArrayList<ArrayList<String>>();
        iconList = new ArrayList<Drawable>();
    }


    /*
    *   Function retrieveInfoOld
    * ------------------------
    *   Description:
    *       Retrieves info about all running applications on phones of LOLLIPOP and below
    *
    *   @returns
    *       void
    *
    */
    private void retrieveInfoOld(){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> pidsTask = activityManager.getRunningAppProcesses();

        for(int i = 0; i < pidsTask.size(); i++) {
            String somePackage = pidsTask.get(i).processName;
            setPackageManagerInfo(somePackage);
        }
    }

    /*
    *   Function retrieveInfoNougat
    * ------------------------
    *   Description:
    *       Retrieves info about all running applications on phones above LOLLIPOP
    *           it can be used for LOLLIPOP but is not necessary
    *
    *   @returns
    *       void
    *
    */
    private void retrieveInfoNougat(){
        UsageStatsManager usm = (UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 10000*10000, time);
        SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();

        for (UsageStats usageStats : appList) {
            String somePackage = usageStats.getPackageName();
            String fgt = "Foreground Time: " + Long.toString(usageStats.getTotalTimeInForeground());
            setPackageManagerInfo(somePackage);
        }
        if (mySortedMap != null && !mySortedMap.isEmpty()) {
            String currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();

        }
    }



    /*
    *   Function retrieveInfo
    * ------------------------
    *   Description:
    *       Gets info of ALL non-system apps installed
    *
    *   @returns
    *       void
    *
    */
    @Override
    protected void retrieveInfo() {
        List<PackageInfo> packList = context.getPackageManager().getInstalledPackages(0);
        for (int i=0; i < packList.size(); i++)
        {
            PackageInfo packInfo = packList.get(i);
            if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
            {
                String packageName = packInfo.applicationInfo.packageName;
                setPackageManagerInfo(packageName);
            }
        }
    }







    /*
    *   Function setPackageManagerInfo
    * ------------------------
    *   Description:
    *       Uses Package Manager to get more info about app.
    *       Separated because, provided a package name, version does not matter.
    *
    *   @params
    *       String packageName - the name of the package in question.
    *
    *   @returns
    *       void
    *
    */
    private void setPackageManagerInfo(String packageName){
        //Get Package Name
        ArrayList<String> tempPidList = new ArrayList<String>();
        ArrayList<String> tempList = new ArrayList<String>();
        ;
        int uid = 0;
        Drawable icon;

        try {
            //Get Application Name
            ApplicationInfo app = context.getPackageManager().getApplicationInfo(packageName, 0);
            String appName = context.getPackageManager().getApplicationLabel(app).toString();
            nameList.add(appName);
            //Get Application Icon
            icon = context.getPackageManager().getApplicationIcon(packageName);
            iconList.add(icon);
            //Get UID
            uid = context.getPackageManager().getApplicationInfo(packageName, 0).uid;
            String tempStr = "PID: " + Integer.toString(uid);
            tempPidList.add(tempStr);
            pidList.add(tempPidList);
            //creates "blank" slates for later data insertion
            tempList.add(" - ");
            emptyVoltList.add(tempList);
            emptyAmpList.add(tempList);
            emptyTimeList.add(tempList);
            //Add package name
            packageList.add(packageName);

        }
        catch(PackageManager.NameNotFoundException e){
            Toast t = Toast.makeText(context, "An app couldn't be loaded", Toast.LENGTH_SHORT);
            t.show();
        }
    }



    //======================================= CHECKS =======================================
    /*
    *   Function areAppsFoundUsm
    * ------------------------
    *   Description:
    *       Checks if apps can be found on phones above LOLLIPOP
    *
    *   @params
    *       none
    *
    *   @returns
    *       boolean
    *
    */
    public boolean areAppsFoundUsm(){
        UsageStatsManager usm = (UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 10000*10000, time);
        if (appList != null && appList.size() == 0) {
            return false;
        }
        else if (appList != null && appList.size() > 0) {
            return true;
        }
        return false;
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return (pkgInfo.applicationInfo.flags &
                ApplicationInfo.FLAG_SYSTEM) != 0;
    }


    //================================== UPDATES/SETTERS ==================================



    public void updateRunningAppl() {
        packageList.clear();
        pidList.clear();
        nameList.clear();
        emptyVoltList.clear();
        emptyAmpList.clear();
        emptyTimeList.clear();        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
            retrieveInfoNougat();
        } else{
            retrieveInfoOld();
        }
    }

    public void updateInstalledAppl() {
        packageList.clear();
        pidList.clear();
        nameList.clear();
        emptyVoltList.clear();
        emptyAmpList.clear();
        emptyTimeList.clear();
        retrieveInfo();
    }

    //====================================== GETTERS ======================================

    public ArrayList<String> getApplPackages() {
        return packageList;
    }

    public ArrayList<ArrayList<String>> getApplPid() {
        return pidList;
    }

    public ArrayList<String> getApplNames() {
        return nameList;
    }

    public ArrayList<Drawable> getIconList() {
        return iconList;
    }

    public ArrayList<ArrayList<String>> getEmptyVoltList() { return emptyVoltList; }

    public ArrayList<ArrayList<String>> getEmptyAmpList() { return emptyAmpList; }

    public ArrayList<ArrayList<String>> getEmptyTimeList() { return emptyTimeList; }

}