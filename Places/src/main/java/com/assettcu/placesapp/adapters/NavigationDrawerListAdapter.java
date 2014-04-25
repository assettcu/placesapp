package com.assettcu.placesapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

    private Integer[] iconArray =
            {
                    // Icon placeholders until we find better ones
                    android.R.drawable.ic_menu_mylocation,
                    android.R.drawable.ic_menu_directions,
                    android.R.drawable.ic_menu_info_details,
                    android.R.drawable.ic_menu_agenda

               // R.drawable.home_icon_3,
               // R.drawable.navigate_to_building_icon,
               // R.drawable.need_icon,
               // R.drawable.my_classes_icon_2
            };

    public NavigationDrawerListAdapter(Context c)
    {
        mContext = c;
        navHelper = new NavigationHelper((Activity) c);
    }

    public int getCount()
    {
        return iconArray.length;
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
        {  // if it's not recycled, initialize some attributes

            navListView = new View(mContext);

            // get layout from drawer_item
            navListView = inflater.inflate(R.layout.drawer_item, null);

            TextView textView = (TextView) navListView.findViewById(R.id.drawer_item_label);
            ImageView imageView = (ImageView) navListView.findViewById(R.id.drawer_item_image);

            textView.setText(navHelper.getTitleAtPosition(position));
            imageView.setImageResource(iconArray[position]);

        } else
        {
            navListView = (View) convertView;
        }

        return navListView;
    }

}