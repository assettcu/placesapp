package com.assettcu.placesapp;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by Aaron on 4/5/2014.
 */

public class NavigationHelper
{
    private final static int NUM_SUPPORTED_FRAGMENTS = 3;
    private Fragment fragmentArray[] = new Fragment[NUM_SUPPORTED_FRAGMENTS];
    private CharSequence titleArray[] = new CharSequence[NUM_SUPPORTED_FRAGMENTS];

    public NavigationHelper(Activity parent)
    {
        fragmentArray[0] = new PlaceholderFragment();
        fragmentArray[1] = new NavigateToBuildingFragment();
        fragmentArray[2] = new NearestBuildingFragment();

        titleArray[0] = parent.getString(R.string.title_section1);
        titleArray[1] = parent.getString(R.string.title_section2);
        titleArray[2] = parent.getString(R.string.title_section3);
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
}
