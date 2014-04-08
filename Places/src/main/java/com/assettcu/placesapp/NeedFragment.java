package com.assettcu.placesapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * file: NeedFragment.java
 * by: Aaron Mertz
 * created: 4/7/14.
 */

public class NeedFragment extends Fragment
{

    private GridView gridView;
    private ArrayAdapter adapter;
    private ArrayList<String> needs;

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

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_need, container, false);
        gridView = (GridView) view.findViewById(R.id.gridView);
        gridView.setAdapter(new ImageAdapter(this.getActivity()));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                if (position != 4)
                {
                    Toast.makeText(getActivity(), "You need " + mThumbTitles[position].toString().toLowerCase() + ".", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getActivity(), "You most definitely do not need " + mThumbTitles[position].toString().toLowerCase() + ".", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    public class ImageAdapter extends BaseAdapter
    {
        private Context mContext;


        public ImageAdapter(Context c)
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

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View gridView;

            if (convertView == null)
            {  // if it's not recycled, initialize some attributes
                gridView = new View(mContext);

                // get layout from grid_item
                gridView = inflater.inflate(R.layout.grid_item, null);

                TextView textView = (TextView) gridView.findViewById(R.id.grid_item_label);
                ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);

                textView.setText(mThumbTitles[position]);
                imageView.setImageResource(mThumbIds[position]);

//                imageView = new ImageView(mContext);
//                imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                imageView.setPadding(8, 8, 8, 8);
            } else
            {
                gridView = (View) convertView;
            }

            return gridView;
        }


    }

}
