package com.molaro.androidpowermonitor.UI;

import android.app.Fragment;
import android.app.FragmentManager;


import android.support.v13.app.FragmentPagerAdapter;


/*
*   Class
* ------------------------
*   Description
*
*/
public class MyPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 9;

    //===================================== CONSTRUCTOR =====================================
    public MyPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }


    @Override
    public int getCount() {
        return NUM_ITEMS;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ApplInfo.newInstance(1);
            case 1:
                return ApplInfo.newInstance(0);
            case 2:
                return BatteryInfo.newInstance();
            case 3:
                return CpuInfo.newInstance(0);
            case 4:
                return CpuInfo.newInstance(1);
            case 5:
                return NetInfo.newInstance();
            case 6:
                return MemInfo.newInstance();
            case 7:
                return GeneralInfo.newInstance();
            case 8:
                return DirectoryInfo.newInstance();
            default:
                return null;
        }
    }


    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence ret = "Tab " + position;
        switch(position){
            case 0:
                ret = "All Applications";
                break;
            case 1:
                ret = "Running Applications";
                break;
            case 2:
                ret = "Power Usage";
                break;
            case 3:
                ret = "CPU Usage";
                break;
            case 4:
                ret = "CPU Frequency";
                break;
            case 5:
                ret = "Network";
                break;
            case 6:
                ret = "RAM Usage";
                break;
            case 7:
                ret = "Other Info";
                break;
            case 8:
                ret = "Proc & Sys Directories";
                break;
            default:
                ret = "Other";
                break;
        }
        return ret;
    }

}
