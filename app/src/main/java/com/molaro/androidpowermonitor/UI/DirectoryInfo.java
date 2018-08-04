package com.molaro.androidpowermonitor.UI;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.molaro.androidpowermonitor.Implementation.DirectoryData;
import com.molaro.androidpowermonitor.R;

import java.util.ArrayList;


/*
*   Class
* ------------------------
*   Description
*
*/
public class DirectoryInfo extends Fragment implements View.OnClickListener{

//===============================================================================

    private ArrayAdapter<String> listViewAdapter;
    private ArrayList<String> currDirectories;
    private ArrayList<String> prevDirectories;

    private ArrayList<ArrayList<String>> allPrevDirectories;

    private DirectoryData proc;



    /*===================================== CONSTRUCTOR =====================================
    *   Function DirectoryInfo newInstance()
    * ------------------------
    *   Creates a new fragment of DirectoryInfo
    */
    public static DirectoryInfo newInstance(){
        DirectoryInfo fragment = new DirectoryInfo();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        proc = new DirectoryData();
        currDirectories = new ArrayList<String>();
        prevDirectories = new ArrayList<String>();
        allPrevDirectories = new ArrayList<ArrayList<String>>();
    }

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.activity_directory_list, container, false);
        final ListView listView = (ListView)view.findViewById(R.id.info_list);
        currDirectories.clear();
        currDirectories.addAll(proc.getDirectories());

        listViewAdapter = new ArrayAdapter<String> (getActivity(), android.R.layout.simple_list_item_1, currDirectories);
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                nextLayer(text);

            }
        });

        Button homeButton = (Button) view.findViewById(R.id.home_button);
        Button backButton = (Button) view.findViewById(R.id.back_button);

        homeButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        return view;
    }

    
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.home_button:
                homeLayer();
                break;
            case R.id.back_button:
                lastLayer();
                break;
        }
    }


    /*
    *   Function nextLayer
    * ------------------------
    *   Description:
    *       Extends one directory "deeper" and saves previous directory.
    *
    *   @params
    *       String s - new directory name
    *
    *   @returns
    *       void
    */
    private void nextLayer(String s){


        ArrayList<String> new_dir = new ArrayList<String>();
        proc.updateDirectories(s);
        if(proc.getDirectories() == null)
            return;
        new_dir.addAll(proc.getDirectories());



        ArrayList<String> temp_dir = new ArrayList<String>();

        temp_dir.addAll(currDirectories);

        allPrevDirectories.add(temp_dir);

        currDirectories.clear();

        currDirectories.addAll(new_dir);

        listViewAdapter.notifyDataSetChanged();
    }


    /*
    *   Function lastLayer
    * ------------------------
    *   Description:
    *       Returns to the previous directory /..
    *
    *   @returns
    *       void
    */
    private void lastLayer(){
        if(allPrevDirectories.size() == 0)
            return;

        currDirectories.clear();
        prevDirectories.clear();
        prevDirectories = allPrevDirectories.get(allPrevDirectories.size() - 1);
        allPrevDirectories.remove(allPrevDirectories.size() - 1);
        currDirectories.addAll(prevDirectories);
        listViewAdapter.notifyDataSetChanged();
    }


    /*
    *   Function homeLayer
    * ------------------------
    *   Description:
    *       Goes to original base directory
    *           and clears saved directories
    *           
    *   @returns
    *       void
    */
    private void homeLayer() {
        if(allPrevDirectories.size() == 0)
            return;

        currDirectories.clear();
        prevDirectories = allPrevDirectories.get(0);
        currDirectories.addAll(prevDirectories);
        allPrevDirectories.clear();
        listViewAdapter.notifyDataSetChanged();

    }



}
