package com.ems305.icastloop.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.ems305.icastloop.R;
import com.ems305.icastloop.utility.ICastPrefs;

import java.util.ArrayList;

/*
 * Created by Erik Smith on 7/5/13.
 */

public class SettingsActivity extends Activity {

    private Spinner mSpinner;
    private ListView mListView;
    private RadioGroup mRadioGroup;

    private static final String MY_LOCATION = "Use My Location";
    private static final String DEFAULT_LOCATION = "Use Default Location";

    enum LocationDefault {
        MY_LOCATION,
        DEFAULT_LOCATION
    }

    // ------------------------------------
    // Lifecycle Methods
    // ------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initControls();
        initListeners();

        this.setupActivity();
    }

    @Override
    public void onBackPressed() {

        // Save Our Settings If They Do A System Back
        this.saveSettingsAndExit();
        super.onBackPressed();
    }

    // ------------------------------------
    // Init Methods
    // ------------------------------------

    private void initControls() {

        mSpinner = (Spinner) findViewById(R.id.activity_settings_spinner);
        mListView = (ListView) findViewById(R.id.activity_settings_list_view);
        mRadioGroup = (RadioGroup) findViewById(R.id.activity_settings_radio_group);
    }

    private void initListeners() {

        mListView.setOnItemClickListener(mListItemClickListener);
    }

    private void setupActivity() {

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.radar_names_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(spinnerAdapter);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(MY_LOCATION);
        arrayList.add(DEFAULT_LOCATION);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, arrayList);
        mListView.setAdapter(arrayAdapter);

        // Set Default Location No Matter Which Option Is Selected
        String defaultLocation = ICastPrefs.getInstance().getDefaultLocation();
        if (defaultLocation != null) {
            int pos = spinnerAdapter.getPosition(defaultLocation);
            mSpinner.setSelection(pos);
        }

        // Set Whether We Want To Use A Loop Or Still
        boolean useLoop = ICastPrefs.getInstance().getUseLoop();
        if (useLoop) {
            mRadioGroup.check(R.id.activity_settings_radio_loop);
        } else {
            mRadioGroup.check(R.id.activity_settings_radio_still);
        }

        // Select ListItem From Preferences
        boolean useDefaultLocation = ICastPrefs.getInstance().getDefaultMode();
        if (useDefaultLocation) {
            mListView.setItemChecked(LocationDefault.DEFAULT_LOCATION.ordinal(), true);
        } else {
            mListView.setItemChecked(LocationDefault.MY_LOCATION.ordinal(), true);
        }
        mSpinner.setEnabled(useDefaultLocation);
        mSpinner.setClickable(useDefaultLocation);
    }

    // ------------------------------------
    // Menu Methods
    // ------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Treat "Up" Button As Back Button So We Don't Go Through Full Lifecycle Again...
                onBackPressed();
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }

    // ------------------------------------
    // Private Methods
    // ------------------------------------

    private void saveSettingsAndExit() {

        int position = mListView.getCheckedItemPosition();

        ICastPrefs.getInstance().setDefaultMode(position == LocationDefault.DEFAULT_LOCATION.ordinal());
        ICastPrefs.getInstance().setUseLoop((mRadioGroup.getCheckedRadioButtonId() == R.id.activity_settings_radio_loop));
        ICastPrefs.getInstance().setDefaultLocation(mSpinner.getSelectedItem().toString());
    }

    // ------------------------------------
    // Listeners
    // ------------------------------------

    final AdapterView.OnItemClickListener mListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSpinner.setEnabled((position == LocationDefault.DEFAULT_LOCATION.ordinal()));
            mSpinner.setClickable((position == LocationDefault.DEFAULT_LOCATION.ordinal()));
        }
    };
}

