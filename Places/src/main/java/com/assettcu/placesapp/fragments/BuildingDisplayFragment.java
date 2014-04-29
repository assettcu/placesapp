package com.assettcu.placesapp.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.adapters.BuildingDisplayListAdapter;
import com.assettcu.placesapp.models.Place;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

/**
 * Takes in information from the Place that is selected in the
 * NavigateToBuildingFragment and displays it.
 */
public class BuildingDisplayFragment extends Fragment
{
    private static final String ARG_PLACE = "place_key";

    private Place mPlace;

    private OnFragmentInteractionListener mListener;
    private BuildingDisplayListAdapter buildingInfo;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param place The Place to display.
     * @return A new instance of fragment BuildingDisplayFragment.
     */
    public static BuildingDisplayFragment newInstance(Place place)
    {
        BuildingDisplayFragment fragment = new BuildingDisplayFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLACE, place);
        fragment.setArguments(args);

        return fragment;
    }

    public BuildingDisplayFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
           mPlace = (Place) getArguments().getSerializable(ARG_PLACE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_building_display, container, false);

        final ImageView buildingImage = (ImageView) view.findViewById(R.id.building_image);
        TextView buildingText = (TextView) view.findViewById(R.id.building_name);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(300);

        // Load thumbnails instead of full images
        String mBuildingURL = mPlace.getImageURL();
        mBuildingURL = mBuildingURL.replace("/images", "/images/thumbs");

        Ion.with(inflater.getContext(), mBuildingURL)
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
                .setCallback(new FutureCallback<ImageView>()
                {
                    @Override
                    public void onCompleted(Exception e, ImageView result)
                    {
                        progressBar.setVisibility(View.GONE);
                        buildingImage.setVisibility(View.VISIBLE);
                    }
                });

        buildingText.setText(mPlace.getPlaceName());

        ImageButton button = (ImageButton) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                navigate();
            }
        });

        buildingInfo = new BuildingDisplayListAdapter(getActivity());

        buildingInfo.addDataToGroup(0, new String[] {"Test1", "Test2"}, "Information");
        buildingInfo.addDataToGroup(1, new String[] {"Test1", "Test2"}, "Printers");
        buildingInfo.addDataToGroup(2, new String[] {"Test1", "Test2"}, "Classrooms");

        ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        expandableListView.setAdapter(buildingInfo);

        return view;
    }

    public void navigate()
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(mPlace.getLatitude(), mPlace.getLongitude());
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener
    {
        public void onFragmentInteraction(String lat, String lon);
    }

}
