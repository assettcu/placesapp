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
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
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

    public static BuildingViewPagerFragment newInstance(Place place)
    {
        BuildingViewPagerFragment fragment = new BuildingViewPagerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLACE, place);
        fragment.setArguments(args);

        Log.d("Assett", "View Pager: new Instance");

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

        Log.d("Assett", "View Pager: onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        if(mPlace == null && getArguments() != null)
        {
            mPlace = (Place) getArguments().getSerializable(ARG_PLACE);
        }
        View view = inflater.inflate(R.layout.fragment_building_view_pager, container, false);
        pager = (ViewPager) view.findViewById(R.id.viewPager);
        pagerAdapter = new ViewPagerAdapter(getFragmentManager());
        pager.setAdapter(pagerAdapter);
        Log.d("Assett", "View Pager: onCreateView arguments: " + (getArguments() != null));

        return view;
    }

    @Override
    public void onStart() {
        super.onPause();
        pagerAdapter.notifyDataSetChanged();
        pager.invalidate();
        Log.d("Assett", "View Pager: onStart");
    }

    @Override
    public void onResume() {
        super.onPause();
        pager.invalidate();
        Log.d("Assett", "View Pager: onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Assett", "View Pager: onPause");
    }

    @Override
    public void onStop() {
        super.onPause();
        pagerAdapter = null;
        Log.d("Assett", "View Pager: onStop");
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

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
            Log.d("Assett", "View Pager: getItem: " + position);
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

        @Override
        public void destroyItem(ViewGroup viewPager, int position, Object object) {
            viewPager.removeView((View) object);
        }
    }
}
