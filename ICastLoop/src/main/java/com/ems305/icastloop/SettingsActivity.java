package com.ems305.icastloop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by Erik on 7/5/13.
 */
public class SettingsActivity extends Activity {

    private Spinner spinner;
    private ListView listView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // Update Spinner
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.radar_names_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(adapter);

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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView parent, View view, int position, long itemId) {

                spinner = (Spinner) findViewById(R.id.spinner);
                String selectedFromList = (String) (listView.getItemAtPosition(position));
                spinner.setEnabled((selectedFromList == "Set Default Location"));
                spinner.setClickable((selectedFromList == "Set Default Location"));

            }
        });
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
    public boolean isEnabled(int position) {
        String selectedItem = listView.getSelectedItem().toString();
        if(selectedItem == "Set Default Location"){
            return false;
        }
        return true;
    }
*/

/*
    @Override
    public void onItemClick(AdapterView arg0, View view, int position,
                            long arg3) {


    }

*/
}

