package com.assettcu.placesapp;

/**
 * file: NavigateToBuildingFragment.java
 * by: Derek Baumgartner
 * created: 3/31/14.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class NavigateToBuildingFragment extends ListFragment {

    private ProgressDialog progress;
    ArrayAdapter<String> adapter;
    ArrayList<String> buildings;
    JSONArray json;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        buildings = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1, buildings);
        setListAdapter(adapter);

        progress = new ProgressDialog(getActivity());
        progress.setTitle("Please wait");
        progress.setMessage("Loading Buildings...");
        progress.show();

        new RetrieveJSON().execute("http://places.colorado.edu/api/buildings");

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        try {
            double latitude = json.getJSONObject(position).getDouble("latitude");
            double longitude = json.getJSONObject(position).getDouble("longitude");
            Toast.makeText(getActivity(), latitude + "," + longitude, Toast.LENGTH_LONG).show();
            //Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + latitude + "," + longitude));
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(
                    "http://maps.google.com/maps?daddr=" + latitude + "," + longitude + "&dirflg=w"));
            startActivity(i);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(getActivity(), "building :" + buildings.get(position),
                Toast.LENGTH_LONG).show();
    }

    public void readJSON(JSONArray json) {
        this.json = json;

        for (int i = 0; i < json.length(); i++) {
            try {
                adapter.add(json.getJSONObject(i).getString("building_code") + " - " +
                        json.getJSONObject(i).getString("placename"));
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
            if(json != null) {
                readJSON(json);
            }
            progress.dismiss();
        }
    }
}
