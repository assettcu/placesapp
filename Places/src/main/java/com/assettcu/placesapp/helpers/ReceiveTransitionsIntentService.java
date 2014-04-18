package com.assettcu.placesapp.helpers;

/**
 * file: ReceiveTransitionsIntentService
 * by: Derek Baumgartner
 * created: 4/11/2014.
 */

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.Geofence;

import java.util.List;
import android.os.Vibrator;

public class ReceiveTransitionsIntentService extends IntentService {

    public ReceiveTransitionsIntentService() {
        super("ReceiveTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (LocationClient.hasError(intent)) {
            int errorCode = LocationClient.getErrorCode(intent);
            Log.e("ReceiveTransitionsIntentService",
                    "Location Services error: " + Integer.toString(errorCode));
        }
        else {
            int transitionType = LocationClient.getGeofenceTransition(intent);
            List<Geofence> geofenceList = LocationClient.getTriggeringGeofences(intent);
            String[] triggerIds = new String[geofenceList.size()];

            for (int i = 0; i < triggerIds.length; i++) {
                triggerIds[i] = geofenceList.get(i).getRequestId();
            }

            Intent broadcast = new Intent("geofenceEvent");
            Bundle extras = new Bundle();
            extras.putInt("transition", transitionType);
            extras.putStringArray("triggers", triggerIds);
            broadcast.putExtras(extras);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);

            Vibrator v = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(80);
        }
    }
}
