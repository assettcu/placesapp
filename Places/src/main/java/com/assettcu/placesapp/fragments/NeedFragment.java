package com.assettcu.placesapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.adapters.NeedAdapter;

/**
 * file: NeedFragment.java
 * by: Aaron Mertz
 * created: 4/7/14.
 */

public class NeedFragment extends Fragment
{

    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_need, container, false);
        listView = (ListView) view.findViewById(R.id.needListView);
        listView.setAdapter(new NeedAdapter(this.getActivity()));


        return view;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }



}
