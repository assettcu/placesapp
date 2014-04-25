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
import android.widget.TextView;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.adapters.BuildingDisplayListAdapter;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

/**
 * Takes in information from the Place that is selected in the
 * NavigateToBuildingFragment and displays it.
 */
public class BuildingDisplayFragment extends Fragment
{
    private static final String ARG_BUILDING_NAME = "building_name";
    private static final String ARG_BUILDING_URL = "building_url";
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";

    private String mBuildingName;
    private String mBuildingURL;
    private String mLatitude;
    private String mLongitude;

    private OnFragmentInteractionListener mListener;
    private BuildingDisplayListAdapter buildingInfo;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param building_name Name of the building.
     * @param building_url  URL of the image for the building.
     * @param lat Latitude of the building.
     * @param lon Longitude of the building.
     * @return A new instance of fragment BuildingDisplayFragment.
     */
    public static BuildingDisplayFragment newInstance(String building_name, String building_url, String lat, String lon)
    {
        BuildingDisplayFragment fragment = new BuildingDisplayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BUILDING_NAME, building_name);
        args.putString(ARG_BUILDING_URL, building_url);
        args.putString(ARG_LATITUDE, lat);
        args.putString(ARG_LONGITUDE, lon);
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
            mBuildingName = getArguments().getString(ARG_BUILDING_NAME);
            mBuildingURL = getArguments().getString(ARG_BUILDING_URL);
            mLatitude = getArguments().getString(ARG_LATITUDE);
            mLongitude = getArguments().getString(ARG_LONGITUDE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_building_display, container, false);

        ImageView buildingImage = (ImageView) view.findViewById(R.id.building_image);
        TextView buildingText = (TextView) view.findViewById(R.id.building_name);

        UrlImageViewHelper.setUrlDrawable(buildingImage, mBuildingURL, null, new UrlImageViewCallback()
        {
            @Override
            public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache)
            {
                if (!loadedFromCache)
                {
                    Animation animation = new AlphaAnimation(0.0f, 1.0f);
                    animation.setDuration(300);
                    imageView.startAnimation(animation);
                }

                if(loadedBitmap == null)
                {
                    imageView.setImageResource(R.drawable.unable_to_load_icon);
                }
            }

        });

        buildingText.setText(mBuildingName);

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
            mListener.onFragmentInteraction(mLatitude, mLongitude);
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
