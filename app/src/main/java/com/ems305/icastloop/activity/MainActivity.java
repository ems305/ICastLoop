package com.ems305.icastloop.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.ems305.icastloop.R;
import com.ems305.icastloop.model.ICastLocation;
import com.ems305.icastloop.utility.ICastPrefs;
import com.ems305.icastloop.utility.NetworkUtils;

/**
 * Created by Erik Smith on 7/5/14.
 */
public class MainActivity extends Activity {

    // TODO: Add OpenWeatherMap API
    // http://code.aksingh.net/owm-japis/src
    // http://openweathermap.org/

    private WebView mWebView;
    private Spinner mSpinner;
    private ProgressBar mProgressBar;

    // ------------------------------------
    // Lifecycle Methods
    // ------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initControls();
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.setupActivity();
    }

    @Override
    public void onBackPressed() {

        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    // ------------------------------------
    // Init Methods
    // ------------------------------------

    private void initControls() {

        mWebView = (WebView) findViewById(R.id.activity_main_web_view);
        mSpinner = (Spinner) findViewById(R.id.activity_main_spinner);
        mProgressBar = (ProgressBar) findViewById(R.id.activity_main_progress_bar);
    }

    private void initListeners() {

        mSpinner.setOnItemSelectedListener(mItemSelectedListener);
    }

    // ------------------------------------
    // Menu Methods
    // ------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Treat "Up" Button As Back Button So We Don't Go Through Full Lifecycle Again...
                onBackPressed();
                break;
            case R.id.action_refresh:
                // Refresh Image
                this.updateRadarImage();
                break;
            case R.id.action_preferences:
                Intent preferenceIntent = new Intent(this, SettingsActivity.class);
                startActivity(preferenceIntent);
                break;
            case R.id.action_about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
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

    private void setupActivity() {

        // Check For Network Availability
        if (!NetworkUtils.isOnline()) {
            // Bounce, They Don't Have A Connection
            Toast.makeText(this, "You must have an active network connection", Toast.LENGTH_LONG).show();
            return;
        }

        // Update Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.radar_names_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);

        // Set Defaults
        boolean useDefaultLocation = ICastPrefs.getInstance().getDefaultMode();
        if (useDefaultLocation) {
            String selLocation = ICastPrefs.getInstance().getDefaultLocation();
            if (!TextUtils.isEmpty(selLocation)) {
                mSpinner.setSelection(adapter.getPosition(selLocation));
                this.updateRadarImage();
            }
        } else {
            // Acquire Location... Callback Handles Loading Image
            ICastLocation myLocation = new ICastLocation();
            myLocation.getLocation(this, mLocationResultCallback);
        }
    }

    private void updateRadarImage() {

        // Set Our WebView Defaults
        this.setupWebView(mWebView);

        // Get Code
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.radar_codes_array, android.R.layout.simple_spinner_item);
        String radarCode = adapter.getItem(mSpinner.getSelectedItemPosition()).toString();

        // Determine Endpoint Based On Still/Animation
        boolean useLoop = ICastPrefs.getInstance().getUseLoop();
        String radarEndpoint = "/WxImages/" + (useLoop ? "RadarLoop/" + radarCode + "_None_anim" : "Radar/" + radarCode);

        String html = "<body><table width=[[WIDTH]]px ><tr><td><img src=\"http://images.intellicast.com[[ENDPOINT]].gif\" width=[[WIDTH]]px /></td></tr></table></body>";
        html = html.replace("[[WIDTH]]", Integer.toString(mWebView.getWidth()));
        html = html.replace("[[ENDPOINT]]", radarEndpoint);
        mWebView.loadData(html, "text/html", "utf-8");
        mWebView.setBackgroundColor(Color.TRANSPARENT);
    }

    private void setupWebView(WebView webView) {

        // Clear Cache
        webView.clearHistory();
        webView.clearFormData();
        webView.clearCache(true);

        // Set All The Settings We Need To Have It Look Nice In The Device
        WebSettings settings = webView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        mProgressBar.setProgress(0);
        mProgressBar.setVisibility(View.VISIBLE);

        webView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress) {

                mProgressBar.setProgress(progress);
                if (progress == 100) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // ------------------------------------
    // Listeners
    // ------------------------------------

    final AdapterView.OnItemSelectedListener mItemSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            MainActivity.this.updateRadarImage();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    final ICastLocation.LocationResult mLocationResultCallback = new ICastLocation.LocationResult() {

        @Override
        public void gotLocation(Location location) {

            if (location == null) {
                return;
            }

            // Set Preferences...
            ICastPrefs.getInstance().setLatitude(Double.doubleToLongBits(location.getLatitude()));
            ICastPrefs.getInstance().setLongitude(Double.doubleToLongBits(location.getLongitude()));

            // Find Closest Radar Location
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            if (latitude != 0 && longitude != 0) {

                Location userLoc = new Location("iCastUserLocation");
                userLoc.setLatitude(latitude);
                userLoc.setLongitude(longitude);

                String[] radarCoordinates = getResources().getStringArray(R.array.radar_coordinate_array);
                float maxDist = Float.MAX_VALUE;
                int nearLocPos = 0;

                for (int i = 0; i < radarCoordinates.length; i++) {

                    if (radarCoordinates[i].contains(",")) {

                        // Coordinates Are Formatted Like '34.746481,-92.289595'
                        String[] coordinate = radarCoordinates[i].split(",");

                        Double dLat = Double.parseDouble(coordinate[0]);
                        Double dLng = Double.parseDouble(coordinate[1]);

                        // Turn It To Android Location Object
                        Location coordinateLoc = new Location("coordinate");
                        coordinateLoc.setLatitude(dLat);
                        coordinateLoc.setLongitude(dLng);

                        float distTo = userLoc.distanceTo(coordinateLoc); // Android Built In Method To Compare Two Locations, Returns In Meters
                        if (distTo < maxDist) {
                            maxDist = distTo;
                            nearLocPos = i;
                        }
                    }
                }

                // Don't Need To Zero Check, If It's Zero It Will Set To First Item In Spinner
                mSpinner.setSelection(nearLocPos);
                MainActivity.this.updateRadarImage();
            }
        }
    };
}

