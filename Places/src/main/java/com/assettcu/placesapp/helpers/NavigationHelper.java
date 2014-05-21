package com.assettcu.placesapp.helpers;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.fragments.MyClassesFragment;
import com.assettcu.placesapp.fragments.NavigateToBuildingFragment;
import com.assettcu.placesapp.fragments.NeedFragment;
import com.assettcu.placesapp.fragments.PlaceholderFragment;
import com.assettcu.placesapp.fragments.ReportABugFragment;
import com.assettcu.placesapp.fragments.WhereAmIFragment;

/**
 * Created by Aaron on 4/5/2014.
 * Models and provides output to the navigation drawer.
 */

public class NavigationHelper
{
    private final static int NUM_SUPPORTED_FRAGMENTS = 6;
    private Fragment fragmentArray[] = new Fragment[10];
    private CharSequence titleArray[] = new CharSequence[10];
    private Integer iconArray[] = new Integer[10];

    public NavigationHelper(Activity parent)
    {
        fragmentArray[0] = new WhereAmIFragment();
        fragmentArray[1] = new NavigateToBuildingFragment();
        fragmentArray[2] = new NeedFragment();
        fragmentArray[3] = new MyClassesFragment();
        fragmentArray[4] = new ReportABugFragment();
        fragmentArray[5] = new PlaceholderFragment();

        titleArray[0] = parent.getString(R.string.title_section1);
        titleArray[1] = parent.getString(R.string.title_section2);
        titleArray[2] = parent.getString(R.string.title_section3);
        titleArray[3] = parent.getString(R.string.title_section4);
        titleArray[4] = parent.getString(R.string.title_section5);
        titleArray[5] = parent.getString(R.string.title_section6);

        iconArray[0] = R.drawable.ic_action_location_found;
        iconArray[1] = R.drawable.ic_action_directions;
        iconArray[2] = R.drawable.ic_action_help;
        iconArray[3] = R.drawable.ic_action_event;
        iconArray[4] = R.drawable.ic_action_warning;
        iconArray[5] = R.drawable.ic_action_settings;
    }

    public Fragment getFragmentAtPosition(int i)
    {
        return fragmentArray[i];
    }

    public CharSequence getTitleAtPosition(int i)
    {
        return titleArray[i];
    }

    public int getNumSupportedFragments()
    {
        return NUM_SUPPORTED_FRAGMENTS - 1;
    }

    public Integer getIconAtPosition(int i)
    {
        return iconArray[i];
    }
}
