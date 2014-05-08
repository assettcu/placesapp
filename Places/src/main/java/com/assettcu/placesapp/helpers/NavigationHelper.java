package com.assettcu.placesapp.helpers;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.fragments.MyClassesFragment;
import com.assettcu.placesapp.fragments.NavigateToBuildingFragment;
import com.assettcu.placesapp.fragments.NeedFragment;
import com.assettcu.placesapp.fragments.PlaceholderFragment;
import com.assettcu.placesapp.fragments.WhereAmIFragment;

/**
 * Created by Aaron on 4/5/2014.
 * Models and provides output to the navigation drawer.
 */

public class NavigationHelper
{
    private final static int NUM_SUPPORTED_FRAGMENTS = 5;
    private Fragment fragmentArray[] = new Fragment[10];
    private CharSequence titleArray[] = new CharSequence[10];
    private Integer iconArray[] = new Integer[10];

    public NavigationHelper(Activity parent)
    {
        fragmentArray[0] = new WhereAmIFragment();
        fragmentArray[1] = new NavigateToBuildingFragment();
        fragmentArray[2] = new NeedFragment();
        fragmentArray[3] = new MyClassesFragment();
        fragmentArray[4] = new PlaceholderFragment();

        titleArray[0] = parent.getString(R.string.title_section1);
        titleArray[1] = parent.getString(R.string.title_section2);
        titleArray[2] = parent.getString(R.string.title_section3);
        titleArray[3] = parent.getString(R.string.title_section4);
        titleArray[4] = parent.getString(R.string.title_section5);

        iconArray[0] = android.R.drawable.ic_menu_mylocation;
        iconArray[1] = android.R.drawable.ic_menu_directions;
        iconArray[2] = android.R.drawable.ic_menu_info_details;
        iconArray[3] = android.R.drawable.ic_menu_agenda;
        iconArray[4] = android.R.drawable.ic_notification_clear_all;
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