/*
      Non-Animation
      http://images.intellicast.com/WxImages/RadarLoop/[REPLACE].gif

      Animation
      http://images.intellicast.com/WxImages/RadarLoop/[REPLACE]_None_anim.gif

      <option value="default">United States</option>
      <option value="lit">AR - Little Rock</option>
      <option value="prc">AZ - Prescott</option>
      <option value="bfl">CA - Bakersfield</option>
      <option value="den">CO - Denver</option>
      <option value="hfd">CT - Hartford</option>
      <option value="eyw">FL - Key West</option>
      <option value="pie">FL - Saint Petersburg</option>
      <option value="csg">GA - Columbus</option>
      <option value="dsm">IA - Des Moines</option>
      <option value="myl">ID - McCall</option>
      <option value="spi">IL - Springfield</option>
      <option value="sln">KS - Salina</option>
      <option value="bwg">KY - Bowling Green</option>
      <option value="msy">LA - New Orleans</option>
      <option value="cad">MI - Cadillac</option>
      <option value="stc">MN - Saint Cloud</option>
      <option value="jef">MO - Jefferson City</option>
      <option value="tvr">MS - Vicksburg</option>
      <option value="lwt">MT - Lewistown</option>
      <option value="clt">NC - Charlotte</option>
      <option value="bis">ND - Bismarck</option>
      <option value="lbf">NE - North Platte</option>
      <option value="bml">NH - Berlin</option>
      <option value="row">NM - Roswell</option>
      <option value="rno">NV - Reno</option>
      <option value="bgm">NY - Binghamton</option>
      <option value="day">OH - Dayton</option>
      <option value="law">OK - Lawton</option>
      <option value="rdm">OR - Redmond</option>
      <option value="pir">SD - Pierre</option>
      <option value="bro">TX - Brownsville</option>
      <option value="sat">TX - San Antonio</option>
      <option value="pvu">UT - Provo</option>
      <option value="fcx">VA - Roanoke</option>
      <option value="shd">VA - Staunton</option>
      <option value="tiw">WA - Tacoma</option>
      <option value="riw">WY - Riverton</option>
*/
