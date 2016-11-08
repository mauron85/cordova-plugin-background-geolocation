# cordova-plugin-mauron85-background-geolocation

## Description

Cross-platform geolocation for Cordova / PhoneGap with battery-saving "circular region monitoring" and "stop detection".

Plugin can be used for geolocation when app is running in foreground or background. It is more battery and data efficient then html5 geolocation or cordova-geolocation plugin. It can be used side by side with other geolocation providers (eg. html5 navigator.geolocation).

On Android you can choose from two location location providers:
* **ANDROID_DISTANCE_FILTER_PROVIDER** (forked from [cordova-plugin-background-geolocation](https://github.com/christocracy/cordova-plugin-background-geolocation))
* **ANDROID_ACTIVITY_PROVIDER**

See [Which provider should I use?](https://github.com/mauron85/cordova-plugin-background-geolocation/blob/master/PROVIDERS.md) for more information about providers.

## Example Application

Checkout repository [cordova-plugin-background-geolocation-example](https://github.com/mauron85/cordova-plugin-background-geolocation-example).

## Submitting issues

All new issues should follow instructions in [ISSUE_TEMPLATE.md](https://raw.githubusercontent.com/mauron85/cordova-plugin-background-geolocation/master/ISSUE_TEMPLATE.md).
Properly filled issue report will significantly reduce number of follow up questions and decrease issue resolving time.
Most issues cannot be resolved without debug logs. Please try to isolate debug lines related to your issue.
Instructions how to prepare debug logs can be found in section [Debugging](#debugging).
If you're reporting app crash, debug logs might not contain all needed informations about the cause of the crash.
In that case, also provide relevant parts of output of `adb logcat` command.

## Semantic Versioning
This plugin is following semantic versioning as defined http://semver.org

## Migration to 2.0

As version 2.0 platform support for Windows Phone 8 was removed.
Some incompatible changes were introduced:

* option `stopOnTerminate` defaults to true
* option `locationService` renamed to `locationProvider`
* android providers are now **ANDROID_DISTANCE_FILTER_PROVIDER** and **ANDROID_ACTIVITY_PROVIDER**
* removed `locationTimeout` option (use `interval` in milliseconds instead)
* `notificationIcon` was replaced with two separate options (`notificationIconSmall` and `notificationIconLarge`)
* js object backgroundGeoLocation is deprecated use `backgroundGeolocation` instead
* iOS foreground mode witch automatic background mode switch
* iOS [switchMode](#switchmodemodeid-success-fail) allows to switch between foreground and background mode
* setPace on iOS is deprecated use switchMode instead

## Installing the plugin

```
cordova plugin add cordova-plugin-mauron85-background-geolocation
```

Default iOS location permission prompt can be changed in your config.xml:
```
<plugin name="cordova-plugin-mauron85-background-geolocation" spec="^2.2.0">
    <variable name="ALWAYS_USAGE_DESCRIPTION" value="This app requires background tracking enabled" />
</plugin>
```

## Registering plugin for Adobe® PhoneGap™ Build

This plugin should work with Adobe® PhoneGap™ Build without any modification.
To register plugin add following line into your config.xml:

```
<plugin name="cordova-plugin-mauron85-background-geolocation" spec="^2.2.0"/>
```

NOTE: If you're using *hydration*, you have to download and reinstall your app with every new version of the plugin, as plugins are not updated.

## Compilation

### Android
You will need to ensure that you have installed the following items through the Android SDK Manager:

| Name                       | Version |
|----------------------------|---------|
| Android SDK Tools          | 24.4.1  |
| Android SDK Platform-tools | 23.1    |
| Android SDK Build-tools    | 23.0.1  |
| Android Support Repository | 25      |
| Android Support Library    | 23.1.1  |
| Google Play Services       | 29      |
| Google Repository          | 24      |


#### Backwards Compatibility
Please note that as of Cordova Android 6.0.0 icons are by default in mipmap/ directory not drawable/ directory, so this plugin will have a build issue on < 6.0.0 Cordova builds, you will need to update Authenticator.xml to drawable directory from mipmap directory to work on older versions.


## Quick Example

```javascript
document.addEventListener('deviceready', onDeviceReady, false);

function onDeviceReady () {

    /**
    * This callback will be executed every time a geolocation is recorded in the background.
    */
    var callbackFn = function(location) {
        console.log('[js] BackgroundGeolocation callback:  ' + location.latitude + ',' + location.longitude);

        // Do your HTTP request here to POST location to your server.
        // jQuery.post(url, JSON.stringify(location));

        /*
        IMPORTANT:  You must execute the finish method here to inform the native plugin that you're finished,
        and the background-task may be completed.  You must do this regardless if your HTTP request is successful or not.
        IF YOU DON'T, ios will CRASH YOUR APP for spending too much time in the background.
        */
        backgroundGeolocation.finish();
    };

    var failureFn = function(error) {
        console.log('BackgroundGeolocation error');
    };

    // BackgroundGeolocation is highly configurable. See platform specific configuration options
    backgroundGeolocation.configure(callbackFn, failureFn, {
        desiredAccuracy: 10,
        stationaryRadius: 20,
        distanceFilter: 30,
        interval: 60000
    });

    // Turn ON the background-geolocation system.  The user will be tracked whenever they suspend the app.
    backgroundGeolocation.start();

    // If you wish to turn OFF background-tracking, call the #stop method.
    // backgroundGeolocation.stop();
}
```

## API

### configure(success, fail, options)

| Parameter | Type          | Platform | Description                                                                     |
|-----------|---------------|----------|---------------------------------------------------------------------------------|
| `success` | `Function`    | all      | Callback to be executed every time a geolocation is recorded in the background. |
| `fail`    | `Function`    | all      | Callback to be executed every time a geolocation error occurs.                  |
| `options` | `JSON Object` | all      | Configure options                                                               |

Configure options:

| Parameter                 | Type              | Platform     | Description                                                                                                                                                                                                                                                                                                                                        |
|---------------------------|-------------------|--------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `desiredAccuracy`         | `Number`          | all          | Desired accuracy in meters. Possible values [0, 10, 100, 1000]. The lower the number, the more power devoted to GeoLocation resulting in higher accuracy readings. 1000 results in lowest power drain and least accurate readings. @see Apple docs                                                                                                 |
| `stationaryRadius`        | `Number`          | all          | Stationary radius in meters. When stopped, the minimum distance the device must move beyond the stationary location for aggressive background-tracking to engage.                                                                                                                                                                                  |
| `debug`                   | `Boolean`         | all          | When enabled, the plugin will emit sounds for life-cycle events of background-geolocation! See debugging sounds table.                                                                                                                                                                                                                             |
| `distanceFilter`          | `Number`          | all          | The minimum distance (measured in meters) a device must move horizontally before an update event is generated. **@see** [Apple docs](https://developer.apple.com/library/ios/documentation/CoreLocation/Reference/CLLocationManager_Class/CLLocationManager/CLLocationManager.html#//apple_ref/occ/instp/CLLocationManager/distanceFilter).        |
| `stopOnTerminate`         | `Boolean`         | all          | Enable this in order to force a stop() when the application terminated (e.g. on iOS, double-tap home button, swipe away the app). (default true)                                                                                                                                                                                                   |
| `startOnBoot`             | `Boolean`         | Android      | Start background service on device boot. (default false)                                                                                                                                                                                                                                                                                           |
| `startForeground`         | `Boolean`         | Android      | If false location service will not be started in foreground and no notification will be shown. (default true)                                                                                                                                                                                                                                      |
| `interval`                | `Number`          | Android      | The minimum time interval between location updates in milliseconds. **@see** [Android docs](http://developer.android.com/reference/android/location/LocationManager.html#requestLocationUpdates(long,%20float,%20android.location.Criteria,%20android.app.PendingIntent) for more information.                                                     |
| `notificationTitle`       | `String` optional | Android      | Custom notification title in the drawer.                                                                                                                                                                                                                                                                                                           |
| `notificationText`        | `String` optional | Android      | Custom notification text in the drawer.                                                                                                                                                                                                                                                                                                            |
| `notificationIconColor`   | `String` optional | Android      | The accent color to use for notification. Eg. **#4CAF50**.                                                                                                                                                                                                                                                                                         |
| `notificationIconLarge`   | `String` optional | Android      | The filename of a custom notification icon. See android quirks.                                                                                                                                                                                                                                                                                    |
| `notificationIconSmall`   | `String` optional | Android      | The filename of a custom notification icon. See android quirks.                                                                                                                                                                                                                                                                                    |
| `locationProvider`        | `Number`          | Android      | Set location provider **@see** [PROVIDERS.md](https://github.com/mauron85/cordova-plugin-background-geolocation/blob/master/PROVIDERS.md)                                                                                                                                                                                                          |
| `activityType`            | `String`          | iOS          | [AutomotiveNavigation, OtherNavigation, Fitness, Other] Presumably, this affects iOS GPS algorithm. **@see** [Apple docs](https://developer.apple.com/library/ios/documentation/CoreLocation/Reference/CLLocationManager_Class/CLLocationManager/CLLocationManager.html#//apple_ref/occ/instp/CLLocationManager/activityType) for more information |
| `pauseLocationUpdates`    | `Boolean`         | iOS          | Pauses location updates when app is paused (default: true)                                                                                                                                                                                                                                                                                         |
| `url`                     | `String`          | all          | Server url where to send HTTP POST with recorded locations **@see** [HTTP locations posting](#http-locations-posting)                                                                                                                                                                                                                              |
| `syncUrl`                 | `String`          | all          | Server url where to send fail to post locations **@see** [HTTP locations posting](#http-locations-posting)                                                                                                                                                                                                                                         |
| `syncThreshold`           | `Number`          | all          | Specifies how many previously failed locations will be sent to server at once (default: 100)                                                                                                                                                                                                                                                       |
| `httpHeaders`             | `Object`          | all          | Optional HTTP headers sent along in HTTP request                                                                                                                                                                                                                                                                                                   |
| `saveBatteryOnBackground` | `Boolean`         | iOS          | Switch to less accurate significant changes and region monitory when in background (default)                                                                                                                                                                                                                                                       |
| `maxLocations`            | `Number`          | all          | Limit maximum number of locations stored into db (default: 10000)                                                                                                                                                                                                                                                                                  |

Following options are specific to provider as defined by locationProvider option
### ANDROID_ACTIVITY_PROVIDER provider options

| Parameter             | Type      | Platform | Description                                                                                                                                                                                                                      |
|-----------------------|-----------|----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `interval`            | `Number`  | Android  | Rate in milliseconds at which your app prefers to receive location updates. **@see** [android docs](https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest.html#getInterval())          |
| `fastestInterval`     | `Number`  | Android  | Fastest rate in milliseconds at which your app can handle location updates. **@see** [android  docs](https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest.html#getFastestInterval()). |
| `activitiesInterval`  | `Number`  | Android  | Rate in milliseconds at which activity recognition occurs. Larger values will result in fewer activity detections while improving battery life.                                                                                  |
| `stopOnStillActivity` | `Boolean` | Android  | stop() is forced, when the STILL activity is detected (default is true)                                                                                                                                                          |

Success callback will be called with one argument - location object, which tries to mimic w3c [Coordinates interface](http://dev.w3.org/geo/api/spec-source.html#coordinates_interface).

| Callback parameter | Type      | Description                                                            |
|--------------------|-----------|------------------------------------------------------------------------|
| `locationId`       | `Number`  | ID of location as stored in DB (or null)                               |
| `provider`         | `String`  | gps, network, passive or fused                                         |
| `locationProvider` | `Number`  | Location provider                                                      |
| `debug`            | `Boolean` | true if location recorded as part of debug                             |
| `time`             | `Number`  | UTC time of this fix, in milliseconds since January 1, 1970.           |
| `latitude`         | `Number`  | latitude, in degrees.                                                  |
| `longitude`        | `Number`  | longitude, in degrees.                                                 |
| `accuracy`         | `Number`  | estimated accuracy of this location, in meters.                        |
| `speed`            | `Number`  | speed if it is available, in meters/second over ground.                |
| `altitude`         | `Number`  | altitude if available, in meters above the WGS 84 reference ellipsoid. |
| `bearing`          | `Number`  | bearing, in degrees.                                                   |


### start()
Platform: iOS, Android

Start background geolocation.

### stop()
Platform: iOS, Android

Stop background geolocation.

### isLocationEnabled(success, fail)
Platform: iOS, Android

One time check for status of location services. In case of error, fail callback will be executed.

| Success callback parameter | Type      | Description                                          |
|----------------------------|-----------|------------------------------------------------------|
| `enabled`                  | `Boolean` | true/false (true when location services are enabled) |

### showAppSettings()
Platform: Android >= 6, iOS >= 8.0

Show app settings to allow change of app location permissions.

### showLocationSettings()
Platform: iOS, Android

Show system settings to allow configuration of current location sources.

### watchLocationMode(success, fail)
Platform: iOS, Android

Method can be used to detect user changes in location services settings.
If user enable or disable location services then success callback will be executed.
In case or error (SettingNotFoundException) fail callback will be executed.

| Success callback parameter | Type      | Description                                          |
|----------------------------|-----------|------------------------------------------------------|
| `enabled`                  | `Boolean` | true/false (true when location services are enabled) |

### stopWatchingLocationMode()
Platform: iOS, Android

Stop watching for location mode changes.

### getLocations(success, fail)
Platform: iOS, Android

Method will return all stored locations.
This method is useful for initial rendering of user location on a map just after application launch.
NOTE: Returned locations does not contain locationId.

| Success callback parameter | Type    | Description                    |
|----------------------------|---------|--------------------------------|
| `locations`                | `Array` | collection of stored locations |

```javascript
backgroundGeolocation.getLocations(
  function (locations) {
    console.log(locations);
  }
);
```

### getValidLocations(success, fail)
Platform: iOS, Android

Method will return locations, which has not been yet posted to server.
NOTE: Locations does contain locationId.

| Success callback parameter | Type    | Description                    |
|----------------------------|---------|--------------------------------|
| `locations`                | `Array` | collection of stored locations |

### deleteLocation(locationId, success, fail)
Platform: iOS, Android

Delete location with locationId.

Note: Locations are not actually deleted from database to avoid gaps in locationId numbering.
Instead locations are marked as deleted. Locations marked as deleted will not appear in output of `backgroundGeolocation.getLocations`.

### deleteAllLocations(success, fail)
Note: You don't need to delete all locations. Plugin manages number of locations automatically and location count never exceeds number as defined by `option.maxLocations`.

Platform: iOS, Android

Delete all stored locations.

### switchMode(modeId, success, fail)
Platform: iOS

Normally plugin will handle switching between **BACKGROUND** and **FOREGROUND** mode itself.
Calling switchMode you can override plugin behavior and force plugin to switch into other mode.

In **FOREGROUND** mode plugin uses iOS local manager to receive locations and behavior is affected
by `option.desiredAccuracy` and `option.distanceFilter`.

In **BACKGROUND** mode plugin uses significant changes and region monitoring to receive locations
and uses `option.stationaryRadius` only.

```
// switch to FOREGROUND mode
backgroundGeolocation.switchMode(backgroundGeolocation.mode.FOREGROUND);

// switch to BACKGROUND mode
backgroundGeolocation.switchMode(backgroundGeolocation.mode.BACKGROUND);
```
### getLogEntries(limit, success, fail)
Platform: iOS, Android

Return all logged events. Useful for plugin debugging.
Parameter `limit` limits number of returned entries.
**@see [Debugging](#debugging)** for more information.

## Real world example

``` javascript
backgroundGeolocation.configure(callbackFn, failureFn, {
    desiredAccuracy: 10,
    stationaryRadius: 20,
    distanceFilter: 30,
    url: 'http://192.168.81.15:3000/locations',
    httpHeaders: { 'X-FOO': 'bar' },
    maxLocations: 1000,
    // Android only section
    locationProvider: backgroundGeolocation.provider.ANDROID_ACTIVITY_PROVIDER,
    interval: 60000,
    fastestInterval: 5000,
    activitiesInterval: 10000,
    notificationTitle: 'Background tracking',
    notificationText: 'enabled',
    notificationIconColor: '#FEDD1E',
    notificationIconLarge: 'mappointer_large',
    notificationIconSmall: 'mappointer_small'
});

backgroundGeolocation.watchLocationMode(
  function (enabled) {
    if (enabled) {
      // location service are now enabled
      // call backgroundGeolocation.start
      // only if user already has expressed intent to start service
    } else {
      // location service are now disabled or we don't have permission
      // time to change UI to reflect that
    }
  },
  function (error) {
    console.log('Error watching location mode. Error:' + error);
  }
);

backgroundGeolocation.isLocationEnabled(function (enabled) {
  if (enabled) {
    backgroundGeolocation.start(
      function () {
        // service started successfully
        // you should adjust your app UI for example change switch element to indicate
        // that service is running
      },
      function (error) {
        // Tracking has not started because of error
        // you should adjust your app UI for example change switch element to indicate
        // that service is not running
        if (error.code === 2) {
          if (window.confirm('Not authorized for location updates. Would you like to open app settings?')) {
            backgroundGeolocation.showAppSettings();
          }
        } else {
          window.alert('Start failed: ' + error.message);  
        }
      }
    );
  } else {
    // Location services are disabled
    if (window.confirm('Location is disabled. Would you like to open location settings?')) {
      backgroundGeolocation.showLocationSettings();
    }
  }
});
```

## HTTP locations posting

All locations updates are recorded in local db at all times. When App is in foreground or background in addition to storing location in local db, location callback function is triggered. Number of location stored in db is limited by `option.maxLocations` a never exceeds this number. Instead old locations are replaced by new ones.

When `option.url` is defined, each location is also immediately posted to url defined by `option.url`. If post is successful, the location is marked as deleted in local db. All failed to post locations will be coalesced and send in some time later in one single batch. Batch sync takes place only when number of failed to post locations reaches `option.syncTreshold`.
Optionally different url for batch sync can be defined by `option.syncUrl`. If `option.syncUrl` is not set then `option.url` will be used instead.

When only `option.syncUrl` is defined. Locations are send only in single batch, when number of locations reaches `option.syncTreshold`. (No individual location will be send)

Request body of posted locations is always array, even when only one location is sent.

Warning: `option.maxLocations` has to be larger than `option.syncTreshold`. It's recommended to be 2x larger. In other case location syncing might not work properly.

### Example of express (nodejs) server
```javascript
var express    = require('express');
var bodyParser = require('body-parser');

var app = express();

// parse application/json
app.use(bodyParser.json({ type : '*/*' })); // force json

app.post('/locations', function(request, response){
    console.log('Headers:\n', request.headers);
    console.log('Body:\n', request.body);
    console.log('------------------------------');
    response.sendStatus(200);
});

app.listen(3000);
console.log('Server started...');
```

## Quirks

### iOS

On iOS the plugin will execute your configured ```callbackFn```. You may manually POST the received ```Geolocation``` to your server using standard XHR. The plugin uses iOS Significant Changes API, and starts triggering ```callbackFn``` only when a cell-tower switch is detected (i.e. the device exits stationary radius). The function ```changePace(isMoving, success, failure)``` is provided to force the plugin to enter "moving" or "stationary" state.

#### `stationaryRadius`

Since the plugin uses **iOS** significant-changes API, the plugin cannot detect the exact moment the device moves out of the stationary-radius.  In normal conditions, it can take as much as 3 city-blocks to 1/2 km before stationary-region exit is detected.

### Android

On Android devices it is recommended to have a notification in the drawer (option `startForeground:true`). This gives plugin location service higher priority, decreasing probability of OS killing it. Check [wiki](https://github.com/mauron85/cordova-plugin-background-geolocation/wiki/Android-implementation) for explanation.

#### Custom ROMs

Plugin should work with custom ROMS at least ANDROID_DISTANCE_FILTER_PROVIDER. But ANDROID_ACTIVITY_PROVIDER provider depends on Google Play Services.
Usually ROMs don't include Google Play Services libraries. Strange bugs may occur, like no GPS locations (only from network and passive) and other. When posting issue report, please mention that you're using custom ROM.

#### Multidex
Note: Following section was kindly copied from [phonegap-plugin-push](https://github.com/phonegap/phonegap-plugin-push/blob/master/docs/INSTALLATION.md#multidex). Visit link for resolving issue with facebook plugin.

If you have an issue compiling the app and you're getting an error similar to this (`com.android.dex.DexException: Multiple dex files define`):

```
UNEXPECTED TOP-LEVEL EXCEPTION:
com.android.dex.DexException: Multiple dex files define Landroid/support/annotation/AnimRes;
	at com.android.dx.merge.DexMerger.readSortableTypes(DexMerger.java:596)
	at com.android.dx.merge.DexMerger.getSortedTypes(DexMerger.java:554)
	at com.android.dx.merge.DexMerger.mergeClassDefs(DexMerger.java:535)
	at com.android.dx.merge.DexMerger.mergeDexes(DexMerger.java:171)
	at com.android.dx.merge.DexMerger.merge(DexMerger.java:189)
	at com.android.dx.command.dexer.Main.mergeLibraryDexBuffers(Main.java:502)
	at com.android.dx.command.dexer.Main.runMonoDex(Main.java:334)
	at com.android.dx.command.dexer.Main.run(Main.java:277)
	at com.android.dx.command.dexer.Main.main(Main.java:245)
	at com.android.dx.command.Main.main(Main.java:106)
```

Then at least one other plugin you have installed is using an outdated way to declare dependencies such as `android-support` or `play-services-gcm`.
This causes gradle to fail, and you'll need to identify which plugin is causing it and request an update to the plugin author, so that it uses the proper way to declare dependencies for cordova.
See [this for the reference on the cordova plugin specification](https://cordova.apache.org/docs/en/5.4.0/plugin_ref/spec.html#link-18), it'll be usefull to mention it when creating an issue or requesting that plugin to be updated.

Common plugins to suffer from this outdated dependency management are plugins related to *facebook*, *google+*, *notifications*, *crosswalk* and *google maps*.

#### Android Permissions

Android 6.0 "Marshmallow" introduced a new permissions model where the user can turn on and off permissions as necessary. When user disallow location access permissions, error configure callback will be called with error code: 2.


#### Notification icons

**NOTE:** Only available for API Level >=21.

To use custom notification icons, you need to put icons into *res/drawable* directory **of your app**. You can automate the process  as part of **after_platform_add** hook configured via [config.xml](https://github.com/mauron85/cordova-plugin-background-geolocation-example/blob/master/config.xml). Check [config.xml](https://github.com/mauron85/cordova-plugin-background-geolocation-example/blob/master/config.xml) and [scripts/res_android.js](https://github.com/mauron85/cordova-plugin-background-geolocation-example/blob/master/scripts/res_android.js) of example app for reference.

With Adobe® PhoneGap™ Build icons must be placed into ```locales/android/drawable``` dir at the root of your project. For more information go to [how-to-add-native-image-with-phonegap-build](http://stackoverflow.com/questions/30802589/how-to-add-native-image-with-phonegap-build/33221780#33221780).

### Intel XDK

Plugin will not work in XDK emulator ('Unimplemented API Emulation: BackgroundGeolocation.start' in emulator). But will work on real device.

## Debugging

Plugin logs all activity into database. Logs are retained for 7 days.
You can attach your device to the computer and print logs to console.

* For iOS open Safari and select from menu `Develop` ➜ `Your Device name`
* For Android launch Chrome `about:inspect`

Copy paste following snippet into your browser console:

```javascript
function padLeft(nr, n, str) {
  return Array(n - String(nr).length + 1).join(str || '0') + nr;
}

function printLogs(logEntries, logFormatter, COLORS, MAX_LINES) {
  MAX_LINES = MAX_LINES || 100; // maximum lines to print per batch
  var batch = Math.ceil(logEntries.length / MAX_LINES);
  var logLines = Array(MAX_LINES); //preallocate memory prevents GC
  var logLinesColor = Array(MAX_LINES * 2);
  for (var i = 0; i < batch; i++) {
    var it = 0;
    var logEntriesPart = logEntries.slice((i * MAX_LINES), (i + 1) * MAX_LINES);
    for (var j = 0; j < logEntriesPart.length; j++) {
      var logEntry = logEntriesPart[j];
      logLines[j] = logFormatter(logEntry);
      logLinesColor[it++] = ('background:white;color:black');
      logLinesColor[it++] = (COLORS[logEntry.level]);      
    }
    if (logEntriesPart.length < MAX_LINES) {
      console.log.apply(console, [logLines.slice(0,logEntriesPart.length).join('\n')]
        .concat(logLinesColor.slice(0,logEntriesPart.length*2)));
    } else {
      console.log.apply(console, [logLines.join('\n')].concat(logLinesColor));
    }
  }
}

function printAndroidLogs(logEntries) {
  var COLORS = Object();
  COLORS['ERROR'] = 'background:white;color:red';
  COLORS['WARN'] = 'background:black;color:yellow';
  COLORS['INFO'] = 'background:white;color:blue';
  COLORS['TRACE'] = 'background:white;color:black';
  COLORS['DEBUG'] = 'background:white;color:black';

  var logFormatter = function(logEntry) {
    var d = new Date(logEntry.timestamp);
    var dateStr = [d.getFullYear(), padLeft(d.getMonth()+1,2), padLeft(d.getDate(),2)].join('/');
    var timeStr = [padLeft(d.getHours(),2), padLeft(d.getMinutes(),2), padLeft(d.getSeconds(),2)].join(':');
    return ['%c[', dateStr, ' ', timeStr, '] %c', logEntry.logger, ':', logEntry.message].join('');
  }

  return printLogs(logEntries, logFormatter, COLORS);
}

function printIosLogs(logEntries) {
  var COLORS = Array();
  COLORS[1] = 'background:white;color:red';
  COLORS[2] = 'background:black;color:yellow';
  COLORS[4] = 'background:white;color:blue';
  COLORS[8] = 'background:white;color:black';
  COLORS[16] = 'background:white;color:black';

  var logFormatter = function(logEntry) {
    var d = new Date(logEntry.timestamp * 1000);
    var dateStr = [d.getFullYear(), padLeft(d.getMonth()+1,2), padLeft(d.getDate(),2)].join('/');
    var timeStr = [padLeft(d.getHours(),2), padLeft(d.getMinutes(),2), padLeft(d.getSeconds(),2)].join(':');
    return ['%c[', dateStr, ' ', timeStr, '] %c', logEntry.logger, ':', logEntry.message].join('');
  }

  return printLogs(logEntries, logFormatter, COLORS);
}
```

Print Android logs:

```
backgroundGeolocation.getLogEntries(100, printAndroidLogs);
```

Print iOS logs:

```
backgroundGeolocation.getLogEntries(100, printIosLogs);
```

### Debugging sounds
| *ios*                               | *android*                         |                         |
|-------------------------------------|-----------------------------------|-------------------------|
| Exit stationary region              | Calendar event notification sound | dialtone beep-beep-beep |
| Geolocation recorded                | SMS sent sound                    | tt short beep           |
| Aggressive geolocation engaged      | SIRI listening sound              |                         |
| Passive geolocation engaged         | SIRI stop listening sound         |                         |
| Acquiring stationary location sound | "tick,tick,tick" sound            |                         |
| Stationary location acquired sound  | "bloom" sound                     | long tt beep            |

**NOTE:** For iOS  in addition, you must manually enable the *Audio and Airplay* background mode in *Background Capabilities* to hear these debugging sounds.

## Geofencing
There is nice cordova plugin [cordova-plugin-geofence](https://github.com/cowbell/cordova-plugin-geofence), which does exactly that. Let's keep this plugin lightweight as much as possible.

## Changelog

See [CHANGES.md](/CHANGES.md)

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
