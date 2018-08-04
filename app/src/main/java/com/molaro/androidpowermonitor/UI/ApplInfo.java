package com.molaro.androidpowermonitor.UI;

import android.app.AppOpsManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.molaro.androidpowermonitor.Implementation.ApplData;
import com.molaro.androidpowermonitor.R;
import com.molaro.androidpowermonitor.Implementation.RecordValues;

import java.io.File;
import java.util.ArrayList;


//=================== startService(new Intent(this, RecordValues.class));

/*
*   Class
* ------------------------
*   Description
*
*/
public class ApplInfo extends Fragment {

    public static final String USAGE_STATS_SERVICE = "usagestats";
    final int MY_PERMISSIONS_REQUEST = 1;

    private int keyNum = 1;

    private final Handler handler = new Handler();
    private Runnable timer;

    EditText edtext;

    private int lastX = 0;

    private static Context context;


    View view;
    ExpandableListView listView;


    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String SCREEN_CLASS_NAME = "com.android.settings.RunningServices";


    /*===================================== CONSTRUCTOR =====================================
    *   Function ApplInfo newInstance()
    * ------------------------
    *   Creates a new fragment of ApplInfo
    */
    public static ApplInfo newInstance(int key){
        ApplInfo fragment = new ApplInfo();
        Bundle args = new Bundle();
        args.putInt("key", key); // 0 for CPU % graph, 1 for frequency graph
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        keyNum = getArguments().getInt("key");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_app_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ApplData applObj = new ApplData(context);

        Button sampleButton = (Button) view.findViewById(R.id.default_sample_button);
        edtext = (EditText) view.findViewById(R.id.base_sample_time);

        ViewGroup layout = (ViewGroup) sampleButton.getParent();
        if(keyNum == 0){
            getRunningApps(applObj);
        }
        else if(keyNum == 1){
            getAllApps(applObj);
        }

        sampleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, RecordValues.class);
                String result = "10";
                result = edtext.getText().toString();
                intent.putExtra("collectstats", "collectstats");
                intent.putExtra("sampletime", result);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.System.canWrite(context)) {
                        context.startService(intent);
                    }
                    else {
                        Intent i = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        i.setData(Uri.parse("package:" + getActivity().getPackageName()));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                }
                else {
                    context.startService(intent);
                }

            }
        });

        listView = (ExpandableListView) view.findViewById(R.id.info_list);
        listView.setAdapter(new ExpandableListAdapter(applObj));
        listView.setGroupIndicator(null);

    }

    /*
    *   Function getRunningApps
    * ------------------------
    *   Description:
    *       Does error/version checking to get all
    *           currently running apps and related info.
    *
    *   @returns
    *       void
    */
    private void getRunningApps(ApplData applObj){
        TextView tv = (TextView) view.findViewById(R.id.error_text);
        Button b = (Button) view.findViewById(R.id.app_button);
        ViewGroup layout = (ViewGroup) b.getParent();


        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
            if(needPermissionForBlocking()) {
                Log.d("Executed app", "######### NO PERMISSIONS FOUND ##########");
                tv.setText("Due to security updates in android 7.0+, currently active applications " +
                        "cannot be viewed without proper permissions. To enable permissions click the " +
                        "button below, find 'Android Power Monitor', click it and enable permissons.");

                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        //intent.setClassName(APP_DETAILS_PACKAGE_NAME,  SCREEN_CLASS_NAME);
                        context.startActivity(intent);
                    }
                });
            }
            else if (!applObj.areAppsFoundUsm()) {
                Log.d("Executed app", "######### NO APP FOUND ##########" );
                tv.setText("Due to security updates in android 7.0+, currently active applications " +
                        "cannot be viewed. Click the button below to be redirected to a list of all " +
                        "running apps provided by your phone.");

                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        //intent.setClassName(APP_DETAILS_PACKAGE_NAME,  SCREEN_CLASS_NAME);
                        context.startActivity(intent);
                    }
                });
            }
            else if (applObj.areAppsFoundUsm()) {
                layout.removeView(b);
                layout.removeView(tv);
            }
        } else{
            layout.removeView(b);
            layout.removeView(tv);
        }
        applObj.updateRunningAppl();
    }


    /*
    *   Function
    * ------------------------
    *   Description:
    *       gets a list of all installed (non-system)
    *           applications and related information.
    *
    *   @returns
    *       void
    */
    private void getAllApps(ApplData applObj){
        TextView tv = (TextView) view.findViewById(R.id.error_text);
        Button b = (Button) view.findViewById(R.id.app_button);
        ViewGroup layout = (ViewGroup) b.getParent();

        applObj.updateInstalledAppl();

        layout.removeView(b);
        layout.removeView(tv);
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
    public static boolean needPermissionForBlocking(){
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return  (mode != AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }




    /*==================================== CLASS ExpandableListAdapter ====================================
    *   Class ExpandableListAdapter
    * ------------------------
    *   Creates a custom Expandable List View format for displaying apps in this fragment.
    *
    *
    *
    *
    *
    */
    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
        private static final String SCREEN_CLASS_NAME = "com.android.settings.RunningServices";
        private final LayoutInflater inf;
        ApplData applData;
        CustomInfo ci;

        public ExpandableListAdapter(ApplData applData) {
            this.applData = applData;
            inf = LayoutInflater.from(getActivity());
            ci = new CustomInfo();
            ci.groups = applData.getApplNames();
            ci.groupsTwo = applData.getApplPackages();
            ci.icon = applData.getIconList();
            ci.children = applData.getApplPid();
            ci.childrenTwo = applData.getEmptyVoltList();
            ci.childrenThree = applData.getEmptyAmpList();
            ci.childrenFour = applData.getEmptyTimeList();
        }

        @Override
        public int getGroupCount() {
            return ci.groups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return ci.children.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return ci.groups.get(groupPosition);
        }

        public Object getGroup(int groupPosition, int groupNum) {
            Object ret = new Object();
            if(groupNum == 1)
                ret = ci.groups.get(groupPosition);
            else if(groupNum == 2)
                ret = ci.groupsTwo.get(groupPosition);
            return ret;
        }

        public Drawable getGroupImg(int groupPosition) {
            return ci.icon.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return ci.children.get(groupPosition).get(childPosition);
        }

        public Object getChild(int groupPosition, int childPosition, int childNum) {
            Object ret = new Object();
            if(childNum == 1)
                ret = ci.children.get(groupPosition).get(childPosition);
            else if(childNum == 2)
                ret = ci.childrenTwo.get(groupPosition).get(childPosition);
            else if(childNum == 3)
                ret = ci.childrenThree.get(groupPosition).get(childPosition);
            else if(childNum == 4)
                ret = ci.childrenFour.get(groupPosition).get(childPosition);
            return ret;
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
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            final ViewHolder holder;


            if (convertView == null) {
                convertView = inf.inflate(R.layout.item_app_details, parent, false);
                holder = new ViewHolder();

                holder.text1 = (TextView) convertView.findViewById(R.id.textView1);
                holder.text2 = (TextView) convertView.findViewById(R.id.textView2);
                holder.text3 = (TextView) convertView.findViewById(R.id.textView3);
                holder.text4 = (TextView) convertView.findViewById(R.id.textView4);
                holder.edit = (EditText) convertView.findViewById(R.id.sample_time);
                holder.buttonCenter = (Button) convertView.findViewById(R.id.center_button);
                holder.buttonRight = (Button) convertView.findViewById(R.id.right_button);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text1.setText(getChild(groupPosition, childPosition, 1).toString());
            holder.text2.setText(getChild(groupPosition, childPosition, 2).toString());
            holder.text3.setText(getChild(groupPosition, childPosition, 3).toString());
            holder.text4.setText(getChild(groupPosition, childPosition, 4).toString());

            final String packageName = getGroup(groupPosition, 2).toString();

            String[] tokens;
            File file = new File(context.getFilesDir(), "AppUsage.txt");

            String line = applData.fileSingleSearch(file.getAbsolutePath(), packageName);
            if(line != null) {
                tokens = line.split(" ");
                double remainingTime = Double.parseDouble(tokens[1]);
                double baseRemainingTime = Double.parseDouble(tokens[2]);
                double appBatteryUsage = Double.parseDouble(tokens[3]);

                double difference_mW = 0;
                appBatteryUsage = Math.abs(appBatteryUsage);
                difference_mW = Math.abs(difference_mW);

                holder.text2.setText(remainingTime + " hours remaining while running App");
                holder.text3.setText(baseRemainingTime + " hours remaining without running App");
                holder.text4.setText("App consumes " + Double.toString(appBatteryUsage) + "% of current usage");
            }


            holder.buttonCenter.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    String result = "10";
                    result = holder.edit.getText().toString();
                    Intent i = new Intent(context, RecordValues.class);
                    i.putExtra("packagename", packageName);
                    i.putExtra("isapp", true);
                    i.putExtra("sampletime", result);
                    i.putExtra("collectstats", "");
                    context.startService(i);




                }
            });
            holder.buttonRight.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + packageName));
                    startActivity(i);
                }
            });
            return convertView;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inf.inflate(R.layout.item_app_clickable, parent, false);

                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.lblImage);
                holder.text1 = (TextView) convertView.findViewById(R.id.lblListItem);
                holder.text2 = (TextView) convertView.findViewById(R.id.lblPackage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text1.setText(getGroup(groupPosition, 1).toString());
            holder.text2.setText(getGroup(groupPosition, 2).toString());
            holder.icon.setImageDrawable(getGroupImg(groupPosition));

            return convertView;
        }



        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }



        /*==================================== CLASS CustomInfo ====================================
        *   Class
        * ------------------------
        *   Description
        *
        */
        private class CustomInfo {
            ArrayList<String> groups = new ArrayList<String>();
            ArrayList<String> groupsTwo = new ArrayList<String>();
            ArrayList<Drawable> icon = new ArrayList<Drawable>();
            ArrayList<ArrayList<String>> children = new ArrayList<ArrayList<String>>();
            ArrayList<ArrayList<String>> childrenTwo = new ArrayList<ArrayList<String>>();
            ArrayList<ArrayList<String>> childrenThree = new ArrayList<ArrayList<String>>();
            ArrayList<ArrayList<String>> childrenFour = new ArrayList<ArrayList<String>>();
        }
        //================================== END CLASS CustomInfo ==================================

        /*==================================== CLASS ViewHolder ====================================
        *   Class
        * ------------------------
        *   Description
        *
        */
        private class ViewHolder {
            ImageView icon;
            TextView text1;
            TextView text2;
            TextView text3;
            TextView text4;
            EditText edit;
            Button buttonCenter;
            Button buttonRight;
        }
        //================================== END CLASS ViewHolder ==================================
    }
    //================================== END CLASS ExpandableListAdapter ==================================

}
