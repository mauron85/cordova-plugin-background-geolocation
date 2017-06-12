/*
 According to apache license

 This is fork of christocracy cordova-plugin-background-geolocation plugin
 https://github.com/christocracy/cordova-plugin-background-geolocation

 Differences to original version:

 1. new method isLocationEnabled
 */

var exec = require('cordova/exec');
var emptyFnc = function(){};

var backgroundGeolocation = {

    /**
     * @property {Object} provider
     */
    provider: {
        ANDROID_DISTANCE_FILTER_PROVIDER: 0,
        ANDROID_ACTIVITY_PROVIDER: 1
    },

    mode: {
        BACKGROUND: 0,
        FOREGROUND: 1
    },

    accuracy: {
        HIGH: 0,
        MEDIUM: 100,
        LOW: 1000,
        PASSIVE: 10000
    },

    configure: function(success, failure, config) {
        exec(success || emptyFnc,
            failure || emptyFnc,
            'BackgroundGeolocation',
            'configure',
            [config]
        );
    },

    start: function(success, failure) {
        exec(success || emptyFnc,
            failure || emptyFnc,
            'BackgroundGeolocation',
            'start', []);
    },

    stop: function(success, failure) {
        exec(success || emptyFnc,
            failure || emptyFnc,
            'BackgroundGeolocation',
            'stop', []);
    },

    finish: function(success, failure) {
        exec(success || emptyFnc,
            failure || emptyFnc,
            'BackgroundGeolocation',
            'finish', []);
    },

    changePace: function(mode, success, failure) {
        console.log('[Warning]: changePace is deprecated. Use switchMode instead.')
        this.switchMode(mode, success, failure);
    },

    switchMode: function(mode, success, failure) {
        exec(success || emptyFnc,
            failure || emptyFnc,
            'BackgroundGeolocation',
            'switchMode', [mode]);
    },

    getConfig: function(success, failure) {
      if (typeof(success) !== 'function') {
           throw 'BackgroundGeolocation#getConfig requires a success callback';
      }
      exec(success,
          failure || emptyFnc,
          'BackgroundGeolocation',
          'getConfig', []);
    },

    /**
     * Returns current stationaryLocation if available.  null if not
     */
    getStationaryLocation: function(success, failure) {
        if (typeof(success) !== 'function') {
             throw 'BackgroundGeolocation#getStationaryLocation requires a success callback';
        }
        exec(success,
            failure || emptyFnc,
            'BackgroundGeolocation',
            'getStationaryLocation', []);
    },

    /**
     * Add a stationary-region listener.  Whenever the devices enters "stationary-mode", your #success callback will be executed with #location param containing #radius of region
     * @param {Function} success
     * @param {Function} failure [optional] NOT IMPLEMENTED
     */
    onStationary: function(success, failure) {
        if (typeof(success) !== 'function') {
             throw 'BackgroundGeolocation#onStationary requires a success callback';
        }
        exec(success,
            failure || emptyFnc,
            'BackgroundGeolocation',
            'addStationaryRegionListener', []);
    },

    isLocationEnabled: function(success, failure) {
        if (typeof(success) !== 'function') {
             throw 'BackgroundGeolocation#isLocationEnabled requires a success callback';
        }
        exec(success,
            failure || emptyFnc,
            'BackgroundGeolocation',
            'isLocationEnabled', []);
    },

    showAppSettings: function() {
        exec(emptyFnc,
            emptyFnc,
            'BackgroundGeolocation',
            'showAppSettings', []);
    },

    showLocationSettings: function() {
        exec(emptyFnc,
            emptyFnc,
            'BackgroundGeolocation',
            'showLocationSettings', []);
    },

    watchLocationMode: function(success, failure) {
        if (typeof(success) !== 'function') {
             throw 'BackgroundGeolocation#watchLocationMode requires a success callback';
        }
        exec(success,
            failure || emptyFnc,
            'BackgroundGeolocation',
            'watchLocationMode', []);
    },

    stopWatchingLocationMode: function() {
        exec(emptyFnc,
            emptyFnc,
            'BackgroundGeolocation',
            'stopWatchingLocationMode', []);
    },

    getLocations: function(success, failure) {
        if (typeof(success) !== 'function') {
             throw 'BackgroundGeolocation#getLocations requires a success callback';
        }
        exec(success,
            failure || emptyFnc,
            'BackgroundGeolocation',
            'getLocations', []);
    },

    getValidLocations: function(success, failure) {
        if (typeof(success) !== 'function') {
             throw 'BackgroundGeolocation#getValidLocations requires a success callback';
        }
        exec(success,
            failure || emptyFnc,
            'BackgroundGeolocation',
            'getValidLocations', []);
    },

    deleteLocation: function(locationId, success, failure) {
        exec(success || emptyFnc,
            failure || emptyFnc,
            'BackgroundGeolocation',
            'deleteLocation', [locationId]);
    },

    deleteAllLocations: function(success, failure) {
        console.log('[Warning]: deleteAllLocations is deprecated and will be removed in future versions.')
        exec(success || emptyFnc,
            failure || emptyFnc,
            'BackgroundGeolocation',
            'deleteAllLocations', []);
    },

    getLogEntries: function(limit, success, failure) {
        exec(success || emptyFnc,
            failure || emptyFnc,
            'BackgroundGeolocation',
            'getLogEntries', [limit]);
    }
};

/* @Deprecated */
window.backgroundGeoLocation = backgroundGeolocation;

module.exports = backgroundGeolocation;
