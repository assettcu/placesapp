package com.assettcu.placesapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.models.Place;
import com.koushikdutta.ion.Ion;

/**
 * file: WhereAmIListAdapter
 * by: Derek Baumgartner
 * created: 4/16/2014.
 */
public class WhereAmIListAdapter extends ArrayAdapter<Place> {
    public View v;
    public WhereAmIListAdapter(Context context){
        super(context, R.layout.list_view_card);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_card, null);
        }

        Place place = getItem(position);
        TextView name = (TextView) convertView.findViewById(R.id.text_view);
        name.setText(place.getPlaceName());

        ImageView buildingImageView = (ImageView) convertView.findViewById(R.id.icon);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(300);

        Ion.with(buildingImageView)
                .placeholder(null)
                .error(R.drawable.printer)   // Temporary Error drawable
                .animateLoad(animation)
                .animateIn(animation)
                .load(place.getImageURL());

        return convertView;
    }
}
