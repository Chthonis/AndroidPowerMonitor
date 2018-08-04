package com.molaro.androidpowermonitor.Implementation;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/*
*   Class
* ------------------------
*   Description:
*       Here we have functions that find and get the values showing the
*          proc, sys, and dev directories on one's phone. This is mainly
*          used as utility, many files cannot be opened and, while there is
*          error checking, files that may crash the program may have been missed.
*/
public class DirectoryData extends GatherData{

    //===================================== VARIABLES =====================================
    private ArrayList<String> currDirectories;
    private String s;

//===================================== CONSTRUCTOR =====================================

    public DirectoryData(){
        currDirectories = new ArrayList<String>();
        s = new String();
        initInfo();
    }


    /*
    *   Function initInfo
    * ------------------------
    *   Description:
    *       Gets the inital directories (proc, sys, dev). Only called from constructor.
    *
    *   @params
    *       none
    *
    *   @returns
    *       void
    *
    */
    private void initInfo() {
        ArrayList<String> relevantData = new ArrayList<String>();

        File openFile = new File("/");
        File[] listOfFiles = openFile.listFiles();

        if(openFile.isDirectory() && listOfFiles == null) {
            currDirectories = null;
            return;
        }

        if(openFile.isFile() && openFile.exists()) {
            relevantData.addAll(filePrint("/"));
            currDirectories.addAll(relevantData);
            return;
        }
        else if(openFile.isFile()){
            currDirectories = null;
            return;
        }

        for (int i = 0; i < listOfFiles.length; i++) {
            if(!listOfFiles[i].getPath().equals("/proc") && !listOfFiles[i].getPath().equals("/sys") &&!listOfFiles[i].getPath().equals("/dev") )
                continue;
            if (listOfFiles[i].isFile() && listOfFiles[i].exists()) {
                relevantData.add(listOfFiles[i].getPath());
            } else if (listOfFiles[i].isDirectory()) {
                relevantData.add(listOfFiles[i].getPath());
            }

        }

        currDirectories.addAll(relevantData);



    }

    /*
    *   Function retrieveInfo
    * ------------------------
    *   @Overrides function from GatherData
    *
    *   Description:
    *       gets next directory. Sets
    *       this data for changing directory.
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
        ArrayList<String> relevantData = new ArrayList<String>();
        currDirectories = new ArrayList<String>();

        File openFile = new File(s);
        File[] listOfFiles = null;
        if(openFile.isDirectory()) {
            listOfFiles = openFile.listFiles();
            if(listOfFiles == null) {
                currDirectories = null;
                return;
            }
        }


        if(openFile.isFile() && filePrint(s) != null) {
            relevantData.addAll(filePrint(s));
            currDirectories.addAll(relevantData);
            return;
        }
        else if(openFile.isFile()){
            currDirectories = null;
            return;
        }
        if(listOfFiles == null) {
            currDirectories = null;
            return;
        }


        for (int i = 0; i < listOfFiles.length; i++) {
            Log.d(TAG, listOfFiles[i].getAbsolutePath());
            if(listOfFiles[i].getAbsolutePath().equals("/sys/power/wakeup_count"))
                continue;
            if ((listOfFiles[i].isFile() && listOfFiles[i].exists() && filePrint(listOfFiles[i].getAbsolutePath())!=null) || (listOfFiles[i].isDirectory())) {
                relevantData.add(listOfFiles[i].getPath());
            }
        }
        currDirectories.addAll(relevantData);
    }

    //================================== UPDATES/SETTERS ==================================

    public void updateDirectories(String s){ this.s = s; retrieveInfo(); }

    //====================================== GETTERS ======================================

    public ArrayList<String> getDirectories(){ return currDirectories; }

}
