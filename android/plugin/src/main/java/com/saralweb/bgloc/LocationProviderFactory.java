/*
According to apache license

This is fork of christocracy cordova-plugin-background-geolocation plugin
https://github.com/christocracy/cordova-plugin-background-geolocation

This is a new class
*/

package com.saralweb.bgloc;

import android.content.Context;
import com.saralweb.bgloc.data.DAOFactory;
import com.saralweb.bgloc.LocationProvider;
import com.saralweb.bgloc.DistanceFilterLocationProvider;
import com.saralweb.bgloc.ActivityRecognitionLocationProvider;
import java.lang.IllegalArgumentException;

/**
 * LocationProviderFactory
 */
public class LocationProviderFactory {

    private LocationService context;

    public LocationProviderFactory(LocationService context) {
        this.context = context;
    };

    public LocationProvider getInstance (Integer locationProvider) {
        LocationProvider provider;
        switch (locationProvider) {
            case Config.ANDROID_DISTANCE_FILTER_PROVIDER:
                provider = new DistanceFilterLocationProvider(context);
                break;
            case Config.ANDROID_ACTIVITY_PROVIDER:
                provider = new ActivityRecognitionLocationProvider(context);
                break;
            default:
                throw new IllegalArgumentException("Provider not found");
        }

        provider.onCreate();
        return provider;
    }
}
