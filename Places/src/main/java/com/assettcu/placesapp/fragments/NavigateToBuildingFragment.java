package com.assettcu.placesapp.fragments;

/**
 * file: NavigateToBuildingFragment.java
 * by: Derek Baumgartner
 * created: 3/31/14.
 */

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.models.Place;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class NavigateToBuildingFragment extends ListFragment {

    private ProgressDialog progress;
    ArrayAdapter<String> adapter;
    ArrayList<String> buildings;
    JsonArray json;
    ArrayList<Place> places;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        buildings = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1, buildings);
        places = new ArrayList<Place>();
        setListAdapter(adapter);

        if(json == null) {
            progress = new ProgressDialog(getActivity());
            progress.setTitle("Please wait");
            progress.setMessage("Loading Buildings...");
            progress.show();
            Ion.with(inflater.getContext()).load("http://places.colorado.edu/api/buildings").asJsonArray()
                    .setCallback(new FutureCallback<JsonArray>() {
                        @Override
                        public void onCompleted(Exception e, JsonArray result) {
                            progress.hide();
                            readJSON(result);
                        }
                    });
        }
        else
        {
            readJSON(json);
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id)
    {
        super.onListItemClick(listView, view, position, id);

        Place place = places.get(position);
        BuildingDisplayFragment fragment = BuildingDisplayFragment.newInstance(place);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        fragmentManager.beginTransaction()
                       .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                       .replace(R.id.container, fragment)
                       .addToBackStack(null)
                       .commit();
    }

    public void readJSON(JsonArray json) {
        this.json = json;

        for (int i = 0; i < json.size(); i++) {
                JsonObject jsonObject = json.get(i).getAsJsonObject();
                String buildingCode = jsonObject.get("building_code").getAsString();
                String placeName = jsonObject.get("placename").getAsString();
                int placeId = jsonObject.get("placeid").getAsInt();
                String path = jsonObject.get("path").getAsString();
                String lat = jsonObject.get("latitude").getAsString();
                String lon = jsonObject.get("longitude").getAsString();

                adapter.add(buildingCode + " - " + placeName);

                Place place = new Place();
                place.setPlaceID(placeId);
                place.setPlaceName(placeName);
                place.setBuildingCode(buildingCode);
                place.setImageURL("http://places.colorado.edu" + path);
                place.setLatitude(lat);
                place.setLongitude(lon);
                places.add(place);
        }
    }
}
