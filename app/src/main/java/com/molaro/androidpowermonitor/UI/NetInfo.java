package com.molaro.androidpowermonitor.UI;

import android.os.Bundle;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.molaro.androidpowermonitor.Implementation.NetData;

import java.util.ArrayList;


/*
*   Class
* ------------------------
*   Description
*
*/
public class NetInfo extends GraphList {

    private final int GRAPHABLE_LIST_SIZE = 3;

    private NetData netObj = new NetData();
    private double maxSoFar = 15.0;

    /*===================================== CONSTRUCTOR =====================================
    *   Function NetInfo newInstance()
    * ------------------------
    *   Creates a new fragment of NetInfo
    */
    public static NetInfo newInstance(){
        NetInfo fragment = new NetInfo();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /*
    *   Function
    * ------------------------
    *   Description:
    *
    *
    *   @params
    *
    *
    *   @returns
    *
    *
    */
    @Override
    public void pageActivity(){
        allDetails.clear();

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX){
                if(isValueX) {
                    return super.formatLabel(value, isValueX);
                }
                return super.formatLabel(value, isValueX) + "kB";
            }
        });

        initializeGraph(GRAPHABLE_LIST_SIZE);
        allDetails.addAll(getNetworkStrings());
    }


    /*
    *   Function
    * ------------------------
    *   Description:
    *
    *
    *   @params
    *
    *
    *   @returns
    *
    *
    */
    @Override
    public void runActivity() {
        ArrayList<Long> tempTotals = new ArrayList<Long>();
        ArrayList<Long> tempCurrents = new ArrayList<Long>();

        double graphVals = 25.0;

        allDetails.clear();
        netObj.updateInfo();
        allDetails.addAll(getNetworkStrings());


        tempTotals.addAll(netObj.getNetworkTotals());
        tempCurrents.addAll(netObj.getNetworkCurrents());

        lastX++;
        for(int i = 0; i < 2; i++){
            LineGraphSeries<DataPoint> tempSeries = allSeries.get(i);

            graphVals = bytesToKilobytes(tempCurrents.get(i));

            if(graphVals > maxSoFar)
                maxSoFar = graphVals;

            Viewport viewport = graph.getViewport();
            viewport.setMaxY(maxSoFar);

            tempSeries.appendData(new DataPoint(lastX, graphVals), true, 200);

        }

    }

    /*
    *   Function
    * ------------------------
    *   Description:
    *
    *
    *   @params
    *
    *
    *   @returns
    *
    *
    */
    private ArrayList<String> getNetworkStrings(){
        ArrayList<String> ret = new ArrayList<String>();

        ArrayList<Long> tempTotals = new ArrayList<Long>();
        ArrayList<Long> tempCurrents = new ArrayList<Long>();

        tempTotals.addAll(netObj.getNetworkTotals());
        tempCurrents.addAll(netObj.getNetworkCurrents());

        for(int i = 0; i < tempTotals.size(); i++){
            double total;
            double current;

            String direction = getDataDirection(i);

            total = bytesToKilobytes(tempTotals.get(i));
            //current = bytesToKilobytes(tempCurrents.get(i));
            current = (double)tempCurrents.get(i);

            ret.add(direction + " Total: " + Double.toString(total) + " KB\n"
                    + direction + " Current: " + Double.toString(current) + "B/s");

        }
        return ret;
    }

}

