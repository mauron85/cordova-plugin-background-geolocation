//
//  BackgroundGeolocationDelegate.h
//
//  Created by Marian Hello on 04/06/16.
//  Version 2.0.0
//
//  According to apache license
//
//  This is class is using code from christocracy cordova-plugin-background-geolocation plugin
//  https://github.com/christocracy/cordova-plugin-background-geolocation

#import <Cordova/CDVPlugin.h>
#import "LocationManager.h"

@interface CDVBackgroundGeolocation : CDVPlugin <LocationManagerDelegate>

- (void) configure:(CDVInvokedUrlCommand*)command;
- (void) start:(CDVInvokedUrlCommand*)command;
- (void) stop:(CDVInvokedUrlCommand*)command;
- (void) finish:(CDVInvokedUrlCommand*)command;
- (void) switchMode:(CDVInvokedUrlCommand*)command;
- (void) isLocationEnabled:(CDVInvokedUrlCommand*)command;
- (void) showAppSettings:(CDVInvokedUrlCommand*)command;
- (void) showLocationSettings:(CDVInvokedUrlCommand*)command;
- (void) addStationaryRegionListener:(CDVInvokedUrlCommand*)command;
- (void) watchLocationMode:(CDVInvokedUrlCommand*)command;
- (void) stopWatchingLocationMode:(CDVInvokedUrlCommand*)command;
- (void) getStationaryLocation:(CDVInvokedUrlCommand *)command;
- (void) getLocations:(CDVInvokedUrlCommand*)command;
- (void) getValidLocations:(CDVInvokedUrlCommand*)command;
- (void) deleteLocation:(CDVInvokedUrlCommand*)command;
- (void) deleteAllLocations:(CDVInvokedUrlCommand*)command;
- (void) getLogEntries:(CDVInvokedUrlCommand*)command;
- (void) onPause:(NSNotification *)notification;
- (void) onResume:(NSNotification *)notification;
- (void) onAppTerminate;

@end
