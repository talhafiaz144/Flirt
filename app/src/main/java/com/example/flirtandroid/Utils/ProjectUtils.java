package com.example.flirtandroid.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

public class ProjectUtils {
    public static final String TAG = "ProjectUtility";
    Context context;
    static String appUniqueId;
    private static FirebaseAnalytics mFirebaseAnalytics;
    public ProjectUtils(Context context) {
        this.context = context;
    }

    @SuppressLint("HardwareIds")
    public static String getAppUniqueID(Activity activity) {
        if (activity != null) {
            appUniqueId = Settings.Secure.getString(activity.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Log.d(TAG, "getAppUniqueID: "+appUniqueId);
        }
        return appUniqueId;
    }

    public static void googleAnalyticEvent(Activity activity, String event_name) {
        if (activity != null) {
            if (mFirebaseAnalytics == null) {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
            }
            try {
                Bundle params = new Bundle();
                params.putString("app_unique_id", getAppUniqueID(activity));
                mFirebaseAnalytics.logEvent(event_name, params);
                // Log statement indicating successful event logging
                Log.e("Analytics", "Event logged: " + event_name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
