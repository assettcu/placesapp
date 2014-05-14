package com.assettcu.placesapp.fragments;

/**
 * file: NavigateToBuildingFragment.java
 * by: Derek Baumgartner
 * created: 3/31/14.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.assettcu.placesapp.HomeActivity;
import com.assettcu.placesapp.R;
import com.assettcu.placesapp.models.Place;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class NavigateToBuildingFragment extends ListFragment {

    private ProgressDialog progress;
    ArrayAdapter<Spannable> adapter;
    ArrayList<Spannable> buildings;
    JsonArray buildingsJsonArray;
    ArrayList<Place> places;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        buildings = new ArrayList<Spannable>();
        adapter = new ArrayAdapter<Spannable>(getActivity(), android.R.layout.simple_list_item_1, buildings);
        places = new ArrayList<Place>();
        setListAdapter(adapter);

        Activity parent = getActivity();
        if(parent instanceof HomeActivity) {
            buildingsJsonArray = ((HomeActivity) parent).getBuildingsJsonArray();
            Log.d("Assett", "Got BuildingsArray. null = " + (buildingsJsonArray == null));
        }

        progress = new ProgressDialog(getActivity());

        if(buildingsJsonArray == null) {
            progress.setTitle("Please wait");
            progress.setMessage("Loading Buildings...");
            progress.show();
            Ion.with(getActivity()).load("http://places.colorado.edu/api/buildings").asJsonArray()
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
            readJSON(buildingsJsonArray);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.setBackgroundColor(getResources().getColor(android.R.color.white));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        progress.dismiss();
        super.onDestroyView();
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
        this.buildingsJsonArray = json;

        Activity parent = getActivity();
        if(parent instanceof HomeActivity) {
            ((HomeActivity) parent).setBuildingsJsonArray(json);
            Log.d("Assett", "Set BuildingsJsonArray");
        }

        for (int i = 0; i < json.size(); i++) {
                JsonObject jsonObject = json.get(i).getAsJsonObject();
                String buildingCode = jsonObject.get("building_code").getAsString();
                String placeName = jsonObject.get("placename").getAsString();
                int placeId = jsonObject.get("placeid").getAsInt();
                String path = jsonObject.get("path").getAsString();
                String lat = jsonObject.get("latitude").getAsString();
                String lon = jsonObject.get("longitude").getAsString();

                adapter.add(new SpannableString(Html.fromHtml(
                        "<strong>" + buildingCode + "</strong> - " + placeName)));

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
