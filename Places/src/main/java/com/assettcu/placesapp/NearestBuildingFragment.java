package com.assettcu.placesapp;

/**
 * Created by: Derek Baumgartner
 * Created on: 3/26/14
 */

import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.json.JSONArray;

public class NearestBuildingFragment extends Fragment {

    private ProgressDialog progress;
    private TextView textView;
    private Location location;
    private JSONArray json;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearestbuilding, container, false);
        textView = (TextView) view.findViewById(R.id.section_label);
        getNearestBuildings();
        return view;
    }

    public void getNearestBuildings()
    {
        progress = new ProgressDialog(getActivity());
        progress.setTitle("Please wait");
        progress.setMessage("Finding nearest buildings...");
        progress.show();

        location = ((Home) getActivity()).getLocation();
        if (location == null) {
            textView.setText("Location is unavailable");
        }
        else {
            textView.setText(location.getLatitude() + " " + location.getLongitude() + " " + location.getAccuracy());
            new RetrieveJSON().execute("http://places.colorado.edu/api/nearestbuildings/?latitude=" +
                        location.getLatitude() + "&longitude=" + location.getLongitude() + "&limit=3");
        }
    }

    public void readJSON(JSONArray json) {
        this.json = json;
        textView.setText(json.toString());
        progress.dismiss();
    }

    class RetrieveJSON extends AsyncTask<String, Void, JSONArray> {
        protected JSONArray doInBackground(String... urls) {
            try {
                return JsonRequest.getJson(urls[0]);
            } catch (Exception e) {
                Log.d("JSON", e.toString());
                return null;
            }
        }

        protected void onPostExecute(JSONArray json) {
            readJSON(json);
        }
    }
}
