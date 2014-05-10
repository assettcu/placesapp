package com.assettcu.placesapp.fragments;

/**
 * file: RoomDisplayFragment
 * by: Derek Baumgartner
 * created: 5/9/2014.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.assettcu.placesapp.R;

public class RoomDisplayFragment extends Fragment {

    private String roomName;
    private int roomId;

    public RoomDisplayFragment()
    {

    }

    public static RoomDisplayFragment newInstance(String roomName, int roomId)
    {
        RoomDisplayFragment fragment = new RoomDisplayFragment();
        Bundle args = new Bundle();
        args.putString("roomName", roomName);
        args.putInt("roomId", roomId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            roomName = getArguments().getString("roomName");
            roomId = getArguments().getInt("roomId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText("Room Name: " + roomName + " - Room ID: " + roomId);
        return rootView;
    }
}
