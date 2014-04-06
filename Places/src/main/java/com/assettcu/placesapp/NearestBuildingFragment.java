package com.assettcu.placesapp;

/**
 * Created by: Derek Baumgartner
 * Created on: 3/26/14
 */

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;

public class NearestBuildingFragment extends Fragment {

    private ProgressDialog progress;
    private TextView textView;
    private ImageView imageView;
    private Location location;
    private JSONArray json;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearestbuilding, container, false);
        textView = (TextView) view.findViewById(R.id.section_label);
        imageView = (ImageView) view.findViewById(R.id.buildingImage);
        progress = new ProgressDialog(getActivity());
        progress.setTitle("Waiting for GPS Lock");
        progress.show();
        new GetNearestBuildingsTask().execute();
        return view;
    }

    public Location getLocation() {
        return ((HomeActivity) getActivity()).getLocation();
    }

    public void readJSON(JSONArray json) throws JSONException {
        this.json = json;

        // Change image URL to give us the thumbnail URL and get it
        String path = json.getJSONObject(0).getString("path").replace("/images", "/images/thumbs");
        new DownloadImageTask(imageView).execute("http://places.colorado.edu" + path);

        String buildingCode = json.getJSONObject(0).getString("building_code");
        String buildingName = json.getJSONObject(0).getString("placename");
        textView.setText("Nearest Building: " + buildingCode + " - " + buildingName);
        progress.dismiss();
    }

    class GetNearestBuildingsTask extends AsyncTask<Void, Integer, JSONArray> {
        protected JSONArray doInBackground(Void... urls) {
            Location gps;
            int waited = 0;

            gps = getLocation();

            // If the location service is working
            if(gps != null){
                // While the GPS accuracy is worse than 25 meters and the user has
                // waited less than 20 seconds for a GPS lock
                while((gps = getLocation()).getAccuracy() > 25 && waited < 100) {
                    publishProgress((int)gps.getAccuracy());
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    waited++;
                }
            }
            else {
                return null;
            }
            publishProgress(-1);

            try {
                return JsonRequest.getJson("http://places.colorado.edu/api/nearestbuildings/?latitude=" +
                        gps.getLatitude() + "&longitude=" + gps.getLongitude() + "&limit=3");
            } catch (Exception e) {
                Log.d("JSON", e.toString());
                return null;
            }
        }

        protected void onProgressUpdate(Integer... accuracy) {
            if(accuracy[0] == -1) {
                progress.setTitle("Please wait");
                progress.setMessage("Getting nearest buildings...");
            }
            else {
                progress.setMessage("Current accuracy: " + accuracy[0] + " meters");
            }
        }

        protected void onPostExecute(JSONArray json) {
            if(json != null)
            {
                try {
                    readJSON(json);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                textView.setText("Location is unavailable");
                progress.dismiss();
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView buildingImage;

        public DownloadImageTask(ImageView buildingImage) {
            this.buildingImage = buildingImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            buildingImage.setImageBitmap(result);
        }
    }
}
