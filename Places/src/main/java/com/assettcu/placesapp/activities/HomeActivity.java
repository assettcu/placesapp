package com.assettcu.placesapp.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.fragments.BuildingDisplayFragment;
import com.assettcu.placesapp.fragments.NavigationDrawerFragment;
import com.assettcu.placesapp.helpers.DebugMode;
import com.assettcu.placesapp.helpers.NavigationHelper;
import com.assettcu.placesapp.helpers.ReceiveTransitionsIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.gson.JsonArray;
import com.bugsense.trace.BugSenseHandler;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationClient.OnAddGeofencesResultListener,
        LocationClient.OnRemoveGeofencesResultListener,
        BuildingDisplayFragment.OnFragmentInteractionListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static int GPS_UPDATE_INTERVAL = 5000; // Update every 10 seconds
    private final static int GPS_FASTEST_UPDATE_INTERVAL = 1000; // Fastest update is 1 second

    private NavigationHelper navigationHelper;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private LocationRequest mLocationRequest;
    private LocationClient mLocationClient;

    private JsonArray buildingsJsonArray;

    PendingIntent pendIntent;
    Intent intent;
    List<String> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        navigationHelper = new NavigationHelper(this);
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(HomeActivity.this, "bc260ea5");
        setContentView(R.layout.activity_home);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        places = new ArrayList<String>();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(GPS_UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(GPS_FASTEST_UPDATE_INTERVAL);
        mLocationClient = new LocationClient(this, this, this);
    }

    @Override
    protected void onStart() {
        mLocationClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mLocationClient.isConnected()) {
            // Make sure geofences are removed if application exits
            if(pendIntent != null) {
                mLocationClient.removeGeofences(pendIntent, this);
            }
            mLocationClient.removeLocationUpdates(this);
        }
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment;

        if(position <= navigationHelper.getNumSupportedFragments())
        {
            // Get the data about the selected fragment
            fragment = navigationHelper.getFragmentAtPosition(position);
            mTitle = navigationHelper.getTitleAtPosition(position);

            if(mTitle.equals("Settings"))
            {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return;
            }
        }
        else
        {
            fragment = navigationHelper.getFragmentAtPosition(0);
            mTitle = navigationHelper.getTitleAtPosition(position);
        }

        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.home, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void addGeofences(List<Geofence> mGeofenceList) {
        if(mLocationClient.isConnected()) {
            intent = new Intent(this, ReceiveTransitionsIntentService.class);
            pendIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mLocationClient.addGeofences(mGeofenceList, pendIntent, this);
        }
    }

    public void removeGeofences() {
        if(mLocationClient.isConnected() && pendIntent != null) {
            mLocationClient.removeGeofences(pendIntent, this);
        }
    }

    @Override
    public void onRemoveGeofencesByRequestIdsResult(int i, String[] strings) {}

    @Override
    public void onRemoveGeofencesByPendingIntentResult(int statusCode,PendingIntent requestIntent) {
        if (statusCode == LocationStatusCodes.SUCCESS) {
            DebugMode.makeToast(this, "Removed all Geofences");
        }
        else {
            DebugMode.makeToast(this, "Failed to remove Geofences");
        }
    }

    /*
    * Called by Location Services when the request to connect the
    * client finishes successfully. At this point, you can
    * request the current location or start periodic updates
    */
    @Override
    public void onConnected(Bundle dataBundle) {
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() { }

    /*
     * Called by Location Services if the attempt to start Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
        else {
            int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            if (errorCode != ConnectionResult.SUCCESS) {
                GooglePlayServicesUtil.getErrorDialog(errorCode, this, 0).show();
            }
        }
    }

    @Override
    public void onAddGeofencesResult(int i, String[] strings) {
        if (LocationStatusCodes.SUCCESS == i) {
            DebugMode.makeToast(this, "Added " + strings.length + " Geofences");
        }
        else {
            DebugMode.makeToast(this, "Failed to add Geofences");
        }
    }

    @Override
    public void onFragmentInteraction(String lat, String lon)
    {
        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lon);
        DebugMode.makeToast(this, latitude + "," + longitude);
        //Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + latitude + "," + longitude));
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(
                "http://maps.google.com/maps?daddr=" + latitude + "," + longitude + "&dirflg=w"));
        startActivity(i);
    }

    /** Never Used
    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        private Dialog mDialog;
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    private boolean servicesConnected() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), "Location Sample");
            }
            return false;
        }
    }
    **/

    @Override
    public void onLocationChanged(Location location) { }

    public LocationRequest getLocationRequest()
    {
        return mLocationRequest;
    }

    // Get the buildingsJsonArray. Can return null.
    public JsonArray getBuildingsJsonArray() {
        return buildingsJsonArray;
    }

    // Set the buildingsJsonArray
    public void setBuildingsJsonArray(JsonArray buildingsJsonArray) {
        this.buildingsJsonArray = buildingsJsonArray;
    }

    // Get the current location. Can return null.
    public Location getLocation() {
        if(mLocationClient.isConnected()){
            return mLocationClient.getLastLocation();
        }
        else {
            return null;
        }
    }

}
