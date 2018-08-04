package com.molaro.androidpowermonitor.UI;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.molaro.androidpowermonitor.R;

import java.util.ArrayList;

/*
*   Class GraphList
* ------------------------
*   Extends Fragment
*
*   abstract class used to create a consistent usage
*       of a specific layout that is used many times
*
*   formatted as a graph with a clickable ListView under it
*       that shows/hides related graphical data.
*
*/
abstract class GraphList extends Fragment{

    private final Handler handler = new Handler();
    private Runnable timer;

    protected ArrayAdapter<String> listViewAdapter;
    protected ListView listView;
    protected GraphView graph;
    protected ArrayList<String> allDetails;
    protected ArrayList<LineGraphSeries<DataPoint>> allSeries;
    protected ArrayList<Boolean> graphOn;
    protected int lastX = 0;
    protected Context context;
    //public LineGraphSeries<DataPoint> series;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allDetails = new ArrayList<String>();
        allSeries = new ArrayList<LineGraphSeries<DataPoint>>();
        graphOn = new ArrayList<Boolean>();
        context = getActivity().getApplicationContext();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.activity_graph_list, container, false);
        graph = (GraphView) view.findViewById(R.id.graph);

        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0.0);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0.0);
        viewport.setMaxX(200);
        viewport.setScrollable(true);

        pageActivity();

        listView = (ListView)view.findViewById(R.id.info_list);

        listViewAdapter = new ArrayAdapter<String> (getActivity(), android.R.layout.simple_list_item_1, allDetails);

        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int hexColor = getColor(position);
                int newPosition = position - listView.getFirstVisiblePosition();

                if(graphOn.get(position)) {
                    graph.removeSeries(allSeries.get(position));
                    graphOn.set(position, false);
                    listView.getChildAt(newPosition).setBackgroundColor(Color.TRANSPARENT);
                } else {
                    graph.addSeries(allSeries.get(position));
                    graphOn.set(position, true);
                    listView.getChildAt(newPosition).setBackgroundColor(hexColor);
                }
            }
        });



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Runnable() {
            @Override
            public void run() {
                runActivity();
                listViewAdapter.notifyDataSetChanged();
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(timer, 1000);

    }

    @Override
    public void onPause() {
        handler.removeCallbacks(timer);
        super.onPause();
    }


    public void initializeGraph(int size){
        for(int i = 0; i <= size; i++){
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
            int hexColor = getColor(i);
            series.setColor(hexColor);
            allSeries.add(series);
            graphOn.add(false);
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
    public int getColor(int n){
        int hexColor;
        switch (n) {
            case 0:
                hexColor = Color.BLUE;
                break;
            case 1:
                hexColor = Color.GREEN;
                break;
            case 2:
                hexColor = Color.RED;
                break;
            case 3:
                hexColor = Color.YELLOW;
                break;
            case 4:
                hexColor = Color.CYAN;
                break;
            case 5:
                hexColor = Color.MAGENTA;
                break;
            case 6:
                hexColor = Color.DKGRAY;
                break;
            case 7:
                hexColor = Color.LTGRAY;
                break;
            case 8:
                hexColor = Color.GRAY;
                break;
            case 9:
                hexColor = Color.WHITE;
                break;
            default:
                hexColor = Color.BLACK;
                break;
        }
        return hexColor;
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
    public double bytesToKilobytes(long b){
        double ret = (double)b;
        ret /= 1000.0;
        ret = Math.round(ret * 1000.0) / 1000.0;
        return ret;
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
    public String getDataDirection(int n){
        String ret;
        switch(n){
            case(0):
                ret = "Download";
                break;
            case(1):
                ret = "Upload";
                break;
            default:
                ret = "_";
                break;
        }
        return ret;
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
    public abstract void pageActivity();


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
    public abstract void runActivity();

}
