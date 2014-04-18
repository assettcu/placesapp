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
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

/**
 * file: CustomAdapter
 * by: Derek Baumgartner
 * created: 4/16/2014.
 */
public class CustomAdapter extends ArrayAdapter<Place> {
    public View v;
    public CustomAdapter(Context context){
        super(context, R.layout.list_view_card);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_card, null);
        }

        Place place = getItem(position);
        TextView name = (TextView) convertView.findViewById(R.id.text_view);
        name.setText(place.getPlacename());

        ImageView placeImage = (ImageView) convertView.findViewById(R.id.icon);
        UrlImageViewHelper.setUrlDrawable(placeImage, place.getImage_url(), null, new UrlImageViewCallback() {
            @Override
            public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                if (!loadedFromCache) {
                    Animation animation = new AlphaAnimation(0.0f, 1.0f);
                    animation.setDuration(300);
                    imageView.startAnimation(animation);
                }
            }
        });

        return convertView;
    }
}
