/**
 * file: WhereAmIFragment
 * by: Derek Baumgartner
 * created: 4/14/2014.
 */

package com.assettcu.placesapp.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.activities.HomeActivity;
import com.assettcu.placesapp.adapters.WhereAmIListAdapter;
import com.assettcu.placesapp.helpers.DebugMode;
import com.assettcu.placesapp.models.Place;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class WhereAmIFragment extends Fragment {

    public static final boolean createTestGeofences = false;  // Create Test ASSETT geofences
    private ArrayAdapter<Place> adapter;
    private List<Place> places;
    private Place nearestBuilding;                // Manual building if use is not in any geofences

    private ListView listView;
    private LinearLayout progressSpinner;
    private TextView outOfRangeTextView;

    private BroadcastReceiver receiver;
    private List<Geofence> mGeofenceList;
    private JsonArray buildingsJsonArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_where_am_i, container, false);
        listView = (ListView) view.findViewById(R.id.list_view);
        progressSpinner = (LinearLayout) view.findViewById(R.id.progressSpinner);
        outOfRangeTextView = (TextView) view.findViewById(R.id.outofrange);
        listView.setEmptyView(outOfRangeTextView);

        places = new ArrayList<Place>();
        adapter = new WhereAmIListAdapter(inflater.getContext());
        listView.setAdapter(adapter);

        setOnClickAdapter();

        mGeofenceList = new ArrayList<Geofence>();
        receiver = geofenceBroadcastReceiver();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver,
                new IntentFilter("geofenceEvent"));

        if(createTestGeofences)
            addTestGeofences();

        Activity parent = getActivity();
        if(parent instanceof HomeActivity) {
            buildingsJsonArray = ((HomeActivity) parent).getBuildingsJsonArray();
            Log.d("Assett", "Got BuildingsArray. null = " + (buildingsJsonArray == null));
        }

        // If the JSON array hasn't been fetched yet, get it
        if(buildingsJsonArray == null) {
            progressSpinner.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);

            Ion.with(inflater.getContext()).load("http://places.colorado.edu/api/buildings").asJsonArray()
                    .setCallback(new FutureCallback<JsonArray>() {
                        @Override
                        public void onCompleted(Exception e, JsonArray result) {
                            readBuildingsJson(result);
                        }
                    });

        }
        // Otherwise just load it in again
        else {
            readBuildingsJson(buildingsJsonArray);
        }


        return view;
    }

    public void setOnClickAdapter(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Place place = adapter.getItem(position);
                //BuildingDisplayFragment fragment = BuildingDisplayFragment.newInstance(place);
                BuildingViewPagerFragment fragment = BuildingViewPagerFragment.newInstance(place);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                fragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);

        Activity parent = getActivity();
        if(parent instanceof HomeActivity) {
            ((HomeActivity) parent).removeGeofences();
        }

        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public BroadcastReceiver geofenceBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                if(extras != null) {
                    int transitionType = extras.getInt("transition");
                    String[] triggerIds = extras.getStringArray("triggers");

                    if(triggerIds != null && triggerIds.length > 0) {
                        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {

                            if(nearestBuilding != null){
                                adapter.remove(nearestBuilding);
                                nearestBuilding = null;
                            }

                            for(String trigger : triggerIds) {
                                for(Place p : places) {
                                    if(p.getPlaceName().equals(trigger)) {
                                        adapter.add(p);
                                        Log.d("Places", p.getPlaceName() + ": " + p.getImageURL());

                                        // Debug toast to see what geofences were entered
                                        DebugMode.makeToast(getActivity(), "Entered: "
                                                + TextUtils.join(", ", triggerIds));
                                    }
                                }
                            }
                        }
                        else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
                            for(String trigger : triggerIds) {
                                for(Place p : places) {
                                    if(p.getPlaceName().equals(trigger))
                                        adapter.remove(p);
                                }
                            }

                            // If the user isn't in any geofences, we calculate the nearest one
                            if(adapter.getCount() == 0) {
                                new WaitForGPSLockTask().execute();
                            }

                            // Debug toast to see what geofences were exited
                            DebugMode.makeToast(getActivity(), "Exited: "
                                    + TextUtils.join(", ", triggerIds));
                        }
                    }
                }
            }
        };
    }

    public void addTestGeofences() {
        // Test Geofences located near ASSETT for testing purposes

        addGeofence("Assett E295",         40.013847, -105.250449, 20);
        addGeofence("Assett Second Floor", 40.013803, -105.250443, 30);
        addGeofence("Assett Bus Stop",     40.013708, -105.250771, 20);

        places.add(new Place(1003, "Assett E295", "ASET",
                "http://media.defenceindustrydaily.com/images/AIR_C-295MP_Chile_Concept_lg.jpg"));

        places.add(new Place(1001, "Assett Second Floor", "ASET",
                "http://www.portlandcompany.com/images/rooms/building2secondfloor_2.jpg"));

        places.add(new Place(1002, "Assett Bus Stop", "ASET",
                "http://www.zombiezodiac.com/rob/ped/busstop/keio_bus_stop.JPG"));
    }

    public void readBuildingsJson(JsonArray json)
    {
        buildingsJsonArray = json;

        Activity parent = getActivity();
        if(parent instanceof HomeActivity) {
            ((HomeActivity) parent).setBuildingsJsonArray(json);
            Log.d("Assett", "Set BuildingsJsonArray");
        }

        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(300);
        set.addAnimation(animation);

        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );

        animation.setDuration(300);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        listView.setLayoutAnimation(controller);

        Place place;
        for(int i = 0; i < json.size(); i++)
        {
            JsonObject building = json.get(i).getAsJsonObject();

            // Create new place and add it to array
            place = new Place();
            place.setPlaceID(building.get("placeid").getAsInt());
            place.setPlaceName(building.get("placename").getAsString());
            place.setBuildingCode(building.get("building_code").getAsString());
            place.setImageURL("http://places.colorado.edu" + building.get("path").getAsString());
            place.setLatitude(building.get("latitude").getAsString());
            place.setLongitude(building.get("longitude").getAsString());
            places.add(place);

            // Add Geofence for new place
            addGeofence(place.getPlaceName(),
                    Double.valueOf(place.getLatitude()),
                    Double.valueOf(place.getLongitude()),
                    100);
        }

        if(parent instanceof HomeActivity) {
            ((HomeActivity) parent).addGeofences(mGeofenceList);
        }

        progressSpinner.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);

        // Get the nearest building as a backup
        new WaitForGPSLockTask().execute();
    }

    public void getNearestBuildingJson() {
        Location gpsLocation = getLocation();
        if(adapter.getCount() == 0) {
            Ion.with(this.getActivity()).load(
                    "http://places.colorado.edu/api/nearestbuildings/?latitude=" +
                            gpsLocation.getLatitude() + "&longitude=" +
                            gpsLocation.getLongitude() + "&limit=1"
            ).asJsonArray()
                    .setCallback(new FutureCallback<JsonArray>() {
                        @Override
                        public void onCompleted(Exception e, JsonArray result) {
                            readNearestBuildingJson(result);
                        }
                    });
        }
    }

    public void readNearestBuildingJson(JsonArray json) {
        // Make sure adapter doesn't have any geofences
        if(adapter.getCount() == 0) {
            JsonObject building = json.get(0).getAsJsonObject();
            double distance = building.get("distance").getAsDouble();

            // If building is less than 0.125 miles (~200 meters) from user
            if (distance < 5.125) {
                String buildingName = building.get("placename").getAsString();

                // Find building from list of places and add it to the listview adapter
                for (Place p : places) {
                    if (p.getPlaceName().equals(buildingName)) {
                        adapter.add(p);
                        nearestBuilding = p;
                        DebugMode.makeToast(getActivity(), "Manually Added: " + buildingName);
                    }
                }
            }
            else {
                outOfRangeTextView.setText(R.string.out_of_range);
            }
        }
    }

    public void addGeofence(String id, double latitude, double longitude, float radius){
        Geofence geofence;
        geofence = new Geofence.Builder()
                .setRequestId(id)              // Set name of building
                .setTransitionTypes(1 | 2)     // Entered and Exited transitions
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(600000) // 10 minute expiration time
                .build();
        mGeofenceList.add(geofence);
    }

    public Location getLocation() {
        Activity parent = getActivity();
        if(parent instanceof HomeActivity) {
            return ((HomeActivity) parent).getLocation();
        }
        else {
            return null;
        }
    }

    private void updateIntervalSpeed(long millis, boolean ignore)
    {
        Activity parent = getActivity();

        if(parent instanceof HomeActivity)
        {
            LocationRequest request = ((HomeActivity) parent).getLocationRequest();
            request.setInterval(millis);

            if(ignore) request.setFastestInterval(millis);
            else request.setFastestInterval(1000);
        }
    }

    // Wait for a GPS lock
    class WaitForGPSLockTask extends AsyncTask<Void, Void, Boolean> {
        protected Boolean doInBackground(Void... urls) {
            final int LOCK_ACCURACY = 30;
            int waitTime = 30;                 // Seconds to wait for a GPS lock before giving up
            int waited = 0;                    // Seconds waited for a GPS lock
            Location gps = getLocation();     // Current GPS location

            // Increase the interval speed needed to get a lock
            updateIntervalSpeed(100, true);

            if(gps != null) {
                // While the GPS accuracy is worse than 25 meters AND the user has
                // waited less than 'waitTime' seconds for a GPS lock
                while (true) {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    gps = getLocation();
                    if(gps != null)
                    {
                        if(gps.getAccuracy() <= LOCK_ACCURACY || waited > waitTime) break;
                    }

                    waited++;
                }

                // If the GPS obtained a lock: return true, otherwise: return false
                // Set the interval speed back to normal
                updateIntervalSpeed(5000, false);
                return (gps.getAccuracy() <= LOCK_ACCURACY);
            }

            // If the GPS isn't started, return false;
            return false;
        }

        protected void onPostExecute(Boolean locked) {
            if(locked) {
                getNearestBuildingJson();
            }
        }
    }
}
