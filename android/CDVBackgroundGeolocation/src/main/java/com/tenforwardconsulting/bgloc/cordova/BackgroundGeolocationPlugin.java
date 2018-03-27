/*
According to apache license

This is fork of christocracy cordova-plugin-background-geolocation plugin
https://github.com/christocracy/cordova-plugin-background-geolocation

Differences to original version:

1. new methods isLocationEnabled, mMessageReciever, handleMessage
*/

package com.tenforwardconsulting.bgloc.cordova;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings.SettingNotFoundException;

import com.marianhello.bgloc.BackgroundGeolocationFacade;
import com.marianhello.bgloc.Config;
import com.marianhello.bgloc.LocationService;
import com.marianhello.bgloc.PluginDelegate;
import com.marianhello.bgloc.PluginError;
import com.marianhello.bgloc.cordova.ConfigMapper;
import com.marianhello.bgloc.data.BackgroundActivity;
import com.marianhello.bgloc.data.BackgroundLocation;
import com.marianhello.logging.LogEntry;
import com.marianhello.logging.LoggerManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

public class BackgroundGeolocationPlugin extends CordovaPlugin implements PluginDelegate {

    public static final String LOCATION_EVENT = "location";
    public static final String STATIONARY_EVENT = "stationary";
    public static final String ACTIVITY_EVENT = "activity";
    public static final String FOREGROUND_EVENT = "foreground";
    public static final String BACKGROUND_EVENT = "background";
    public static final String AUTHORIZATION_EVENT = "authorization";
    public static final String START_EVENT = "start";
    public static final String STOP_EVENT = "stop";

    public static final String ACTION_START = "start";
    public static final String ACTION_STOP = "stop";
    public static final String ACTION_CONFIGURE = "configure";
    public static final String ACTION_SWITCH_MODE = "switchMode";
    public static final String ACTION_LOCATION_ENABLED_CHECK = "isLocationEnabled";
    public static final String ACTION_SHOW_LOCATION_SETTINGS = "showLocationSettings";
    public static final String ACTION_SHOW_APP_SETTINGS = "showAppSettings";
    public static final String ACTION_GET_STATIONARY = "getStationaryLocation";
    public static final String ACTION_GET_ALL_LOCATIONS = "getLocations";
    public static final String ACTION_GET_VALID_LOCATIONS = "getValidLocations";
    public static final String ACTION_DELETE_LOCATION = "deleteLocation";
    public static final String ACTION_DELETE_ALL_LOCATIONS = "deleteAllLocations";
    public static final String ACTION_GET_CONFIG = "getConfig";
    public static final String ACTION_GET_LOG_ENTRIES = "getLogEntries";
    public static final String ACTION_CHECK_STATUS = "checkStatus";
    public static final String ACTION_REGISTER_EVENT_LISTENER = "addEventListener";
    public static final String ACTION_START_TASK = "startTask";
    public static final String ACTION_END_TASK = "endTask";
    public static Context staticContext;
    public static Boolean hasMockLocationsEnabled() {
        if (staticContext != null) {
            return android.provider.Settings.Secure.getString(staticContext.getContentResolver(), android.provider.Settings.Secure.ALLOW_MOCK_LOCATION).equals("1");
        }
        return false;
    }


    private static final int PERMISSIONS_REQUEST_CODE = 1;

    private BackgroundGeolocationFacade facade;

    private CallbackContext callbackContext;

    private org.slf4j.Logger logger;

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();

        facade = new BackgroundGeolocationFacade(this);

