package com.ems305.icastloop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Erik on 7/5/13.
 */
public class SettingsActivity extends Activity {


    private ListView listView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        ArrayList<String> arrayList; // list of the strings that should appear in ListView
        ArrayAdapter arrayAdapter; // a middle man to bind ListView and array list

        //In addition to list view we need objects of ArrayList and ArrayAdapter .
        //now we have to initialize all objects in Oncreate() method .

        listView = (ListView) findViewById(R.id.optionListView);

        arrayList = new ArrayList();
        arrayList.add("Use My Location");
        arrayList.add("Set Default Location");

        arrayAdapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_single_choice,arrayList);
        listView.setAdapter(arrayAdapter);

        //listView.setOnItemClickListener(this.onItemClick());
    }


    // Button Clicks
    public void onBtnClicked(View v){

        if(v.getId() == R.id.backButton){
            // Go back to Main Screen
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            this.finish();
        }
    }


/*
    @Override
    public void onItemClick(AdapterView arg0, View view, int position,
                            long arg3) {


    }

*/
}

