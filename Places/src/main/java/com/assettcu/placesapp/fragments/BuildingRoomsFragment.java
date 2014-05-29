package com.assettcu.placesapp.fragments;

/**
 * file: BuildingRoomsFragment
 * by: Derek Baumgartner
 * created: 5/19/2014.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.adapters.RoomsGridViewAdapter;
import com.assettcu.placesapp.models.Place;
import com.assettcu.placesapp.models.Room;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class BuildingRoomsFragment extends Fragment {

    private static final String ARG_PLACE = "place_key";
    private static final String ARG_ROOM = "room_key";

    private GridView gridView;
    private Place place;
    private RoomsGridViewAdapter adapter;
    private boolean roomType = true;       // true = classrooms, false = labs

    private List<Room> rooms;

    public static BuildingRoomsFragment newInstance(Place place, boolean rootType)
    {
        BuildingRoomsFragment fragment = new BuildingRoomsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLACE, place);
        args.putBoolean(ARG_ROOM, rootType);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            place = (Place) getArguments().getSerializable(ARG_PLACE);
            roomType = (Boolean) getArguments().getSerializable(ARG_ROOM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_building_rooms, container, false);
        rooms = new ArrayList<Room>();
        gridView = (GridView) view.findViewById(R.id.gridView1);
        adapter = new RoomsGridViewAdapter(inflater.getContext(), rooms);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, final int position, long id) {
                Log.d("Assett", "id : " + v.getId() + "  value: " + adapter.getItem(position).getRoomName());
                Room room = adapter.getItem(position);

                RoomDisplayFragment fragment = RoomDisplayFragment.newInstance(room);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                fragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        parseJson();
        return view;
    }

    public void parseJson(){
        Fragment parent = getParentFragment();
        if(parent instanceof BuildingViewPagerFragment) {
            JsonObject json = ((BuildingViewPagerFragment) parent).getJson();

            String jsonName = (roomType) ? "classrooms" : "labs";

            if (json.has(jsonName)) {
                JsonArray classroomsJsonArray = json.get(jsonName).getAsJsonArray();
                Room newRoom;
                for (int i = 0; i < classroomsJsonArray.size(); i++) {
                    newRoom = new Room();
                    JsonObject room = classroomsJsonArray.get(i).getAsJsonObject();
                    newRoom.setRoomName(room.get("placename").getAsString().toUpperCase());
                    newRoom.setBuildingCode(place.getBuildingCode());
                    newRoom.setRoomId(room.get("placeid").getAsInt());
                    String roomImageURL = "http://places.colorado.edu/" + getStringFromJson(room, "path");
                    roomImageURL = roomImageURL.replace("/images", "/images/thumbs");
                    newRoom.setRoomImageURL(roomImageURL);
                    adapter.addRoom(newRoom);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    public String getStringFromJson(JsonObject json, String memberName) {
        JsonElement element = json.get(memberName);

        // Element is not null, return the json element value
        if(!element.isJsonNull()){
            return element.getAsString();
        }
        // Element is null, return an empty string
        else {
            return "";
        }
    }
}