        logger = LoggerManager.getLogger(BackgroundGeolocationPlugin.class);
    }

    public boolean execute(String action, final JSONArray data, final CallbackContext callbackContext) {
        Context context = getContext();
        staticContext = context;

        if (ACTION_REGISTER_EVENT_LISTENER.equals(action)) {
            logger.debug("Registering event listeners");
            this.callbackContext = callbackContext;

            return true;
        }
        else if (ACTION_START.equals(action)) {
            runOnWebViewThread(new Runnable() {
                public void run() {
                    start();
                }
            });

            return true;
        } else if (ACTION_STOP.equals(action)) {
            runOnWebViewThread(new Runnable() {
                public void run() {
                    facade.stop();
                }
            });

            return true;
        } else if (ACTION_SWITCH_MODE.equals(action)) {
            try {
                int mode = data.getInt(0);
                facade.switchMode(mode);
            } catch (JSONException e) {
                logger.error("Switch mode error: {}", e.getMessage());
                sendError(new PluginError(PluginError.JSON_ERROR, e.getMessage()));
            }

            return true;
        } else if (ACTION_CONFIGURE.equals(action)) {
            runOnWebViewThread(new Runnable() {
                public void run() {
                    try {
                        Config config = ConfigMapper.fromJSONObject(data.getJSONObject(0));
                        facade.configure(config);
                        callbackContext.success();
                    } catch (Exception e) {
                        logger.error("Configuration error: {}", e.getMessage());
                        callbackContext.error("Configuration error: " + e.getMessage());
                    }
                }
            });

            return true;
        } else if (ACTION_LOCATION_ENABLED_CHECK.equals(action)) {
            logger.debug("Location services enabled check");
            try {
                callbackContext.success(facade.locationServicesEnabled() ? 1 : 0);
            } catch (SettingNotFoundException e) {
                logger.error("Location service checked failed: {}", e.getMessage());
                callbackContext.error("Location setting error occured");
            }

            return true;
        } else if (ACTION_SHOW_LOCATION_SETTINGS.equals(action)) {
            BackgroundGeolocationFacade.showLocationSettings(context);

            return true;
        } else if (ACTION_SHOW_APP_SETTINGS.equals(action)) {
            BackgroundGeolocationFacade.showAppSettings(context);

            return true;
        } else if (ACTION_GET_STATIONARY.equals(action)) {
            try {
                BackgroundLocation stationaryLocation = facade.getStationaryLocation();
                if (stationaryLocation != null) {
                    callbackContext.success(stationaryLocation.toJSONObject());
                } else {
                    callbackContext.success();
                }
            } catch (JSONException e) {
                logger.error("Getting stationary location failed: {}", e.getMessage());
                callbackContext.error("Getting stationary location failed");
            }

            return true;
        } else if (ACTION_GET_ALL_LOCATIONS.equals(action)) {
            runOnWebViewThread(new Runnable() {
                public void run() {
                    try {
                        callbackContext.success(getAllLocations());
                    } catch (JSONException e) {
                        logger.error("Getting all locations failed: {}", e.getMessage());
                        callbackContext.error("Converting locations to JSON failed.");
                    }
                }
            });

            return true;
        } else if (ACTION_GET_VALID_LOCATIONS.equals(action)) {
            runOnWebViewThread(new Runnable() {
                public void run() {
                    try {
                        callbackContext.success(getValidLocations());
                    } catch (JSONException e) {
                        logger.error("Getting valid locations failed: {}", e.getMessage());
                        callbackContext.error("Converting locations to JSON failed.");
                    }
                }
            });

            return true;
        } else if (ACTION_DELETE_LOCATION.equals(action)) {
            runOnWebViewThread(new Runnable() {
                public void run() {
                    try {
                        Long locationId = data.getLong(0);
                        facade.deleteLocation(locationId);
                        callbackContext.success();
                    } catch (JSONException e) {
                        logger.error("Delete location failed: {}", e.getMessage());
                        callbackContext.error("Deleting location failed: " + e.getMessage());
                    }
                }
            });

            return true;
        } else if (ACTION_DELETE_ALL_LOCATIONS.equals(action)) {
            runOnWebViewThread(new Runnable() {
                public void run() {
                    facade.deleteAllLocations();
                    callbackContext.success();
                }
            });

            return true;
        } else if (ACTION_GET_CONFIG.equals(action)) {
            runOnWebViewThread(new Runnable() {
                public void run() {
                    try {
                        Config config = facade.getConfig();
                        callbackContext.success(ConfigMapper.toJSONObject(config));
                    } catch (JSONException e) {
                        logger.error("Error getting mConfig: {}", e.getMessage());
                        callbackContext.error("Error getting mConfig: " + e.getMessage());
                    }
                }
            });

            return true;
        } else if (ACTION_GET_LOG_ENTRIES.equals(action)) {
            runOnWebViewThread(new Runnable() {
                public void run() {
                    try {
                        callbackContext.success(getLogs(data.getInt(0)));
                    } catch (Exception e) {
                        callbackContext.error("Getting logs failed: " + e.getMessage());
                    }
                }
            });

            return true;
        } else if (ACTION_CHECK_STATUS.equals(action)) {
            runOnWebViewThread(new Runnable() {
                public void run() {
                    try {
                        callbackContext.success(checkStatus());
                    } catch (Exception e) {
                        callbackContext.error("Getting logs failed: " + e.getMessage());
                    }
                }
            });

            return true;
        } else if (ACTION_START_TASK.equals(action)) {
            callbackContext.success(1);
            return true;
        } else if (ACTION_END_TASK.equals(action)) {
            callbackContext.success();
            return true;
        }

        return false;
    }

    private void start() {
        if (facade.hasPermissions()) {
            try {
                facade.start();
            } catch (JSONException e) {
                logger.error("Configuration error: {}", e.getMessage());
                sendError(new PluginError(PluginError.JSON_ERROR, e.getMessage()));
            }
        } else {
            logger.debug("Permissions not granted");
            cordova.requestPermissions(this, PERMISSIONS_REQUEST_CODE, BackgroundGeolocationFacade.PERMISSIONS);
        }
    }

    /**
     * Called when the system is about to start resuming a previous activity.
     *
     * @param multitasking		Flag indicating if multitasking is turned on for app
     */
    public void onPause(boolean multitasking) {
        logger.info("App will be paused multitasking={}", multitasking);
        facade.switchMode(BackgroundGeolocationFacade.BACKGROUND_MODE);
        sendEvent(BACKGROUND_EVENT);
    }

    /**
     * Called when the activity will start interacting with the user.
     *
     * @param multitasking		Flag indicating if multitasking is turned on for app
     */
    public void onResume(boolean multitasking) {
        logger.info("App will be resumed multitasking={}", multitasking);
        facade.switchMode(BackgroundGeolocationFacade.FOREGROUND_MODE);
        sendEvent(FOREGROUND_EVENT);
    }

    /**
     * Called when the activity is becoming visible to the user.
     */
    public void onStart() {
        logger.info("App is visible");
    }

    /**
     * Called when the activity is no longer visible to the user.
     */
    public void onStop() {
        logger.info("App is no longer visible");
    }

    /**
     * The final call you receive before your activity is destroyed.
     * Checks to see if it should turn off
     */
     @Override
    public void onDestroy() {
        logger.info("Destroying plugin");
        facade.onAppDestroy();
        super.onDestroy();
    }

    @Override
    public Activity getActivity() {
        return cordova.getActivity();
    }

    public Context getContext() {
        return getActivity().getApplicationContext();
    }

    protected Application getApplication() {
        return getActivity().getApplication();
    }

    private void sendEvent(String name) {
         if (callbackContext == null) {
             return;
         }
        JSONObject event = new JSONObject();
        try {
            event.put("name", name);
            PluginResult result = new PluginResult(PluginResult.Status.OK, event);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
        } catch (JSONException e) {
            logger.error("Error sending event {}: {}", name, e.getMessage());
        }
    }

    private void sendEvent(String name, JSONObject payload) {
        if (callbackContext == null) {
            return;
        }
        JSONObject event = new JSONObject();
        try {
            event.put("name", name);
            event.put("payload", payload);
            PluginResult result = new PluginResult(PluginResult.Status.OK, event);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
        } catch (JSONException e) {
            logger.error("Error sending event {}: {}", name, e.getMessage());
        }
    }

    private void sendEvent(String name, Integer payload) {
        if (callbackContext == null) {
            return;
        }
        JSONObject event = new JSONObject();
        try {
            event.put("name", name);
            event.put("payload", payload);
            PluginResult result = new PluginResult(PluginResult.Status.OK, event);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
        } catch (JSONException e) {
            logger.error("Error sending event {}: {}", name, e.getMessage());
        }
    }

    private void sendError(PluginError error) {
        if (callbackContext == null) {
            return;
        }
        try {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, error.toJSONObject());
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
        } catch(JSONException je) {
            logger.error("Error sending error {}: {}", je.getMessage());
        }
    }

    private void runOnUiThread(Runnable runnable) {
        getActivity().runOnUiThread(runnable);
    }

    private void runOnWebViewThread(Runnable runnable) {
        cordova.getThreadPool().execute(runnable);
    }

    private JSONArray getAllLocations() throws JSONException {
        JSONArray jsonLocationsArray = new JSONArray();
        Collection<BackgroundLocation> locations = facade.getLocations();
        for (BackgroundLocation location : locations) {
            jsonLocationsArray.put(location.toJSONObjectWithId());
        }
        return jsonLocationsArray;
    }

    private JSONArray getValidLocations() throws JSONException {
        JSONArray jsonLocationsArray = new JSONArray();
        Collection<BackgroundLocation> locations = facade.getValidLocations();
        for (BackgroundLocation location : locations) {
            jsonLocationsArray.put(location.toJSONObjectWithId());
        }
        return jsonLocationsArray;
    }

    private JSONArray getLogs(Integer limit) throws Exception {
        JSONArray jsonLogsArray = new JSONArray();
        Collection<LogEntry> logEntries = facade.getLogEntries(limit);
        for (LogEntry logEntry : logEntries) {
            jsonLogsArray.put(logEntry.toJSONObject());
        }
        return jsonLogsArray;
    }

    private JSONObject checkStatus() throws Exception {
        JSONObject json = new JSONObject();
        json.put("isRunning", LocationService.isRunning());
        json.put("hasPermissions", facade.hasPermissions()); //@Deprecated
        json.put("locationServicesEnabled", facade.locationServicesEnabled());
        json.put("authorization", facade.getAuthorizationStatus());

        return json;
    }

    @Override
    public void onAuthorizationChanged(int authStatus) {
        sendEvent(AUTHORIZATION_EVENT, authStatus);
    }

    @Override
    public void onLocationChanged(BackgroundLocation location) {
        try {
            sendEvent(LOCATION_EVENT, location.toJSONObjectWithId());
        } catch (JSONException e) {
            logger.error("Error converting location to json: {}", e.getMessage());
            sendError(new PluginError(PluginError.JSON_ERROR, e.getMessage()));
        }
    }

    @Override
    public void onStationaryChanged(BackgroundLocation location) {
        try {
            sendEvent(STATIONARY_EVENT, location.toJSONObjectWithId());
        } catch (JSONException e) {
            logger.error("Error converting location to json: {}", e.getMessage());
            sendError(new PluginError(PluginError.JSON_ERROR, e.getMessage()));
        }
    }

    @Override
    public void onActitivyChanged(BackgroundActivity activity) {
        try {
            sendEvent(ACTIVITY_EVENT, activity.toJSONObject());
        } catch (JSONException e) {
            logger.error("Error converting activity to json: {}", e.getMessage());
            sendError(new PluginError(PluginError.JSON_ERROR, e.getMessage()));
        }
    }

    @Override
    public void onLocationPause() {
        sendEvent(STOP_EVENT);
    }

    @Override
    public void onLocationResume() {
        sendEvent(START_EVENT);
    }

    @Override
    public void onError(PluginError error) {
        sendError(error);
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length == 0) {
                    // permission denied
                    logger.info("User denied requested permissions");
                    onAuthorizationChanged(BackgroundGeolocationFacade.AUTHORIZATION_DENIED);
                    return;
                }
                for (int grant : grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        // permission denied
                        logger.info("User denied requested permissions");
                        onAuthorizationChanged(BackgroundGeolocationFacade.AUTHORIZATION_DENIED);
                        return;
                    }
                }

                // permission was granted
                // start service
                logger.info("User granted requested permissions");
                facade.start();

                return;
            }
        }
    }
}
