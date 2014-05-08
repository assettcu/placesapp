package com.assettcu.placesapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.assettcu.placesapp.R;

/**
 * Created by Aaron on 5/6/2014.
 */
public class NeedAdapter extends BaseAdapter
{
    private Context mContext;

    // references to our images
    public Integer[] mThumbIds =
            {
                    R.drawable.printer,
                    R.drawable.ryan,
                    R.drawable.derek,
                    R.drawable.aaron,
                    R.drawable.trent
            };

    public CharSequence[] mThumbTitles =
            {
                    "A printer",
                    "A Ryan",
                    "A Derek",
                    "An Aaron",
                    "A Trent"
            };

    public NeedAdapter(Context c)
    {
        mContext = c;
    }

    public int getCount()
    {
        return mThumbIds.length;
    }

    public Object getItem(int position)
    {
        return null;
    }

    public long getItemId(int position)
    {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listView;

        if (convertView == null)
        {  // if it's not recycled, initialize some attributes
            listView = new View(mContext);

            // get layout from adapter_list_need
            listView = inflater.inflate(R.layout.adapter_list_need, null);

            TextView textView = (TextView) listView.findViewById(R.id.list_item_label);
            ImageView imageView = (ImageView) listView.findViewById(R.id.list_item_image);

            textView.setText(mThumbTitles[position]);
            imageView.setImageResource(mThumbIds[position]);

        } else
        {
            listView = (View) convertView;
        }

        return listView;
    }


}
