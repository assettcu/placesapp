package com.assettcu.placesapp;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by Aaron on 4/5/2014.
 */

public class NavigationHelper
{
    private final static int NUM_SUPPORTED_FRAGMENTS = 3;
    private Fragment fragmentArray[] = new Fragment[10];
    private CharSequence titleArray[] = new CharSequence[10];

    public NavigationHelper(Activity parent)
    {
        fragmentArray[0] = new PlaceholderFragment();
        fragmentArray[1] = new NavigateToBuildingFragment();
        fragmentArray[2] = new NearestBuildingFragment();

        titleArray[0] = parent.getString(R.string.title_section1);
        titleArray[1] = parent.getString(R.string.title_section2);
        titleArray[2] = parent.getString(R.string.title_section3);
        titleArray[3] = parent.getString(R.string.title_section4);
        titleArray[4] = parent.getString(R.string.title_section5);
        titleArray[5] = parent.getString(R.string.title_section6);
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
