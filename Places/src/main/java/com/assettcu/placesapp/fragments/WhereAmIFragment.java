/**
 * file: WhereAmIFragment
 * by: Derek Baumgartner
 * created: 4/14/2014.
 */

package com.assettcu.placesapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.assettcu.placesapp.adapters.CustomAdapter;
import com.assettcu.placesapp.HomeActivity;
import com.assettcu.placesapp.helpers.JsonRequest;
import com.assettcu.placesapp.R;
import com.assettcu.placesapp.models.Place;
import com.google.android.gms.location.Geofence;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WhereAmIFragment extends Fragment {

    public static final boolean createTestGeofences = false;  // Create Test ASSETT geofences

    private ProgressDialog progress;
    private ArrayAdapter<Place> adapter;
    private List<Place> places;
    private Place nearestBuilding;                // Manual building if use is not in any geofences

    private ListView listView;
    private TextView outOfRangeTextView;

    private BroadcastReceiver receiver;
    private List<Geofence> mGeofenceList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_where_am_i, container, false);
        listView = (ListView) view.findViewById(R.id.list_view);
        outOfRangeTextView = (TextView) view.findViewById(R.id.outofrange);
        listView.setEmptyView(outOfRangeTextView);

        places = new ArrayList<Place>();
        adapter = new CustomAdapter(inflater.getContext());
        listView.setAdapter(adapter);

        progress = new ProgressDialog(getActivity());
        progress.setTitle("Please wait");
        progress.setMessage("Loading Buildings...");
        progress.show();

        mGeofenceList = new ArrayList<Geofence>();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                if(extras != null) {
                    int transitionType = extras.getInt("transition");
                    String[] triggerIds = extras.getStringArray("triggers");

                    if(triggerIds != null && triggerIds.length > 0) {
                        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
                            if(progress.isShowing())
                                progress.dismiss();

                            if(nearestBuilding != null){
                                adapter.remove(nearestBuilding);
                                nearestBuilding = null;
                            }

                            for(String trigger : triggerIds) {
                                for(Place p : places) {
                                    if(p.getPlacename().equals(trigger)) {
                                        adapter.add(p);
                                        Log.d("Places", p.getPlacename() + ": " + p.getImage_url());

                                        // Debug toast to see what geofences were entered
                                        Toast.makeText(getActivity(), "Entered: "
                                                + TextUtils.join(", ", triggerIds), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }


                        }
                        else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
                            for(String trigger : triggerIds) {
                                for(Place p : places) {
                                    if(p.getPlacename().equals(trigger))
                                        adapter.remove(p);
                                }
                            }

                            // If the user isn't in any geofences, we calculate the nearest one
                            if(adapter.getCount() == 0) {
                                new GetNearestBuildingsTask().execute();
                            }

                            // Debug toast to see what geofences were exited
                            Toast.makeText(getActivity(), "Exited: "
                                    + TextUtils.join(", ", triggerIds), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver,
                new IntentFilter("geofenceEvent"));

        if(createTestGeofences)
            addTestGeofences();

        new getBuildingsList().execute();

        return view;
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

    public void readBuildingsJSON(JSONArray json) throws JSONException {
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

        JSONObject building;
        Place place;
        for(int i = 0; i < json.length(); i++) {
            building = json.getJSONObject(i);
            addGeofence(building.getString("placename"),
                    Double.valueOf(building.getString("latitude")),
                    Double.valueOf(building.getString("longitude")),
                    100);

            place = new Place();
            place.setPlaceid(building.getInt("placeid"));
            place.setPlacename(building.getString("placename"));
            place.setBuilding_code(building.getString("building_code"));
            place.setImage_url("http://places.colorado.edu" + building.getString("path"));
            places.add(place);
        }

        Activity parent = getActivity();
        if(parent instanceof HomeActivity) {
            ((HomeActivity) parent).addGeofences(mGeofenceList);
        }

        // Start Task to find nearest building
        new GetNearestBuildingsTask().execute();
    }

    public void readNearestBuildingJSON(JSONArray json) throws JSONException {
        // Make sure adapter still doesn't have any geofences
        if(adapter.getCount() == 0) {
            double distance = Double.parseDouble(json.getJSONObject(0).getString("distance"));

            // If building is less than 0.125 miles (~200 meters) from user
            if (distance < 0.125) {
                String buildingName = json.getJSONObject(0).getString("placename");

                // Find building from list of places and add it to the listview adapter
                for (Place p : places) {
                    if (p.getPlacename().equals(buildingName)) {
                        adapter.add(p);
                        nearestBuilding = p;
                        Toast.makeText(getActivity(), "Manually Added: " + buildingName, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if(progress.isShowing())
                progress.dismiss();
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
        return ((HomeActivity) getActivity()).getLocation();
    }

    private class getBuildingsList extends AsyncTask<Void, Void, JSONArray> {
        protected JSONArray doInBackground(Void... voids) {
            try {
                return JsonRequest.getJson("http://places.colorado.edu/api/buildings");
            }
            catch (Exception e) {
                Log.d("JSON", e.toString());
                return null;
            }
        }

        protected void onPostExecute(JSONArray json) {
            if (json != null) {
                try {
                    readBuildingsJSON(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(getActivity(), "Error getting building list", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Manually get nearest building using GPS coorindates and Places API
    class GetNearestBuildingsTask extends AsyncTask<Void, Void, JSONArray> {
        protected JSONArray doInBackground(Void... urls) {
            Location gps;
            int waited = 0;
            gps = getLocation();

            // If the location service is working
            if (gps != null) {
                // While the GPS accuracy is worse than 25 meters and the user has
                // waited less than 20 seconds (40 * 500ms) for a GPS lock
                while ((gps = getLocation()).getAccuracy() > 25 && waited < 40) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    waited++;
                }
            } else {
                return null;
            }

            // If after getting a GPS fix, the user still isn't detected in any geofences
            if (adapter.getCount() == 0)
            {
                try {
                    return JsonRequest.getJson("http://places.colorado.edu/api/nearestbuildings/?latitude=" +
                            gps.getLatitude() + "&longitude=" + gps.getLongitude() + "&limit=3");
                } catch (Exception e) {
                    Log.d("JSON", e.toString());
                    return null;
                }
            }
            // If the user is in a geofence, there's no reason to get the nearest building
            else
            {
                return null;
            }
        }

        protected void onPostExecute(JSONArray json) {
            if(json != null)
            {
                try {
                    readNearestBuildingJSON(json);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
