package com.ems305.icastloop.activity;

import android.app.Activity;
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
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.ems305.icastloop.R;

import java.util.ArrayList;

/*
 * Created by Erik on 7/5/13.
 */

public class SettingsActivity extends Activity {

    private Spinner mSpinner;
    private ListView mListView;
    private RadioGroup mRadioGroup;

    // ------------------------------------
    // Lifecycle Methods
    // ------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Update Spinner
        mSpinner = (Spinner) findViewById(R.id.activity_settings_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.radar_names_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("Use My Location");
        arrayList.add("Use Default Location");

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, arrayList);
        mListView = (ListView) findViewById(R.id.activity_settings_list_view);
        mListView.setAdapter(arrayAdapter);
        mListView.setOnItemClickListener(mListItemClickListener);

        // Restore preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Set Default Location No Matter Which Option Is Selected
        String selLocation = settings.getString("defaultLocation", null);
        if (selLocation != null) {
            int pos = adapter.getPosition(selLocation);
            mSpinner.setSelection(pos);
        }

        // Set Whether We Want To Use A Loop Or Still
        boolean useLoop = settings.getBoolean("useLoop", true);
        mRadioGroup = (RadioGroup) findViewById(R.id.activity_settings_radio_group);
        if (useLoop) {
            mRadioGroup.check(R.id.radioLoop);
        } else {
            mRadioGroup.check(R.id.radioStill);
        }

        // Select ListItem From Preferences
        boolean useDefault = settings.getBoolean("defaultMode", false);

        mListView.setItemChecked((useDefault ? 1 : 0), true);
        mSpinner.setEnabled(useDefault);
        mSpinner.setClickable(useDefault);
    }

    // ------------------------------------
    // Menu Methods
    // ------------------------------------

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
                saveSettings();
                exitActivity();
                break;
            default:
                break;
        }
        return true;
    }

    // ------------------------------------
    // Private Methods
    // ------------------------------------

    private void exitActivity() {
        // Go back to Main Screen
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        this.finish();
    }

    private void saveSettings() {

        int position = mListView.getCheckedItemPosition();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("locationMode", (position == 0));
        editor.putBoolean("defaultMode", (position == 1));
        editor.putBoolean("useLoop", (mRadioGroup.getCheckedRadioButtonId() == R.id.radioLoop));
        editor.putString("defaultLocation", mSpinner.getSelectedItem().toString());
        editor.apply();

        this.exitActivity();
    }

    // ------------------------------------
    // Overrides
    // ------------------------------------

    @Override
    public void onBackPressed() {
        // Save Our Settings If They Do A System Back
        this.saveSettings();
        this.exitActivity();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // Save Our Settings If They Do A System Back
                this.saveSettings();
                this.exitActivity();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // ------------------------------------
    // Listeners
    // ------------------------------------

    final AdapterView.OnItemClickListener mListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            mSpinner = (Spinner) findViewById(R.id.activity_settings_spinner);
            mSpinner.setEnabled((position == 1));
            mSpinner.setClickable((position == 1));
        }
    };
}

