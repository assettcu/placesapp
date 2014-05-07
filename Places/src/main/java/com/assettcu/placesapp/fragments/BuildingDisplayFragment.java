package com.assettcu.placesapp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.adapters.BuildingDisplayListAdapter;
import com.assettcu.placesapp.models.Place;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private ExpandableListView expandableListView;

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

        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        expandableListView.addHeaderView(inflater.inflate(R.layout.image_holder, null, false));
        TextView buildingText = (TextView) view.findViewById(R.id.building_name);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
        final ImageView buildingImage = (ImageView) view.findViewById(R.id.building_image);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(300);

        // Load thumbnails instead of full images
        String mBuildingURL = mPlace.getImageURL();
        mBuildingURL = mBuildingURL.replace("/images", "/images/thumbs").replace(" ", "%20");

        // Get Building Image
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

        // Get Building Metadata
        Ion.with(inflater.getContext()).load("http://places.colorado.edu/api/place/?id=" + mPlace.getPlaceID()).asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        readMetadataJson(result);
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

        buildingInfo.setGroupData(0, new String[]{"None"}, "Information");
        buildingInfo.setGroupData(1, new String[]{"None"}, "Printers");

        expandableListView.setAdapter(buildingInfo);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {
                Log.d("assett", buildingInfo.getChild(groupPosition, childPosition));
                return true;
            }
        });

        return view;
    }

    public void readMetadataJson(JsonObject json) {
        if (json.has("information")) {
            JsonObject information = json.get("information").getAsJsonObject();

            List<String> tempInfo = new ArrayList<String>();
            JsonElement element;
            if (!(element = information.get("building_code")).isJsonNull())
                tempInfo.add("<b>Building Code:</b> " + element.getAsString());
            if (!(element = information.get("building_proctor")).isJsonNull())
                tempInfo.add("<b>Proctor:</b> " + element.getAsString());
            if (!(element = information.get("hours_open")).isJsonNull())
                tempInfo.add("<b>Hours Open:</b> " + element.getAsString());
            if (!(element = information.get("number_of_doors")).isJsonNull())
                tempInfo.add("<b>Number of Doors:</b> " + element.getAsString());

            //Add information data
            buildingInfo.setGroupData(0, tempInfo.toArray(new String[tempInfo.size()]), "Information");

            //Add printer data
            JsonElement printerElement = information.get("printers");
            if (!printerElement.isJsonNull()) {
                String data = printerElement.getAsString();
                String[] array;

                if (data.contains("<br/>\r\n")) array = data.split("<br/>\r\n");
                else array = data.split(",");

                buildingInfo.setGroupData(1, array, "Printers");
            }
        }

        // Add classroom data
        if (json.has("classrooms")) {
            JsonArray classroomsJsonArray = json.get("classrooms").getAsJsonArray();
            String[] classrooms = new String[classroomsJsonArray.size()];
            for (int i = 0; i < classroomsJsonArray.size(); i++) {
                String room = classroomsJsonArray.get(i).getAsJsonObject().get("placename").getAsString();
                classrooms[i] = room.toUpperCase();
            }
            buildingInfo.setGroupData(2, classrooms, "Classrooms (" + classrooms.length + ")");
        }

        // Add labs data
        if (json.has("labs")) {
            JsonArray classroomsJsonArray = json.get("labs").getAsJsonArray();
            String[] labs = new String[classroomsJsonArray.size()];
            for (int i = 0; i < classroomsJsonArray.size(); i++) {
                String room = classroomsJsonArray.get(i).getAsJsonObject().get("placename").getAsString();
                labs[i] = room.toUpperCase();
            }
            buildingInfo.setGroupData(3, labs, "Labs (" + labs.length + ")");
        }

        buildingInfo.notifyDataSetChanged();
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
