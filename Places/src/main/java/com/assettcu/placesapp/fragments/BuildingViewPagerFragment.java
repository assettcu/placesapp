package com.assettcu.placesapp.fragments;

/**
 * file: BuildingViewPager
 * by: Derek Baumgartner
 * created: 5/19/2014.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.models.Place;
import com.google.gson.JsonObject;

public class BuildingViewPagerFragment extends Fragment {

    private static final String ARG_PLACE = "place_key";

    private ViewPager pager;
    private Place mPlace;
    private JsonObject buildingJson;
    private ViewPagerAdapter pagerAdapter;

    public static BuildingViewPagerFragment newInstance(Place place)
    {
        BuildingViewPagerFragment fragment = new BuildingViewPagerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLACE, place);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            mPlace = (Place) getArguments().getSerializable(ARG_PLACE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_building_view_pager, container, false);
        pager = (ViewPager) view.findViewById(R.id.viewPager);
        pagerAdapter = new ViewPagerAdapter(getFragmentManager());
        pager.setAdapter(pagerAdapter);

        return view;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle (int position) {
            switch (position) {
                case 0: return "Information";
                case 1: return "Rooms";
                default: return "";
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return BuildingDisplayFragment.newInstance(mPlace);
                case 1: return BuildingRoomsFragment.newInstance(mPlace);
                default: return PlaceholderFragment.newInstance(0);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
