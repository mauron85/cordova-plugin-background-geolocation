/*
 According to apache license

 This is fork of christocracy cordova-plugin-background-geolocation plugin
 https://github.com/christocracy/cordova-plugin-background-geolocation

 Differences to original version:

 1. new method isLocationEnabled
 */

var exec = require("cordova/exec");
module.exports = {
    /**
     * @property {Object} stationaryRegion
     */
    stationaryRegion: null,
    /**
     * @property {Object} config
     */
    config: {},

    configure: function (success, failure, config) {
        this.config = config;
        var params = JSON.stringify(config.params || {}),
            headers = JSON.stringify(config.headers || {}),
            url = config.url || 'BackgroundGeoLocation_url',
            stationaryRadius = (config.stationaryRadius >= 0) ? config.stationaryRadius : 50,    // meters
            distanceFilter = (config.distanceFilter >= 0) ? config.distanceFilter : 500,       // meters
            locationTimeout = (config.locationTimeout >= 0) ? config.locationTimeout : 60,      // seconds
            desiredAccuracy = (config.desiredAccuracy >= 0) ? config.desiredAccuracy : 100,     // meters
            debug = config.debug || false,
            notificationIcon = config.notificationIcon || "notification_icon",
            notificationTitle = config.notificationTitle || "Background tracking",
            notificationText = config.notificationText || "ENABLED",
            notificationIconColor = config.notificationIconColor || "#4CAF50",
            activityType = config.activityType || "OTHER",
            stopOnTerminate = config.stopOnTerminate || false;

        exec(success || function () {
            },
            failure || function () {
            },
            'BackgroundGeoLocation',
            'configure',
            [params, headers, url, stationaryRadius, distanceFilter, locationTimeout, desiredAccuracy, debug, notificationTitle, notificationText, activityType, stopOnTerminate, notificationIcon, notificationIconColor]
        );
    },
    start: function (success, failure, config) {
        exec(success || function () {
            },
            failure || function () {
            },
            'BackgroundGeoLocation',
            'start',
            []);
    },
    stop: function (success, failure, config) {
        exec(success || function () {
            },
            failure || function () {
            },
            'BackgroundGeoLocation',
            'stop',
            []);
    },
    finish: function (success, failure) {
        exec(success || function () {
            },
            failure || function () {
            },
            'BackgroundGeoLocation',
            'finish',
            []);
    },
    changePace: function (isMoving, success, failure) {
        exec(success || function () {
            },
            failure || function () {
            },
            'BackgroundGeoLocation',
            'onPaceChange',
            [isMoving]);
    },
    /**
     * @param {Integer} stationaryRadius
     * @param {Integer} desiredAccuracy
     * @param {Integer} distanceFilter
     * @param {Integer} timeout
     */
    setConfig: function (success, failure, config) {
        this.apply(this.config, config);
        exec(success || function () {
            },
            failure || function () {
            },
            'BackgroundGeoLocation',
            'setConfig',
            [config]);
    },
    /**
     * Returns current stationaryLocation if available.  null if not
     */
    getStationaryLocation: function (success, failure) {
        exec(success || function () {
            },
            failure || function () {
            },
            'BackgroundGeoLocation',
            'getStationaryLocation',
            []);
    },
    /**
     * Add a stationary-region listener.  Whenever the devices enters "stationary-mode", your #success callback will be executed with #location param containing #radius of region
     * @param {Function} success
     * @param {Function} failure [optional] NOT IMPLEMENTED
     */
    onStationary: function (success, failure) {
        var me = this;
        success = success || function () {
            };
        var callback = function (region) {
            me.stationaryRegion = region;
            success.apply(me, arguments);
        };
        exec(callback,
            failure || function () {
            },
            'BackgroundGeoLocation',
            'addStationaryRegionListener',
            []);
    },

    isLocationEnabled: function (success, failure) {
        exec(success || function () {
            },
            failure || function () {
            },
            'BackgroundGeoLocation',
            'isLocationEnabled',
            []);
    },

    apply: function (destination, source) {
        source = source || {};
        for (var property in source) {
            if (source.hasOwnProperty(property)) {
                destination[property] = source[property];
            }
        }
        return destination;
    }
};
