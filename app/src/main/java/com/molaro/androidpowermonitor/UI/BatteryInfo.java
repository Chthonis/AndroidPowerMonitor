package com.molaro.androidpowermonitor.UI;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.molaro.androidpowermonitor.Implementation.BatteryData;
import com.molaro.androidpowermonitor.Implementation.StoreData;
import com.molaro.androidpowermonitor.R;

import java.util.ArrayList;

/*
*   Class
* ------------------------
*   Description
*
*/
public class BatteryInfo extends Fragment{

    private int MY_PERMISSIONS_REQUEST = 1;

    private double largest_V = 2;
    private double largest_watt = 2;


    private final Handler handler = new Handler();
    private Runnable timer;

    private LineGraphSeries<DataPoint> seriesTop;
    private LineGraphSeries<DataPoint> seriesBot;
    private GraphView graphTop;
    private GraphView graphBot;

    private BatteryData batteryObj;
    private StoreData sd;


    private ArrayAdapter<String> listViewAdapter;
    private ListView listView;
    private ArrayList<String> allDetails;
    private int lastXTop = 0;
    private int lastXBot = 0;

    private double currmAh = 0.0;
    private double currmV = 0.0;


    Context context;

    /*===================================== CONSTRUCTOR =====================================
    *   Function BatteryInfo newInstance()
    * ------------------------
    *   Creates a new fragment of BatteryInfo
    */
    public static BatteryInfo newInstance(){
        BatteryInfo fragment = new BatteryInfo();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        allDetails = new ArrayList<String>();
        seriesTop = new LineGraphSeries<DataPoint>();
        seriesBot = new LineGraphSeries<DataPoint>();
        context = getActivity().getApplicationContext();
        batteryObj = new BatteryData(context);
        sd = new StoreData(context);

    }



    /*
    *   Function onCreateView
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.activity_double_graph, container, false);

        graphTop = (GraphView) view.findViewById(R.id.graph_top);
        graphTop.addSeries(seriesTop);
        Viewport viewportOne = graphTop.getViewport();
        viewportOne.setYAxisBoundsManual(true);
        viewportOne.setMinY(0.0);
        viewportOne.setXAxisBoundsManual(true);
        viewportOne.setMinX(0.0);
        viewportOne.setMaxX(200.0);
        viewportOne.setScrollable(true);

        graphBot = (GraphView) view.findViewById(R.id.graph_bot);
        graphBot.addSeries(seriesBot);
        Viewport viewportTwo = graphBot.getViewport();
        viewportTwo.setYAxisBoundsManual(true);
        viewportTwo.setMinY(0.0);
        viewportTwo.setXAxisBoundsManual(true);
        viewportTwo.setMinX(0.0);
        viewportTwo.setMaxX(200.0);
        viewportTwo.setScrollable(true);

        graphTop.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX){
                if(isValueX) {
                    return super.formatLabel(value, isValueX);
                }
                return super.formatLabel(value, isValueX) + "W";
            }
        });

        graphBot.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX){
                if(isValueX) {
                    return super.formatLabel(value, isValueX);
                }
                return super.formatLabel(value, isValueX) + "V";
            }
        });

        pageActivity();

        listView = (ListView)view.findViewById(R.id.power_list);

        listViewAdapter = new ArrayAdapter<String> (getActivity(), android.R.layout.simple_list_item_1, allDetails);

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
    public void onPause() {
        handler.removeCallbacks(timer);
        super.onPause();
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
    public void pageActivity() {
        //sd.storeStr("mAh.txt", SEPARATOR);
        //sd.storeStr("mV.txt", SEPARATOR);
        runActivity();
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
    public void runActivity() {

        allDetails.clear();

        double old_mV = batteryObj.getBatteryVoltage();
        double old_mAh = batteryObj.getmAh();
        double old_mA = batteryObj.getCurrentCurrent();
        double old_mW = (old_mV * old_mA)/1000;


        batteryObj.updateInfo();

        double mV = batteryObj.getBatteryVoltage();
        double mAh = batteryObj.getmAh();
        double capacity = batteryObj.getBatteryCapacity();
        double ppLeft = batteryObj.getBatteryPercent();
        double mA = batteryObj.getCurrentCurrent();
        double mW = (mV * mA)/1000;



        double diff_mV = old_mV - mV;
        double diff_mAh = old_mAh - mAh;
        double diff_mA = old_mA - mA;
        double diff_mW = old_mW - mW;

        double volt = mV/1000;
        double watt = mW/1000;

        if(volt > largest_V)
            largest_V = volt;
        if(watt > largest_watt)
            largest_watt = watt;

        diff_mV = Math.round(diff_mV * 1000.0) / 1000.0;
        diff_mAh = Math.round(diff_mAh * 1000.0) / 1000.0;
        diff_mA = Math.round(diff_mA * 1000.0) / 1000.0;
        diff_mW = Math.round(diff_mW * 1000.0) / 1000.0;

        diff_mV = Math.abs(diff_mV);
        diff_mAh = Math.abs(diff_mAh);
        diff_mA = Math.abs(diff_mA);
        diff_mW = Math.abs(diff_mW);
        mV =  Math.abs(mV);
        mAh =  Math.abs(mAh);
        mA =  Math.abs(mA);
        mW =  Math.abs(mW);

        allDetails.add("Power: " + Double.toString(mW) + "mW\nChange: " + Double.toString(diff_mW) + "mW");
        allDetails.add("Current Capacity: " + Double.toString(mAh) + "mAh\nChange: " + Double.toString(diff_mAh) + "mAh");
        allDetails.add("Total Capacity: " + Double.toString(capacity) + "mAh");
        allDetails.add("Power Percentage: " + Double.toString(ppLeft) + "%");
        allDetails.add("Voltage: " + Double.toString(mV) + "mV\nChange: " + Double.toString(diff_mV) + "mV");
        allDetails.add("Current Current: " + Double.toString(mA) + "mA\nChange: " + Double.toString(diff_mA) + "mA");
        allDetails.add("Avg Current: " + Double.toString(batteryObj.getAvgCurrent()) + "mA");


        Viewport viewportOne = graphTop.getViewport();
        viewportOne.setMaxY(largest_watt + 1);
        Viewport viewportTwo = graphBot.getViewport();
        viewportTwo.setMaxY(largest_V + 1);

        seriesTop.appendData(new DataPoint(lastXTop++, watt), true, 200);
        seriesBot.appendData(new DataPoint(lastXBot++, volt), true, 200);


        currmAh = mAh;
        currmV = mV;


    }
}
