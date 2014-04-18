package com.assettcu.placesapp.helpers;

/**
 * Created by: Derek Baumgartner
 * Created on: 3/31/14
 */

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonRequest {

    public static JSONArray getJson(String url){

        InputStream is = null;
        String result = "";
        JSONArray jsonArray = null;

        // HTTP
        try {
            HttpClient httpclient = new DefaultHttpClient(); // for port 80 requests!
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch(Exception e) {
            Log.d("JSON", e.toString());
        }

        // Read response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch(Exception e) {
            Log.d("JSON", e.toString());
        }

        // Convert string to object
        try {
            jsonArray = new JSONArray(result);
        } catch(JSONException e) {
            Log.d("JSON", e.toString());
        }

        return jsonArray;

    }
}
