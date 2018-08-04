package com.molaro.androidpowermonitor.UI;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.molaro.androidpowermonitor.Implementation.MemData;
import com.molaro.androidpowermonitor.R;

import java.util.ArrayList;


/*
*   Class
* ------------------------
*   Description
*
*/
public class MemInfo extends GraphList {

    private final int GRAPHABLE_LIST_SIZE = 1;
    private MemData memObj = new MemData();
    private LineGraphSeries<DataPoint> series;

    /*===================================== CONSTRUCTOR =====================================
    *   Function MemInfo newInstance()
    * ------------------------
    *   Creates a new fragment of MemInfo
    */
    public static MemInfo newInstance(){
        MemInfo fragment = new MemInfo();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        series = new LineGraphSeries<DataPoint>();
        View view = inflater.inflate(R.layout.activity_graph_list, container, false);
        graph = (GraphView) view.findViewById(R.id.graph);
        graph.addSeries(series);
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0.0);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0.0);
        viewport.setMaxX(200);
        viewport.setScrollable(true);

        pageActivity();

        listView = (ListView)view.findViewById(R.id.info_list);

        listViewAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, allDetails);

        listView.setAdapter(listViewAdapter);

        return view;
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
    public void pageActivity() {
        allDetails.clear();

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX){
                if(isValueX) {
                    return super.formatLabel(value, isValueX);
                }
                return super.formatLabel(value, isValueX) + "%";
            }
        });

        memObj.updateInfo();
        allDetails.addAll(memObj.getMemStrInfo());
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
        memObj.updateInfo();
        allDetails.clear();
        allDetails.addAll(memObj.getMemStrInfo());

        double percentRAM = memObj.getPercentRam();

        Viewport viewport = graph.getViewport();
        viewport.setMaxY(100);
        series.appendData(new DataPoint(lastX++, percentRAM), true, 200);


    }

}
