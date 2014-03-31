package com.assettcu.placesapp;

/**
 * file: NavigateToBuildingFragment.java
 * by: Derek Baumgartner
 * created: 3/31/14.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class NavigateToBuildingFragment extends ListFragment {

    ArrayAdapter<String> adapter;
    ArrayList<String> buildings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        buildings = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1, buildings);
        setListAdapter(adapter);

        Log.d("JSON", "Starting...");

        new RetrieveJSON().execute("http://places.colorado.edu/api/places/?type=building");

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void readJSON(JSONArray json) {
        for (int i = 0; i < json.length(); i++) {
            try {
                adapter.add(json.getJSONObject(i).getString("placename"));
                Log.d("JSON", json.getJSONObject(i).getString("placename"));
            } catch (JSONException e) {
                Log.d("JSON", e.toString());
            }
        }
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
