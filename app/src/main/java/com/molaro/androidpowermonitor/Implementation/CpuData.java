package com.molaro.androidpowermonitor.Implementation;

import java.io.BufferedReader;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/*
*   Class
* ------------------------
*   Description:
*       Here we have functions that find and get the values showing the
*           usage of all CPUs on one's phone as well as number of CPUs
*/
public class CpuData extends GatherData{

    //===================================== VARIABLES =====================================
    private ArrayList<String> cpuStrInfo;
    private ArrayList<int[]> cpuNumInfo;
    private ArrayList<Integer> frequencies;

    private double cpuUsage;
    private int numCores;

    //===================================== CONSTRUCTOR =====================================
    public CpuData(){
        numCores = 1;
        cpuStrInfo = new ArrayList<String>();
        cpuNumInfo = new ArrayList<int[]>();
        frequencies = new ArrayList<Integer>();

        retrieveInfo();
    }



    /*
    *   Function retrieveInfo
    * ------------------------
    *   @Overrides function from GatherData
    *
    *   Description:
    *       Finds data relating to the CPU usage. Sets
    *       this data for getters later on.
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


        int tempTime = 0;
        int totalTime = 0;
        int idleTime = 0;
        int usageTime = 0;
        int freq = 0;
        int avgFreq = 0;

        boolean firstValue = true;

        cpuNumInfo.clear();
        frequencies.clear();
        StringBuffer sb = new StringBuffer();
        BufferedReader br;
        String line;
        String[] tokens;

        findNumCpu();

        String firstPathHalf = "/sys/devices/system/cpu/cpu";
        String secondPathHalf = "/cpufreq/scaling_cur_freq";

        ArrayList<String> relevantData = fileSearch("/proc/stat", "cpu");

        for(int i = 0; i < relevantData.size(); i++) {
            String fullPath = "";
            if(i != 0) {
                fullPath += firstPathHalf + Integer.toString(i - 1) + secondPathHalf;
                freq = readSingleInt(fullPath);
                frequencies.add(freq);
                avgFreq+=freq;
            }

            line = relevantData.get(i);
            tokens = line.split("  | ");

            totalTime = 0;
            idleTime = 0;

            for (int j = 1; j < tokens.length - 2; j++) {

                tempTime = Integer.parseInt(tokens[j]);
                totalTime += tempTime;
                if (j == 4 || j == 5)
                    idleTime += tempTime;


            }

            usageTime = totalTime - idleTime;



            if(i == 0){
                totalTime /= numCores;
                usageTime /= numCores;
                //idleTime /= numCores;
                firstValue = false;
            }

            int[] newInfo = {totalTime, usageTime};
            cpuNumInfo.add(newInfo);

        }
        avgFreq/=numCores;
        frequencies.add(0, avgFreq);
    }

    /*
    *   Function findNumCpu
    * ------------------------
    *   Description:
    *       Searches file /proc/cpuinfo to get the number
    *           of processors in the phone. Sets this for getter.
    *
    *   @params
    *       none
    *
    *   @returns
    *       void
    *
    */
    private void findNumCpu(){
        numCores = fileSearch("/proc/cpuinfo", "processor").size();
        if(numCores <= 0)
            return;
    }


    /*
    *   Function calcDelta
    * ------------------------
    *   Description:
    *       Calculates the cpu usage difference for whatever time interval passed between
    *           the last measured cpu state and the one that was just measured.
    *           The last and most recent specified by input parameters.
    *
    *   @params
    *       CpuData lastCpuObj - Object from the last time CPU data measured
    *       CpuData nextCpuObj - Object from the more recent time CPU data measured
    *       int n - the Processor for which measure is taken. value of 0 represents averate,
    *           not an individual processor.
    *
    *   @returns
    *       double - the difference representing change in cpu data
    *
    */
    public double calcDelta(CpuData lastCpuObj, CpuData nextCpuObj, int n){

        int totLast = 0;
        int usageLast = 0;

        int totNext = 0;
        int usageNext = 0;

        double percentUsage = 0.0;

        ArrayList<int[]> LastCpus = lastCpuObj.getCpuNumInfo();

        nextCpuObj.updateInfo();

        ArrayList<int[]> NextCpus = nextCpuObj.getCpuNumInfo();

        int[] tempLast = LastCpus.get(n);
        int[] tempNext = NextCpus.get(n);

        totLast = tempLast[0];
        usageLast = tempLast[1];

        totNext = tempNext[0];
        usageNext = tempNext[1];


        totNext -= totLast;
        usageNext -= usageLast;

        percentUsage = ((double) usageNext) / ((double) totNext);
        percentUsage *= 100;

        percentUsage = Math.round(percentUsage * 10000.0) / 10000.0;

        return percentUsage;

    }


//================================== UPDATES/SETTERS ==================================

    public void setCpuStrInfo(ArrayList<String> currVals){
        cpuStrInfo = currVals;
    }

//================================== GETTERS ==================================

    public ArrayList<String> getCpuStrInfo(){
        return cpuStrInfo;
    }

    public ArrayList<int[]> getCpuNumInfo(){
        return cpuNumInfo;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public int getNumCores(){
        return numCores;
    }

    public ArrayList<Integer> getCpuFreq() { return frequencies;}



}
