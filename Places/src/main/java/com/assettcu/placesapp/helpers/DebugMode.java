package com.assettcu.placesapp.helpers;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Created by Aaron on 5/16/2014.
 */
public class DebugMode
{
    public static void makeToast(Activity parent, CharSequence text)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(parent);
        boolean debug = prefs.getBoolean("pref_key_debug_toggle", false);

        if(debug) Toast.makeText(parent, text, Toast.LENGTH_SHORT).show();
    }
}
