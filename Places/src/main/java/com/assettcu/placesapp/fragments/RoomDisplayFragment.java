package com.assettcu.placesapp.fragments;

/**
 * file: RoomDisplayFragment
 * by: Derek Baumgartner
 * created: 5/9/2014.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.models.Room;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

public class RoomDisplayFragment extends Fragment {

    private static final String ARG_ROOM = "room_key";

    private Room room;

    public RoomDisplayFragment()
    {

    }

    public static RoomDisplayFragment newInstance(Room room)
    {
        RoomDisplayFragment fragment = new RoomDisplayFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ROOM, room);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            room = (Room) getArguments().getSerializable(ARG_ROOM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_room_display, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.room_name);
        textView.setText(room.getRoomName());

        final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        final ImageView buildingImage = (ImageView) rootView.findViewById(R.id.building_image);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(300);

        // If we have a URL for the room image, load it into our imageview
        if(room.getRoomName() != null) {

            Ion.with(inflater.getContext(), room.getRoomImageURL())
                    .progressBar(progressBar)
                    .progress(new ProgressCallback() {
                        @Override
                        public void onProgress(int downloaded, int total) {
                            progressBar.setProgress(downloaded * 100 / total);
                            System.out.println("" + downloaded + " / " + total);
                        }
                    })
                    .withBitmap()
                    .placeholder(null)
                    .error(R.drawable.printer)   // Temporary Error drawable
                    .animateLoad(animation)
                    .animateIn(animation)
                    .intoImageView(buildingImage)
                    .setCallback(new FutureCallback<ImageView>() {
                        @Override
                        public void onCompleted(Exception e, ImageView result) {
                            progressBar.setVisibility(View.GONE);
                            buildingImage.setVisibility(View.VISIBLE);
                        }
                    });
        }
        // Labs don't have images right now. So we'll use the printer as a placeholder
        else {
            progressBar.setVisibility(View.GONE);
            buildingImage.setVisibility(View.VISIBLE);
            buildingImage.setImageDrawable(getResources().getDrawable(R.drawable.printer));
        }

        return rootView;
    }
}
