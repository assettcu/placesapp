package com.assettcu.placesapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.helpers.NavigationHelper;

/**
 * Created by Aaron on 4/21/2014.
 * Defines the icons for each list and the text for each drawer item.
 */

public class NavigationDrawerListAdapter extends BaseAdapter
{
    private Context mContext;
    private NavigationHelper navHelper;

    public NavigationDrawerListAdapter(Context c)
    {
        mContext = c;
        navHelper = new NavigationHelper((Activity) c);
    }

    public int getCount()
    {
        return navHelper.getNumSupportedFragments() + 1;
    }

    public Object getItem(int position)
    {
        return null;
    }

    public long getItemId(int position)
    {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View navListView;

        if (convertView == null)
        {
            // get layout from drawer_item
            navListView = inflater.inflate(R.layout.drawer_item, parent, false);

            TextView textView = (TextView) navListView.findViewById(R.id.drawer_item_label);

            textView.setText(navHelper.getTitleAtPosition(position));
            textView.setCompoundDrawablesWithIntrinsicBounds(navHelper.getIconAtPosition(position), 0, 0, 0);
        }
        else
        {
            navListView = convertView;
        }

        return navListView;
    }

}