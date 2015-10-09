/*
According to apache license

This is fork of christocracy cordova-plugin-background-geolocation plugin
https://github.com/christocracy/cordova-plugin-background-geolocation

Differences to original version:

1. To avoid conflicts
package com.tenforwardconsulting.cordova.bgloc
was renamed to com.marianhello.cordova.bgloc

2. new methods isLocationEnabled, mMessageReciever, handleMessage
*/

package com.marianhello.cordova.bgloc;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.content.BroadcastReceiver;

import android.os.Build;
import android.text.TextUtils;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.location.LocationManager;
import android.util.Log;

import com.marianhello.cordova.bgloc.data.Location;

public class BackgroundGpsPlugin extends CordovaPlugin {
    private static final String TAG = "BackgroundGpsPlugin";

    public static final String ACTION_START = "start";
    public static final String ACTION_STOP = "stop";
    public static final String ACTION_CONFIGURE = "configure";
    public static final String ACTION_SET_CONFIG = "setConfig";
    public static final String ACTION_LOCATION_ENABLED_CHECK = "isLocationEnabled";

    private Intent updateServiceIntent;

    private Boolean isEnabled = false;

    private String url;
    private String params;
    private String headers;
    private String stationaryRadius = "30";
    private String desiredAccuracy = "100";
    private String distanceFilter = "30";
    private String locationTimeout = "60";
    private String isDebugging = "false";
    private String notificationIconColor  = "#4CAF50";
    private String notificationIcon  = "notification_icon";
    private String notificationTitle = "Background tracking";
    private String notificationText = "ENABLED";
    private String stopOnTerminate = "false";
    private CallbackContext callbackContext;

    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) {
        Activity activity = this.cordova.getActivity();
        Context context = activity.getApplicationContext();
        Boolean result = false;
        updateServiceIntent = new Intent(activity, LocationUpdateService.class);

        if (ACTION_START.equalsIgnoreCase(action) && !isEnabled) {
            result = true;
            if (params == null || headers == null) {
                callbackContext.error("Call configure before calling start");
            } else {
                IntentFilter intentFilter = new IntentFilter(Constant.FILTER);
                context.registerReceiver(mMessageReceiver, intentFilter);

                updateServiceIntent.putExtra("url", url);
                updateServiceIntent.putExtra("params", params);
                updateServiceIntent.putExtra("headers", headers);
                updateServiceIntent.putExtra("stationaryRadius", stationaryRadius);
                updateServiceIntent.putExtra("desiredAccuracy", desiredAccuracy);
                updateServiceIntent.putExtra("distanceFilter", distanceFilter);
                updateServiceIntent.putExtra("locationTimeout", locationTimeout);
                updateServiceIntent.putExtra("isDebugging", isDebugging);
                updateServiceIntent.putExtra("notificationIcon", notificationIcon);
                updateServiceIntent.putExtra("notificationTitle", notificationTitle);
                updateServiceIntent.putExtra("notificationText", notificationText);
                updateServiceIntent.putExtra("notificationIconColor", notificationIconColor);
                updateServiceIntent.putExtra("stopOnTerminate", stopOnTerminate);
                updateServiceIntent.putExtra("activity", cordova.getActivity().getClass().getCanonicalName());
                Log.d( TAG, "Put activity " + cordova.getActivity().getClass().getCanonicalName() );

                activity.startService(updateServiceIntent);
                isEnabled = true;
                Log.d(TAG, "bg service has been started");
            }
        } else if (ACTION_STOP.equalsIgnoreCase(action)) {
            context.unregisterReceiver(mMessageReceiver);

            isEnabled = false;
            result = true;
            activity.stopService(updateServiceIntent);
            callbackContext.success();
            Log.d(TAG, "bg service has been stopped");
        } else if (ACTION_CONFIGURE.equalsIgnoreCase(action)) {
            result = true;
            try {
                this.callbackContext = callbackContext;
                // Params.
                //    0       1       2           3               4                5               6            7           8                9               10              11                12                  13
                //[params, headers, url, stationaryRadius, distanceFilter, locationTimeout, desiredAccuracy, debug, notificationTitle, notificationText, activityType, stopOnTerminate, notificationIcon, notificationIconColor]
                this.params = data.getString(0);
                this.headers = data.getString(1);
                this.url = data.getString(2);
                this.stationaryRadius = data.getString(3);
                this.distanceFilter = data.getString(4);
                this.locationTimeout = data.getString(5);
                this.desiredAccuracy = data.getString(6);
                this.isDebugging = data.getString(7);
                this.notificationTitle = data.getString(8);
                this.notificationText = data.getString(9);
                this.notificationIcon = data.getString(12);
                this.notificationIconColor = data.getString(13);
                this.stopOnTerminate = data.getString(11);
                Log.d(TAG, "bg service configured");
            } catch (JSONException e) {
                callbackContext.error("authToken/url required as parameters: " + e.getMessage());
            }
        } else if (ACTION_SET_CONFIG.equalsIgnoreCase(action)) {
            result = true;
            // TODO reconfigure Service
            callbackContext.success();
            Log.d(TAG, "bg service reconfigured");
        } else if (ACTION_LOCATION_ENABLED_CHECK.equalsIgnoreCase(action)) {
            Log.d(TAG, "location services enabled check");
            try {
                int isLocationEnabled = BackgroundGpsPlugin.isLocationEnabled(context) ? 1 : 0;
                callbackContext.success(isLocationEnabled);
            } catch (SettingNotFoundException e) {
                callbackContext.error("Location setting not found on this platform");
            }
        }

        return result;
    }

    /**
     * Override method in CordovaPlugin.
     * Checks to see if it should turn off
     */
    public void onDestroy() {
        Activity activity = this.cordova.getActivity();

        if(isEnabled && stopOnTerminate.equalsIgnoreCase("true")) {
            activity.stopService(updateServiceIntent);
        }
    }

    public static boolean isLocationEnabled(Context context) throws SettingNotFoundException {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received location from bg service");
            handleMessage(intent);
        }
    };

    private void handleMessage(Intent msg) {
        Bundle data = msg.getExtras();
        switch (data.getInt(Constant.COMMAND, 0))
        {
            case Constant.UPDATE_PROGRESS:
                try {
                    JSONObject location = new JSONObject(data.getString(Constant.DATA));
                    PluginResult result = new PluginResult(PluginResult.Status.OK, location);
                    result.setKeepCallback(true);
                    callbackContext.sendPluginResult(result);
                    Log.d(TAG, "Sending plugin result");
                } catch (JSONException e) {
                    Log.w(TAG, "Error converting message to json");
                }
                break;
            default:
                break;
        }
    }
}
