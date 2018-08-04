package com.molaro.androidpowermonitor.Implementation;

import android.content.Context;
import android.net.TrafficStats;

import java.util.ArrayList;


/*
*   Class
* ------------------------
*   Description:
*       Here we have functions that find and get the values showing the
*          network usage on one's phone.
*/

public class NetData extends GatherData{

    //===================================== VARIABLES =====================================
    private ArrayList<Long> networkTotals;
    private ArrayList<Long> networkCurrents;

    private long totRxBytes;
    private long totTxBytes;

    private Context context;


    //===================================== CONSTRUCTOR =====================================

    public NetData(){
        networkTotals = new ArrayList<Long>();
        networkCurrents = new ArrayList<Long>();
        totRxBytes = TrafficStats.getTotalRxBytes();
        totTxBytes = TrafficStats.getTotalTxBytes();
        retrieveInfo();
    }


    /*
    *   Function retrieveInfo
    * ------------------------
    *   @Overrides function from GatherData
    *
    *   Description:
    *       Gets relevant network information. Sets
    *           it for later getters.
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

        long currRxBytes = TrafficStats.getTotalRxBytes();
        long currTxBytes = TrafficStats.getTotalTxBytes();

        long tempRx = currRxBytes;
        long tempTx = currTxBytes;

        currRxBytes -= totRxBytes;
        currTxBytes -= totTxBytes;

        totRxBytes = tempRx;
        totTxBytes = tempTx;

        networkTotals.clear();
        networkCurrents.clear();

        networkTotals.add(totRxBytes);
        networkTotals.add(totTxBytes);

        networkCurrents.add(currRxBytes);
        networkCurrents.add(currTxBytes);

    }







    //====================================== GETTERS ======================================

    public ArrayList<Long> getNetworkTotals(){
        return networkTotals;
    }

    public ArrayList<Long> getNetworkCurrents(){
        return networkCurrents;
    }


}
