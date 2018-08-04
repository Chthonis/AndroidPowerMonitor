package com.molaro.androidpowermonitor.UI;

import android.app.Fragment;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.molaro.androidpowermonitor.Implementation.GeneralData;
import com.molaro.androidpowermonitor.R;

import java.util.ArrayList;

/*
*   Class
* ------------------------
*   Description
*
*/
public class GeneralInfo extends Fragment{
    ExpandableListView listView;
    private GLSurfaceView glSurfaceView;
    private StringBuilder sb;
    private static Context context;
    private View view;

    ArrayList<String> groups;
    ArrayList<ArrayList<String>> children;

    /*===================================== CONSTRUCTOR =====================================
    *   Function GeneralInfo newInstance()
    * ------------------------
    *   Creates a new fragment of GeneralInfo
    */
    public static GeneralInfo newInstance(){
        GeneralInfo fragment = new GeneralInfo();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /*
    *   Function onCreate
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
        groups = new ArrayList<String>();
        children = new ArrayList<ArrayList<String>>();
        GeneralData generalData = new GeneralData(context);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_info_list, container, false);
        return view;
    }

    /*
    *   Function onViewCreated
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String s;
        groups.clear();
        children.clear();
        listView = (ExpandableListView) view.findViewById(R.id.list_general_info);

        GeneralData generalData = new GeneralData(context);

        //GPU Info
        s = generalData.getGpuInfo();
        addGroupChild("GPU INFO", s);

        //Internet Connection
        s = generalData.getInternetConn();
        addGroupChild("NETWORK", s);

        //LCD Screen Brightness
        s = Integer.toString(generalData.getScreenBrightness());
        addGroupChild("LCD SCREEN", s);

        //GPS Status
        if(generalData.getGpsOn())
            s = "GPS location is ON";
        else
            s = "GPS location is OFF";
        addGroupChild("GPS STATUS", s);

        listView.setAdapter(new ExpandableListAdapter(groups, children));
        listView.setGroupIndicator(null);
    }

    private void addGroupChild(String g, String c){
        ArrayList<String> expandableInfo = new ArrayList<String>();
        expandableInfo.add(c);
        children.add(expandableInfo);
        groups.add(g);
    }

    /*==================================== CLASS ExpandableListAdapter ====================================

    *   Class ExpandableListAdapter
    * ------------------------
    *   Description:
    *
    */
    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private final LayoutInflater inf;
        private ArrayList<String> groups;
        private ArrayList<ArrayList<String>> children;

        public ExpandableListAdapter(ArrayList<String> groups, ArrayList<ArrayList<String>> children) {
            this.groups = groups;
            this.children = children;
            inf = LayoutInflater.from(getActivity());
        }

        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return children.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return children.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = inf.inflate(R.layout.item_string_details, parent, false);
                holder = new ViewHolder();

                holder.text = (TextView) convertView.findViewById(R.id.string_details);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(getChild(groupPosition, childPosition).toString());

            return convertView;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inf.inflate(R.layout.item_string_clickable, parent, false);

                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.string_clickable);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(getGroup(groupPosition).toString());

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        /*==================================== CLASS ViewHolder ====================================
        *   Class
        * ------------------------
        *   Description:
        *
        */
        private class ViewHolder {
            TextView text;
        }
        //================================== END CLASS ViewHolder ==================================
    }
    //================================== END CLASS ExpandableListAdapter ==================================
}
