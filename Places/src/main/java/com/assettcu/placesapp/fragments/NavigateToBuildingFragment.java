package com.assettcu.placesapp.fragments;

/**
 * file: NavigateToBuildingFragment.java
 * by: Derek Baumgartner
 * created: 3/31/14.
 */

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.helpers.JsonRequest;
import com.assettcu.placesapp.models.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NavigateToBuildingFragment extends ListFragment {

    private ProgressDialog progress;
    ArrayAdapter<String> adapter;
    ArrayList<String> buildings;
    JSONArray json;
    ArrayList<Place> places;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        buildings = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1, buildings);
        places = new ArrayList<Place>();
        setListAdapter(adapter);

        progress = new ProgressDialog(getActivity());
        progress.setTitle("Please wait");
        progress.setMessage("Loading Buildings...");
        progress.show();

        new RetrieveJSON().execute("http://places.colorado.edu/api/buildings");

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id)
    {
        super.onListItemClick(listView, view, position, id);

        Place place = places.get(position);
        BuildingDisplayFragment fragment = BuildingDisplayFragment.newInstance(place.getPlacename(), place.getImage_url(),
                                                                               place.getLatitude(), place.getLongitude());

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        fragmentManager.beginTransaction()
                       .replace(R.id.container, fragment)
                       .addToBackStack(null)
                       .commit();

    }

    public void readJSON(JSONArray json) {
        this.json = json;

        for (int i = 0; i < json.length(); i++) {
            try {
                JSONObject jsonObject = json.getJSONObject(i);
                String buildingCode = jsonObject.getString("building_code");
                String placeName = jsonObject.getString("placename");
                int placeId = jsonObject.getInt("placeid");
                String path = jsonObject.getString("path");
                String lat = jsonObject.getString("latitude");
                String lon = jsonObject.getString("longitude");

                adapter.add(buildingCode + " - " + placeName);

                Place place = new Place();
                place.setPlaceid(placeId);
                place.setPlacename(placeName);
                place.setBuilding_code(buildingCode);
                place.setImage_url("http://places.colorado.edu" + path);
                place.setLatitude(lat);
                place.setLongitude(lon);
                places.add(place);

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
