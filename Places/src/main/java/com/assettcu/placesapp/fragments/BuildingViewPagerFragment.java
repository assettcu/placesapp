package com.assettcu.placesapp.fragments;

/**
 * file: BuildingViewPager
 * by: Derek Baumgartner
 * created: 5/19/2014.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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
    private ViewPagerAdapter pagerAdapter;
    private JsonObject buildingJson;

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
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_building_view_pager, container, false);
        pager = (ViewPager) view.findViewById(R.id.viewPager);
        pagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        pager.setAdapter(pagerAdapter);

        return view;
    }

    // Finish setting up ViewPagerAdapter by setting classrooms and labs to
    // true or false. Then set buildingJson to the newest Json.
    public void setJson(JsonObject json, boolean classrooms, boolean labs) {
        this.buildingJson = json;
        pagerAdapter.setup(classrooms, labs);
        pagerAdapter.notifyDataSetChanged();
    }

    // Returns JsonObject set by BuildingDisplayFragment
    // May return null
    public JsonObject getJson() {
        return this.buildingJson;
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private int pageCount = 1;
        private boolean classrooms = false;
        private boolean labs = false;
        private boolean setup = false;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle (int position) {
            if(position == 0)
                return "Information";
            else if(position == 1 && classrooms)
                return "Classrooms";
            else
                return "Labs";
        }

        @Override
        public Fragment getItem(int position) {

            if(position == 0)
                return BuildingDisplayFragment.newInstance(mPlace);
            else if(position == 1 && classrooms)
                return BuildingRoomsFragment.newInstance(mPlace, true);
            else
                return BuildingRoomsFragment.newInstance(mPlace, false);
        }

        @Override
        public int getCount() {
            return pageCount;
        }

        public void setup(boolean classrooms, boolean labs){
            if(!setup) {
                if (classrooms)
                    pageCount++;
                if (labs)
                    pageCount++;

                this.classrooms = classrooms;
                this.labs = labs;
                this.setup = true;
            }
        }
    }
}
