package com.ems305.icastloop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

/*
 * Created by Erik on 7/5/13.
 */

public class SettingsActivity extends Activity {

    private Spinner spinner;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // Update Spinner
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.radar_names_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ArrayList<String> arrayList = new ArrayList();
        arrayList.add("Use My Location");
        arrayList.add("Use Default Location");

        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_single_choice, arrayList);
        listView = (ListView) findViewById(R.id.optionListView);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView parent, View view, int position, long itemId) {

                spinner = (Spinner) findViewById(R.id.spinner);
                spinner.setEnabled((position == 1));
                spinner.setClickable((position == 1));
            }
        });

        // Restore preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Select Item From Preferences
        boolean useDefault = settings.getBoolean("defaultMode", false);
        if(useDefault){

            listView.setItemChecked(1, true);

            String selLocation = settings.getString("defaultLocation", null);
            if(selLocation != null){
                int pos = adapter.getPosition(selLocation);
                spinner.setSelection(pos);
            }
            spinner.setEnabled(true);
            spinner.setClickable(true);

        } else {

            listView.setItemChecked(0, true);

            spinner.setEnabled(false);
            spinner.setClickable(false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.subpage, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_back:

                this.saveSettings();
                this.exitActivity();


                break;

            default:
                break;
        }
        return true;
    }


    private void exitActivity(){
        // Go back to Main Screen
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        this.finish();
    }


    private void saveSettings(){

        listView = (ListView) findViewById(R.id.optionListView);

        int position = listView.getCheckedItemPosition();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("defaultMode", (position == 1));
        editor.putString("defaultLocation", spinner.getSelectedItem().toString());

        // Commit the edits!
        editor.commit();

        this.exitActivity();
    }


    public void onBackPressed(){
        // Save Our Settings If They Do A System Back
        this.saveSettings();
        this.exitActivity();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                // Save Our Settings If They Do A System Back
                this.saveSettings();
                this.exitActivity();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

