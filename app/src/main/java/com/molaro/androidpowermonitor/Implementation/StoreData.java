package com.molaro.androidpowermonitor.Implementation;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static android.content.ContentValues.TAG;


/*
*   Class
* ------------------------
*   Description:
*
*/
public class StoreData {

    Context context;
    final int MY_PERMISSIONS_REQUEST = 1;
    //===================================== CONSTRUCTOR =====================================
    public StoreData(Context context){
        this.context = context;
    }


    public void makeDir(){
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String theDir = dir.getAbsolutePath() + "/AndroidPowerMonitorData";
        new File(theDir).mkdirs();
    }


    public void storeInfo(String fn, double a, double b){

        makeDir();

        String newDir = "AndroidPowerMonitorData/" + fn;

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(dir, newDir);
        String addLine = Double.toString(a) + "\t" + Double.toString(b) + "\n";

        try(FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(addLine);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }

    }

    public void storeStr(String fn, String s){

        makeDir();

        String newDir = "AndroidPowerMonitorData/" + fn;

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(dir, newDir);

        try(FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(s + "\n");
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }

    }

    public void writeToFile(String filename, String s) {

        File file = new File(context.getFilesDir(), filename);
        try(FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(s);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    public void replaceLine(String filename, String replaceWith, String query){
        try {
            File inputFile = new File(context.getFilesDir(), filename);
            String filePath = inputFile.getAbsolutePath();
            if (!inputFile.isFile()) {
                System.out.println("Parameter is not an existing file");
                return;
            }
            //Construct the new file that will later be renamed to the original filename.
            File tempFile = new File(inputFile.getAbsolutePath() + ".tmp");
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
            String line = null;

            //Read from the original file and write to the new
            //unless content matches data to be removed.
            while ((line = br.readLine()) != null) {
                if (!line.trim().contains(query)) {
                    pw.println(line);
                    pw.flush();
                }
            }
            pw.close();
            br.close();

            //Delete the original file
            if (!inputFile.delete()) {
                System.out.println("Could not delete file");
                return;
            }

            //Rename the new file to the filename the original file had.
            if (!tempFile.renameTo(inputFile))
                System.out.println("Could not rename file");
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void setGps(){
        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }







}
