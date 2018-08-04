package com.molaro.androidpowermonitor.Implementation;


import java.util.ArrayList;

/*
*   Class
* ------------------------
*   Description:
*       Here we have functions that find and get the values showing the
*           memory/storate on one's phone.
*/
public class MemData extends GatherData {

    //===================================== VARIABLES =====================================
    private ArrayList<String> memStrInfo;
    private ArrayList<Long> memNumInfo;
    long memTotal;
    long memAvail;
    long memUsed;
    long percRam;

    //===================================== CONSTRUCTOR =====================================
    public MemData() {
        memStrInfo = new ArrayList<String>();
        memNumInfo = new ArrayList<Long>();
        retrieveInfo();
    }




    /*
    *   Function retrieveInfo
    * ------------------------
    *   @Overrides function from GatherData
    *
    *   Description:
    *       Gets memory data from files. Sets it
    *           for later getters.
    *
    *   @params
    *       none
    *
    *   @returns
    *       void
    *
    */
    @Override
    protected void retrieveInfo(){

        String[] tokens;
        String line;

        long memFree;
        memAvail = 0;
        memUsed = 0;
        memTotal = 0;

        memStrInfo.clear();

        line = fileSingleSearch("/proc/meminfo", "MemTotal");
        tokens = line.split(" +");
        memTotal = (long) Integer.parseInt(tokens[1]);

        line = fileSingleSearch("/proc/meminfo", "MemFree");
        tokens = line.split(" +");
        memFree = (long) Integer.parseInt(tokens[1]);

        line = fileSingleSearch("/proc/meminfo", "MemAvail");
        if(line!=null) {
            tokens = line.split(" +");
            memAvail = (long) Integer.parseInt(tokens[1]);
        }
        else{
            line = fileSingleSearch("/proc/meminfo", "Cached");
            tokens = line.split(" +");
            memAvail = (long) Integer.parseInt(tokens[1]);
        }
        //3359MB 1338MB
        memAvail += memFree;
        memUsed = memTotal - memAvail;

        percRam = (memUsed*100)/(memTotal);
        //percRam = Math.round(percRam * 10.0) / 10.0;

        memStrInfo.add("% RAM Usage:     " + Long.toString(percRam) + "%");
        memStrInfo.add("Total RAM:     " + Long.toString(memTotal) + " kB");
        memStrInfo.add("Unused RAM: " + Long.toString(memAvail) + " kB");
        memStrInfo.add("Used RAM:      " + Long.toString(memUsed) + " kB");

        memNumInfo.add(memTotal);
        memNumInfo.add(memAvail);
        memNumInfo.add(memUsed);


    }

    //====================================== GETTERS ======================================

    public ArrayList<String> getMemStrInfo() {
        return memStrInfo;
    }

    public ArrayList<Long> getMemNumInfo() {
        return memNumInfo;
    }

    public long getPercentRam(){return percRam;}
}