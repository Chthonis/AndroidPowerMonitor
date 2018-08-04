package com.molaro.androidpowermonitor.UI;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.molaro.androidpowermonitor.Implementation.CpuData;

import android.os.Bundle;

import java.util.ArrayList;


/*
*   Class
* ------------------------
*   Description
*
*/
public class CpuInfo extends GraphList{

    private int keyNum;

    private CpuData lastCpuObj = new CpuData();
    private CpuData nextCpuObj = new CpuData();

    private double currMax = 1.2;


    /*===================================== CONSTRUCTOR =====================================
    *   Function CpuInfo newInstance()
    * ------------------------
    *   Creates a new fragment of CpuInfo
    */
    public static CpuInfo newInstance(int key){
        CpuInfo fragment = new CpuInfo();
        Bundle args = new Bundle();
        args.putInt("key", key); // 0 for CPU % graph, 1 for frequency graph
        fragment.setArguments(args);
        return fragment;
    }


    /*
    *   Function pageActivity
    * ------------------------
    *   Description:
    *
    *
    *   @params
    *       none
    *
    *   @returns
    *       void;
    *
    */
    @Override
    public void pageActivity() {
        allDetails.clear();
        keyNum = getArguments().getInt("key");
        int cores = lastCpuObj.getNumCores();

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX){
                if(isValueX)
                    return super.formatLabel(value, isValueX);
                if(keyNum == 0)
                    return super.formatLabel(value, isValueX) + "%";
                else if(keyNum == 1)
                    return super.formatLabel(value, isValueX) + "GHz";
                return super.formatLabel(value, isValueX);
            }
        });

        initializeGraph(cores+2);

        allDetails.addAll(lastCpuObj.getCpuStrInfo());
    }


    /*
    *   Function runActivity
    * ------------------------
    *   Description:
    *
    *
    *   @params
    *       none
    *
    *   @returns
    *       void
    *
    */
    @Override
    public void runActivity() {

        double total = 0.0;
        double percentUsage = 0.0;
        double percentIdle = 0.0;
        double deltaUsage = 0.0;
        double cpuFreq = 0.0;
        double cpuMaxFreq = 0.0;
        double cpuMinFreq = 0.0;

        Viewport viewport = graph.getViewport();

        StringBuffer sb = new StringBuffer();
        ArrayList<String> tempCpuStrInfo = new ArrayList<String>();
        ArrayList<Integer> tempCpuFreqInfo = new ArrayList<Integer>();

        allDetails.clear();
        nextCpuObj.updateInfo();

        int cores = nextCpuObj.getNumCores();
        String core_str = "Number of Cores: " + Integer.toString(cores);
        tempCpuStrInfo.add(core_str);

        lastX++;
        for(int i = 0; i <= cores; i++){
            cpuFreq = 0.0;

            if(i == 0){
                sb.append("Average Cpu Core Values: \n");
            }else{
                sb.append("CPU " + i + ":\n");
            }

            deltaUsage = nextCpuObj.calcDelta(lastCpuObj, nextCpuObj, i);
            percentIdle = 100.0 - deltaUsage;

            percentIdle = Math.round(percentIdle * 10000.0) / 10000.0;
            deltaUsage = Math.round(deltaUsage * 10000.0) / 10000.0;
            percentUsage = Math.round(percentUsage * 10000.0) / 10000.0;

            cpuFreq = (double) nextCpuObj.getCpuFreq().get(i);
            cpuFreq/=1000000;
            cpuFreq = Math.round(cpuFreq * 10000.0) / 10000.0;


            if(keyNum == 0) {
                viewport.setMaxY(100.0);


                allSeries.get(i).appendData(new DataPoint(lastX, deltaUsage), true, 200);
            }
            else if(keyNum == 1) {
                if(cpuFreq > currMax)
                    currMax = cpuFreq;
                viewport.setMaxY(currMax);
                allSeries.get(i).appendData(new DataPoint(lastX, cpuFreq), true, 200);
            }

            sb.append("Usage: " + deltaUsage + "%\t");
            sb.append("Idle: " + percentIdle + "%\n");
            sb.append("Frequency: " + cpuFreq + "GHz");

            allDetails.add(sb.toString());
            sb.setLength(0);
        }
        lastCpuObj.setCpuStrInfo(tempCpuStrInfo);

        lastCpuObj.updateInfo();
    }

}