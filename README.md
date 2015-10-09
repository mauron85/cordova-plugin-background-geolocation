cordova-plugin-mauron85-background-geolocation
==============================

Fork notice
==============================

This is fork of [christocracy cordova-backgroud-geolocation](https://github.com/christocracy/cordova-plugin-background-geolocation). The main change is in Android version. Posting positions to url was replaced by callbacks, so now it works same as in iOS. It was possible be using intents.

Warning: You probably have to set your cordova app to keep running by keepRunning property to true

Description
==============================

Cross-platform background geolocation for Cordova / PhoneGap with battery-saving "circular region monitoring" and "stop detection".

Follows the [Cordova Plugin spec](https://github.com/apache/cordova-plugman/blob/master/plugin_spec.md), so that it works with [Plugman](https://github.com/apache/cordova-plugman).

This plugin leverages Cordova/PhoneGap's [require/define functionality used for plugins](http://simonmacdonald.blogspot.ca/2012/08/so-you-wanna-write-phonegap-200-android.html).

## Installing the plugin ##

As Cordova is [shifting towards npm](http://cordova.apache.org/announcements/2015/04/21/plugins-release-and-move-to-npm.html), this plugin can be installed from npm:

```
cordova plugin add cordova-plugin-mauron85-background-geolocation
```

## Registering plugin for Adobe® PhoneGap™ Build

[Adobe® PhoneGap™ Build](http://build.phonegap.com) supports plugins from npm as well. To register plugin add following line into your config.xml

```
<gap:plugin name="cordova-plugin-mauron85-background-geolocation" source="npm"/>
```

## Using the plugin ##
The plugin creates the object `window.plugins.backgroundGeoLocation` with the methods

  `configure(success, fail, option)`,

  `start(success, fail)`

  `stop(success, fail)`.

A full example could be:
```
    //
    //
    // after deviceready
    //
    //

    // Your app must execute AT LEAST ONE call for the current position via standard Cordova geolocation,
    //  in order to prompt the user for Location permission.
    window.navigator.geolocation.getCurrentPosition(function(location) {
        console.log('Location from Phonegap');
    });

    var bgGeo = window.plugins.backgroundGeoLocation;

    /**
    * This would be your own callback for Ajax-requests after POSTing background geolocation to your server.
    */
    var yourAjaxCallback = function(response) {
        ////
        // IMPORTANT:  You must execute the #finish method here to inform the native plugin that you're finished,
        //  and the background-task may be completed.  You must do this regardless if your HTTP request is successful or not.
        // IF YOU DON'T, ios will CRASH YOUR APP for spending too much time in the background.
        //
        //
        bgGeo.finish();
    };

    /**
    * This callback will be executed every time a geolocation is recorded in the background.
    */
    var callbackFn = function(location) {
        console.log('[js] BackgroundGeoLocation callback:  ' + location.latitude + ',' + location.longitude);
        // Do your HTTP request here to POST location to your server.
        //
        //
        yourAjaxCallback.call(this);
    };

    var failureFn = function(error) {
        console.log('BackgroundGeoLocation error');
    }

    // BackgroundGeoLocation is highly configurable.
    bgGeo.configure(callbackFn, failureFn, {
        desiredAccuracy: 10,
        stationaryRadius: 20,
        distanceFilter: 30,
        notificationTitle: 'Background tracking', // <-- android only, customize the title of the notification
        notificationText: 'ENABLED', // <-- android only, customize the text of the notification
        notificationIcon: 'notification_icon', // <-- android only, customize the notification icon
        activityType: 'AutomotiveNavigation',
        debug: true, // <-- enable this hear sounds for background-geolocation life-cycle.
        stopOnTerminate: false // <-- enable this to clear background location settings when the app terminates
    });

    // Turn ON the background-geolocation system.  The user will be tracked whenever they suspend the app.
    bgGeo.start();

    // If you wish to turn OFF background-tracking, call the #stop method.
    // bgGeo.stop()


```

NOTE: The plugin includes `org.apache.cordova.geolocation` as a dependency.  You must enable Cordova's GeoLocation in the foreground and have the user accept Location services by executing `#watchPosition` or `#getCurrentPosition`.

## Example Application

This plugin hosts a SampleApp in ```example/SampleApp``` folder.  This SampleApp contains no plugins so you must first start by adding this plugin

```
$ cordova create SampleApp
$ cd SampleApp
$ cordova plugin add cordova-plugin-mauron85-background-geolocation
$ cordova platform add ios
$ cordova build ios

```

If you're using XCode, boot the SampleApp in the iOS Simulator and enable ```Debug->Location->City Drive```.


## Behaviour

The plugin has features allowing you to control the behaviour of background-tracking, striking a balance between accuracy and battery-usage.  In stationary-mode, the plugin attempts to descrease its power usage and accuracy by setting up a circular stationary-region of configurable #stationaryRadius.  iOS has a nice system  [Significant Changes API](https://developer.apple.com/library/ios/documentation/CoreLocation/Reference/CLLocationManager_Class/CLLocationManager/CLLocationManager.html#//apple_ref/occ/instm/CLLocationManager/startMonitoringSignificantLocationChanges), which allows the os to suspend your app until a cell-tower change is detected (typically 2-3 city-block change) Android uses [LocationManager#addProximityAlert](http://developer.android.com/reference/android/location/LocationManager.html). Windows Phone does not have such a API.

When the plugin detects your user has moved beyond his stationary-region, it engages the native platform's geolocation system for aggressive monitoring according to the configured `#desiredAccuracy`, `#distanceFilter` and `#locationTimeout`.  The plugin attempts to intelligently scale `#distanceFilter` based upon the current reported speed.  Each time `#distanceFilter` is determined to have changed by 5m/s, it recalculates it by squaring the speed rounded-to-nearest-five and adding #distanceFilter (I arbitrarily came up with that formula.  Better ideas?).

  `(round(speed, 5))^2 + distanceFilter`

## iOS

On iOS the plugin will execute your configured ```callbackFn```. You may manually POST the received ```GeoLocation``` to your server using standard XHR. iOS ignores the @config params ```url```, ```params``` and ```headers```. The plugin uses iOS Significant Changes API, and starts triggering ```callbackFn``` only when a cell-tower switch is detected (i.e. the device exits stationary radius). The function ```changePace(isMoving, success, failure)``` is provided to force the plugin to enter "moving" or "stationary" state.


### Android

Android **WILL** execute your configured ```callbackFn```. This is the main difference from original christocracy plugin. Android is using intents to do so. Since the Android plugin must run as an autonomous Background Service, disconnected from your the main Android Activity (your foreground application), the background-geolocation plugin will continue to run, even if the foreground Activity is killed due to memory constraints.

### WP8

WP8 uses ```callbackFn``` the way iOS do. On WP8, however, the plugin does not support the Stationary location and does not implement ```getStationaryLocation()``` and ```onPaceChange()```.
Keep in mind that it is **not** possible to use ```start()``` at the ```pause``` event of Cordova/PhoneGap. WP8 suspend your app immediately and ```start()``` will not be executed. So make sure you fire ```start()``` before the app is closed/minimized.

### Config

Use the following config-parameters with the #configure method:

#####`@param {Integer} desiredAccuracy [0, 10, 100, 1000] in meters`

The lower the number, the more power devoted to GeoLocation resulting in higher accuracy readings.  1000 results in lowest power drain and least accurate readings.  @see [Apple docs](https://developer.apple.com/library/ios/documentation/CoreLocation/Reference/CLLocationManager_Class/CLLocationManager/CLLocationManager.html#//apple_ref/occ/instp/CLLocationManager/desiredAccuracy)

#####`@param {Integer} stationaryRadius (meters)`

When stopped, the minimum distance the device must move beyond the stationary location for aggressive background-tracking to engage.  Note, since the plugin uses iOS significant-changes API, the plugin cannot detect the exact moment the device moves out of the stationary-radius.  In normal conditions, it can take as much as 3 city-blocks to 1/2 km before staionary-region exit is detected.

#####`@param {Boolean} debug`

When enabled, the plugin will emit sounds for life-cycle events of background-geolocation!  **NOTE iOS**:  In addition, you must manually enable the *Audio and Airplay* background mode in *Background Capabilities* to hear these debugging sounds.

- Exit stationary region:  *[ios]* Calendar event notification sound *[android]* dialtone beep-beep-beep
- GeoLocation recorded:  *[ios]* SMS sent sound, *[android]* tt short beep, *[WP8]* High beep, 1 sec.
- Aggressive geolocation engaged:  *[ios]* SIRI listening sound, *[android]* none
- Passive geolocation engaged:  *[ios]* SIRI stop listening sound, *[android]* none
- Acquiring stationary location sound: *[ios]* "tick,tick,tick" sound, *[android]* none
- Stationary location acquired sound:  *[ios]* "bloom" sound, *[android]* long tt beep.

![Enable Background Audio](/enable-background-audio.png "Enable Background Audio")

#####`@param {Integer} distanceFilter`

The minimum distance (measured in meters) a device must move horizontally before an update event is generated.  @see [Apple docs](https://developer.apple.com/library/ios/documentation/CoreLocation/Reference/CLLocationManager_Class/CLLocationManager/CLLocationManager.html#//apple_ref/occ/instp/CLLocationManager/distanceFilter).  However, #distanceFilter is elastically auto-calculated by the plugin:  When speed increases, #distanceFilter increases;  when speed decreases, so does distanceFilter.

distanceFilter is calculated as the square of speed-rounded-to-nearest-5 and adding configured #distanceFilter.

  `(round(speed, 5))^2 + distanceFilter`

For example, at biking speed of 7.7 m/s with a configured distanceFilter of 30m:

  `=> round(7.7, 5)^2 + 30`
  `=> (10)^2 + 30`
  `=> 100 + 30`
  `=> 130`

A gps location will be recorded each time the device moves 130m.

At highway speed of 30 m/s with distanceFilter: 30,

  `=> round(30, 5)^2 + 30`
  `=> (30)^2 + 30`
  `=> 900 + 30`
  `=> 930`

A gps location will be recorded every 930m

Note the following real example of background-geolocation on highway 101 towards San Francisco as the driver slows down as he runs into slower traffic (geolocations become compressed as distanceFilter decreases)

![distanceFilter at highway speed](/distance-filter-highway.png "distanceFilter at highway speed")

Compare now background-geolocation in the scope of a city.  In this image, the left-hand track is from a cab-ride, while the right-hand track is walking speed.

![distanceFilter at city scale](/distance-filter-city.png "distanceFilter at city scale")

#####`@param {Boolean} stopOnTerminate`
Enable this in order to force a stop() when the application terminated (e.g. on iOS, double-tap home button, swipe away the app)


### Android Config

#####`@param {String} notificationText/Title`

On Android devices it is required to have a notification in the drawer because it's a "foreground service".  This gives it high priority, decreasing probability of OS killing it.  To customize the title and text of the notification, set these options.

#####`@param {Integer} locationTimeout`

The minimum time interval between location updates, in seconds.  See [Android docs](http://developer.android.com/reference/android/location/LocationManager.html#requestLocationUpdates(long,%20float,%20android.location.Criteria,%20android.app.PendingIntent)) for more information.

#####`@param {String} notificationIcon`

Optional: the filename of a custom notification icon. The icon must be located in the *res/drawable* directory. You should include a small and large icon (append "\_small" and "\_large" to the end of your image filenames). Omit the small and large when passing notificationIcon to configure. This will default to "notification_icon".

To use custom notification icon eg. new_icon, you need to:

##### 1. Configure plugin
```
bgGeo.configure(callbackFn, failureFn, {
    //... add other config options
    notificationIcon: 'new_icon'
    //... add other config options
});
```

##### 2. Copy icon files
Add your custom *new_icon_small.png* and *new_icon_large.png* to res/drawable directory.

##### 3. Register icons in plugin.xml source-file.
```
<source-file src="res/drawable/new_icon_small.png" target-dir="res/drawable" />
<source-file src="res/drawable/new_icon_large.png" target-dir="res/drawable" />
```

#####`@param {String} notificationIconColor`

Optional: the color of notification icon. Android add a background color to the small icon that is shown as badge or as full icon when the screen is locked.
The default color is green (#4CAF50). The format is HTML color code.

### iOS Config

#####`@param {String} activityType [AutomotiveNavigation, OtherNavigation, Fitness, Other]`

Presumably, this affects ios GPS algorithm.  See [Apple docs](https://developer.apple.com/library/ios/documentation/CoreLocation/Reference/CLLocationManager_Class/CLLocationManager/CLLocationManager.html#//apple_ref/occ/instp/CLLocationManager/activityType) for more information

### WP8 Config

#####`{String} desiredAccuracy`

In Windows Phone, the underlying GeoLocator you can choose to use 'DesiredAccuracy' or 'DesiredAccuracyInMeters'. Since this plugins default configuration accepts meters, the default desiredAccuracy is mapped to the Windows Phone DesiredAccuracyInMeters leaving the DesiredAccuracy enum empty. For more info see the [MS docs](http://msdn.microsoft.com/en-us/library/windows/apps/windows.devices.geolocation.geolocator.desiredaccuracyinmeters) for more information.

## Development

There are many works of original christocracy's plugin. The most interesting repos I've found are:
* [huttj](https://github.com/huttj/cordova-plugin-background-geolocation)
* [erikkemperman](https://github.com/erikkemperman/cordova-plugin-background-geolocation/)
* [codebling](https://github.com/codebling/cordova-plugin-background-geolocation)

Lot of work has been done, but scattered all over the github. My intention is to maintain
this version and adopt all those cool changes. You're more then welcome to pull your request here.

## Changelog

### [0.4.3] - unreleased
### Added
- Android Add icon color parameter

### Changed
- Changed the plugin.xml dependencies to the new NPM-based plugin syntax

### [0.4.2] - 2015-09-30
#### Added
- Android open activity when notification clicked [69989e79a8a67485fc88463eec8d69bb713c2dbe](https://github.com/erikkemperman/cordova-plugin-background-geolocation/commit/69989e79a8a67485fc88463eec8d69bb713c2dbe)

#### Fixed
- Android duplicate desiredAccuracy extra
- Android [compilation error](https://github.com/coletivoEITA/cordova-plugin-background-geolocation/commit/813f1695144823d2a61f9733ced5b9fdedf15ff3)

### [0.4.1] - 2015-09-21
- maintenance version

### [0.4.0] - 2015-03-08
#### Added
- Android using callbacks same as iOS

#### Removed
- Android storing position into sqlite

## Licence ##

[Apache License](http://www.apache.org/licenses/LICENSE-2.0)

Copyright (c) 2013 Christopher Scott, Transistor Software

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.