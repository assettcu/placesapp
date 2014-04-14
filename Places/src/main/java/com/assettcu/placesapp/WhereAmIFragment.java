/**
 * file: WhereAmIFragment
 * by: Derek Baumgartner
 * created: 4/14/2014.
 */

package com.assettcu.placesapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WhereAmIFragment extends ListFragment {

    private ProgressDialog progress;
    private ArrayAdapter<String> adapter;
    private List<String> buildings;

    private BroadcastReceiver receiver;
    private Geofence geofence;
    private List<Geofence> mGeofenceList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        buildings = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1, buildings);
        setListAdapter(adapter);

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
                            for(String trigger : triggerIds) {
                                adapter.add(trigger);
                            }

                            // Debug toast to see what geofences were entered
                            Toast.makeText(getActivity(), "Entered: "
                                    + TextUtils.join(", ", triggerIds), Toast.LENGTH_SHORT).show();
                        }
                        else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
                            for(String trigger : triggerIds) {
                                adapter.remove(trigger);
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

        // Test Geofences located near ASSETT
        addGeofence("Assett E295",         40.013847, -105.250449, 10);
        addGeofence("Assett Second Floor", 40.013803, -105.250443, 30);
        addGeofence("Assett Bus Stop",     40.013708, -105.250771, 15);

        new getBuildingsList().execute();

        return super.onCreateView(inflater, container, savedInstanceState);
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

    public void readJSON(JSONArray json) throws JSONException {
        JSONObject building;
        for(int i = 0; i < json.length(); i++) {
            building = json.getJSONObject(i);
            addGeofence(building.getString("placename"),
                    Double.valueOf(building.getString("latitude")),
                    Double.valueOf(building.getString("longitude")),
                    100);
        }

        Activity parent = getActivity();
        if(parent instanceof HomeActivity) {
            ((HomeActivity) parent).addGeofences(mGeofenceList);
        }
    }

    public void addGeofence(String id, double latitude, double longitude, float radius){
        geofence = new Geofence.Builder()
                .setRequestId(id)              // Set name of building
                .setTransitionTypes(1 | 2)     // Entered and Exited transitions
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(600000) // 10 minute expiration time
                .build();
        mGeofenceList.add(geofence);
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
                    readJSON(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(getActivity(), "Error getting building list", Toast.LENGTH_SHORT).show();
            }
            progress.dismiss();
        }
    }
}
