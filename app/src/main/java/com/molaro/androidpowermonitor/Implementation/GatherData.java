package com.molaro.androidpowermonitor.Implementation;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/*
*   Class GraphList
* ------------------------
*   Description:
*       Abstract class that contains functions that are useful
*           for getting, reading, and checking data.
*
*/
abstract class GatherData {

    /*
    *   Function retrieveInfo()
    * ------------------------
    *   Description:
    *       Abstract function to be implemented by all child functions.
    *       Generally used to find relevant data.
    *
    *   @params
    *       none
    *
    *   @returns
    *       void
    *
    */
    protected abstract void retrieveInfo();

    /*
    *   Function fileSearch(String filename, String query)
    * ------------------------
    *   Description:
    *       Searches a file for any lines containing a specific string.
    *
    *   @params
    *       String filename - the filename for the file to be searched
    *       String query - the desired string to be searched for in the file
    *
    *   @returns
    *       ArrayList<String> - An ArrayList of strings, which are lines
    *           that contain the query inputted.
    *
    */
    protected ArrayList<String> fileSearch(String filename, String query){
        ArrayList<String> relevantData = new ArrayList<String>();
        BufferedReader br;
        String line;
        try {
            br = new BufferedReader(new FileReader(new File(filename)));
            while((line = br.readLine()) != null)
                if(line.contains(query))
                    relevantData.add(line);
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
            return null;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return relevantData;
    }

    /*
    *   Function fileSingleSearch(String filename, String query)
    * ------------------------
    *   Description:
    *       Searches a file for the first line that contains a specific string.
    *
    *   @params
    *       String filename - the filename for the file to be searched
    *       String query - the desired string to be searched for in the file
    *
    *   @returns
    *       String - A string, the first string found to contain the query
    *
    */
    public String fileSingleSearch(String filename, String query){
        BufferedReader br;
        String line;
        try {
            br = new BufferedReader(new FileReader(new File(filename)));
            while((line = br.readLine()) != null)
                if(line.contains(query))
                    return line;
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
            return null;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    /*
    *   Function fileLineNumSearch(String filename, int start, int end)
    * ------------------------
    *   Description:
    *       Searches a file for a specific set of lines.
    *
    *   @params
    *       String filename - the filename for the file to be searched
    *       int start - the starting line to begin finding lines in a file
    *       int end - the ending line to begin finding lines in a file
    *
    *   @returns
    *       ArrayList<String> - An ArrayList of strings, which are the specified
    *           lines in the file.
    *
    */
    protected int readSingleInt(String filename){
        BufferedReader br;
        String line;
        int i = 1;
        try {
            br = new BufferedReader(new FileReader(new File(filename)));
            line = br.readLine();
            if(isInteger(line, 10)) {
                int ret = Integer.parseInt(line);
                return ret;
            }
            return -1;
        } catch (FileNotFoundException e1) {
            //e1.printStackTrace();
            return -1;
        } catch (IOException e1) {
            //e1.printStackTrace();
            return -1;
        }
    }


    /*
    *    filePrint(String filename)
    * ------------------------
    *   Description:
    *       Prints a File into text format.
    *
    *   @params
    *       String filename - the filename for the file to be printed.
    *
    *   @returns
    *       ArrayList<String> - An ArrayList of strings, which are
    *           every line in the file separated line by line.
    *
    */
    protected ArrayList<String> filePrint(String filename){
        ArrayList<String> relevantData = new ArrayList<String>();
        BufferedReader br;
        String line;
        try {
            br = new BufferedReader(new FileReader(new File(filename)));
            while((line = br.readLine()) != null)
                relevantData.add(line);
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
            return null;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return relevantData;
    }

    //================================== UPDATES/SETTERS ==================================

    public void updateInfo(){ retrieveInfo(); }

    //======================================= CHECKS =======================================

    protected boolean isInteger(String s, int radix){
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++){
            if(i == 0 && s.charAt(i) == '-'){
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }



}