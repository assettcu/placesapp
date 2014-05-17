package com.assettcu.placesapp.activities;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.fragments.SettingsFragment;

public class SettingsActivity extends PreferenceActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
        {
            addPreferencesFromResource(R.xml.preferences);
        }
        else
        {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getFragmentManager().beginTransaction()
                                .replace(android.R.id.content, new SettingsFragment())
                                .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}