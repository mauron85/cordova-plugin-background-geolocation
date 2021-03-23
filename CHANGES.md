## Changelog

### [3.1.1] - 2021-03-23
### Fixed
- fix wrong plugin id 

### [3.1.0] - 2019-09-24
### Fixed
- fix package scope
- Android fix RejectedExecutionException
- Android add stop guard

### Changed
- adopt headless task changes in common module

### [3.0.7] - 2019-09-17
### Fixed
- Android Foreground service permission is required since Android 28 - @IsraelHikingMap

### [3.0.6] - 2019-08-27
### Fixed
- Android allow to start service from background on API >=26

### [3.0.5] - 2019-08-13
### Fixed
- Android fix tone generator crash
- Fixed XML config to use to install plugin (PR #575) - @globules-io
- Fixed typo in README - @diegogurpegui

Many thanks to all contributors

### [3.0.1] - 2019-03-28
### Added
- iOS implement config.stopOnTerminate using startMonitoringSignificantLocationChanges

### Fixed
- Android fix don't start service on app visibility change events
fixes: #552, #551

### [3.0.0] - 2019-03-25
### Fixed
- Android fix don't start service on configure
fixes: #552, #551

### [3.0.0-alpha.XY] - unreleased
#### Added
- checkStatus if service is running
- events [start, stop, authorization, background, foreground]
- implement all methods for both platforms
- new RAW_LOCATION_PROVIDER

Since alpha.8:
- onError event signature = { code, message }
- post/sync attributes customization via postTemplate config prop
- enable partial plugin reconfiguration
- Android on "activity" event
- iOS configuration persistence

Since alpha.12:
- iOS ACTIVITY_PROVIDER (experimental)

Since alpha.15:
- checkStatus returns status of location services (locationServicesEnabled)
- iOS RAW_LOCATION_PROVIDER continue to run on app terminate

Since alpha.19:
- Android Headless Task

Since alpha.20:
- Android location parameters isFromMockProvider and mockLocationsEnabled

Since alpha.24:
- Android Oreo support

Since alpha.25:
- method forceSync
- option to get logs by offset and filter by log level
- log uncaught exceptions

Since alpha.30:
- method getCurrentLocation

Since alpha.41:
- notificationsEnabled config option (by [@danielgindi](https://github.com/danielgindi/))
More info: https://github.com/mauron85/react-native-background-geolocation/pull/269
- Allow stopping location updates on status "285 Updates Not Required" (by [@danielgindi](https://github.com/danielgindi/))
More info: https://github.com/mauron85/react-native-background-geolocation/pull/271

Since alpha.45:
- Listen for 401 Unauthorized status codes received from http server (by [@FeNoMeNa](https://github.com/FeNoMeNa/))
More info: https://github.com/mauron85/react-native-background-geolocation/pull/308/files

Since alpha.46:
- typescript definitions

Since alpha.47:
- allow nested location props in postTemplate

#### Changed
- start and stop methods doesn't accept callback (use event listeners instead)
- for background syncing syncUrl option is required
- on Android DISTANCE_FILTER_PROVIDER now accept arbitrary values (before only 10, 100, 1000)
- all plugin constants are in directly BackgroundGeolocation namespace. (check index.js)
- plugin can be started without executing configure (stored settings or defaults will be used)
- location property locationId renamed to just id
- iOS pauseLocationUpdates now default to false (becuase iOS docs now states that you need to restart manually if you set it to true)
- iOS finish method replaced with startTask and endTask

Since alpha.8:
- Android bind to service on facade construct

Since alpha.14:
- iOS saveBatteryOnBackground defaults to false

Since alpha.15:
- shared code base with react-native

Since alpha.25:
- Android common error format
- Android remove sync delay when conditions are met
- Android consider HTTP 201 response code as succesful post
- Android obey system sync setting

Since alpha.28:
- Android remove wake locks
https://github.com/mauron85/background-geolocation-android/pull/4 by @grassick

Since alpha.29:
- Android show service notification only when in background
- Android remove config option startForeground (related to above)

Since alpha.32:
- Android bring back startForeground config option (BREAKING CHANGE!)

startForeground has slightly different meaning.

If false (default) then service will create notification and promotes
itself to foreground service, when client unbinds from service.
This typically happens when application is moving to background.
If app is moving back to foreground (becoming visible to user)
service destroys notification and also stop being foreground service.

If true service will create notification and will stay in foreground at all times.

Since alpha.33:
- Android internal changes (permission handling)

Since alpha.40:
- Android disable notification sound and vibration on oreo
(PR: [#9](https://github.com/mauron85/background-geolocation-android/pull/9)
by [@danielgindi](https://github.com/danielgindi/))

Since alpha.48:
- removeAllListeners - remove all event listeners when calling without parameter

Since alpha.50:
- export BackgroundGeolocationPlugin interface for ionic users (fixes #515)

### Fixed

Since alpha.13:
- iOS open location settings on iOS 10 and later (PR #158) by @asafron

Since alpha.15:
- checkStatus authorization
- Android fix for #362 Build Failed: cannot find symbol (PR #378)

Since alpha.18:
- Android fix #276 - NullPointerException: onTaskRemoved
- Android fix #380 - allow to override android support library

Since alpha.19:
- Android fix event listeners not triggering after app is restarted and service was running

Since alpha.23:
- iOS fix #394 - App Store Rejection - Prefs Non-Public URL Scheme
- iOS reset connectivity status on stop

Since alpha.24:
- Android fix service accidently started with default or stored config

Since alpha.25:
- Android add guards to prevent some race conditions
- Android config null handling

Since alpha.31:
- iOS fix error message format
- iOS fix missing getLogEntries arguments

Since alpha.32:
- iOS display debug notifications in foreground on iOS >= 10
- iOS missing activity provider stationary event
- Android getCurrentLocation request permission prompt

Since alpha.35:
- Android fix issue #431 - "dependencies.gradle" not found

Since alpha.38:
- iOS Fix crash on delete all location ([7392e39](https://github.com/mauron85/background-geolocation-ios/commit/7392e391c3de3ff0d6f5ef2ef19c34aba612bf9b) by [@acerbetti](https://github.com/acerbetti/))

Since alpha.39:
- Android Defer start and configure until service is ready
(PR: [#7](https://github.com/mauron85/background-geolocation-android/pull/7)
Commit: [00e1314](https://github.com/mauron85/background-geolocation-android/commit/00e131478ad4e37576eb85581bb663b65302a4e0) by [@danielgindi](https://github.com/danielgindi/),
fixes #201, #181, #172)

Since alpha.40:
- iOS Avoid taking control of UNUserNotificationCenter
(PR: [#268](https://github.com/mauron85/react-native-background-geolocation/pull/268))

Since alpha.42:
- Android fix locationService treating success as errors
(PR: [#13](https://github.com/mauron85/background-geolocation-android/pull/13)
by [@hoisel](https://github.com/hoisel/))

Since alpha.43:
- Android make sure mService exists when we call start or stop
(PR: [#17](https://github.com/mauron85/background-geolocation-android/pull/17)
by [@ivosabev](https://github.com/ivosabev/))

Since alpha.46:
- Android fix service crash on boot for Android 8 when startOnBoot option is used

Since alpha.48:
- fix typescript definitions (fixes #514)
- Android prefix resource strings to prevent collision with other plugins

Since alpha.49:
- Android fix App Crashes when entering / leaving Background
- Android fix crash on permission when started from background

### [2.3.6] - 2018-09-11
### Fixed
- Android remove non public URL

### [2.3.5] - 2018-03-29
### Fixed
- Android fix #384

### [2.3.3] - 2017-11-17
### Added
- Android allow override google play services version

### [2.3.2] - 2017-11-06
### Fix
- iOS support for iOS 11 (#PR 330)

### [2.3.1] - 2017-10-31
### Fix
- iOS httpHeaders values are not sent with syncUrl on iOS PR #325

### [2.3.0] - 2017-10-31
### Added
- Android Make account name configurable PR #334 by unixmonkey

### [2.2.5] - 2016-11-13
### Fixed
- Android fixing issue #195 PR204

### [2.2.4] - 2016-09-24
### Fixed
- iOS extremely stupid config bug from 2.2.3

### [2.2.3] - 2016-09-23
### Fixed
- Android issue #173 - allow stop service and prevent crash on destroy

### [2.2.2] - 2016-09-22
### Added
- Android android.hardware.location permission

### Fixed
- iOS onStationary null location
- iOS fix potential issue sending outdated location
- iOS handle null config options

### [2.2.1] - 2016-09-15
### Added
- iOS suppress minor error messages on first app run

### [2.2.0] - 2016-09-14
### Added
- iOS option pauseLocationUpdates PR #156

### [2.2.0-alpha.8] - 2016-09-02
### Fixed
- iOS compilation errors

### [2.2.0-alpha.7] - 2016-09-01
#### Removed
- Android location filtering

### Changed
- Android db logging instead of file
- iOS location prop heading renamed to bearing

### [2.2.0-alpha.6] - 2016-08-10
### Fixed
- Android don't try sync when locations count is lower then threshold

### [2.2.0-alpha.5] - 2016-08-10
### Fixed
- Android issue #130 - sync complete notification stays visible
- Android don't try sync when locations count is zero

### [2.2.0-alpha.4] - 2016-08-10
### Fixed
- Android issue #137 - fix only for API LEVEL >= 17

### [2.2.0-alpha.3] - 2016-08-10
### Fixed
- Android issue #139 - Starting backgroundGeolocation just after configure failed

### [2.2.0-alpha.2] - 2016-08-10
### Fixed
- iOS issue #132 use Library as DB path

### [2.2.0-alpha.1] - 2016-08-01
### Added
- Android, iOS limit maximum number of locations in db (maxLocations)
- Android showAppSettings
- Android, iOS database logging (getLogEntries)
- Android, iOS autosync locations to server with configurable threshold
- Android, iOS method getValidLocations
- iOS watchLocationMode and stopWatchingLocationMode
- iOS configurable NSLocationAlwaysUsageDescription

### Changed
- Locations stored into db at all times
- iOS persist locations also when url option is not used
- iOS dropping support for iOS < 4

### Fixed
- Android fix crash on permission change
- Android permission error code: 2
- Android on start err callback instead configure err callback
- Android overall background service reliability
- iOS do not block js thread when posting locations

### [2.1.2] - 2016-06-23
### Fixed
- iOS database not created

### [2.1.1] - private release
### Fixed
- iOS switching mode

### [2.1.0] - private release
### Added
- iOS option saveBatteryOnBackground
- iOS time validation rule for location

### [2.0.0] - 2016-06-17
### Fixed
- iOS prevent unintentional start when in background
- Android Destroy Existing Provider Before Creating New One (#94)

### [2.0.0-rc.3] - 2016-06-13
#### Fixed
- iOS memory leak

### [2.0.0-rc.1] - 2016-06-13
#### Changed
- Android notificationIcon option split into small and large!!!
- Android stopOnTerminate defaults to true
- Android option locationService renamed to locationProvider
- Android providers renamed (see README.md)
- Android bugfixing
- SampleApp moved into separate repo
- deprecated backgroundGeoLocation
- iOS split cordova specific code to allow code sharing with react-native-background-geolocation
- desiredAccuracy map any number
- Android locationTimeout option renamed to interval
- iOS switchMode (formerly setPace)

#### Added
- Android startOnBoot option
- Android startForeground option
- iOS, Android http posting of locations (options url and httpHeaders)
- iOS showLocationSettings
- iOS showAppSettings
- iOS isLocationEnabled
- iOS getLocations
- iOS deleteLocation
- iOS deleteAllLocations
- iOS foreground mode

#### Removed
- WP8 platform
- Android deprecated window.plugins.backgroundGeoLocation

### [1.0.2] - 2016-06-09
#### Fixed
- iOS queued locations are send FIFO (before fix LIFO)

### [1.0.1] - 2016-06-03
#### Fixed
- iOS7 crash on start
- iOS attempt to fix #46 and #39

### [1.0.0] - 2016-06-01
#### Added
- Android ANDROID_FUSED_LOCATION stopOnStillActivity (enhancement #69)

### [0.9.6] - 2016-04-07
#### Fixed
- Android ANDROID_FUSED_LOCATION fixing crash on start
- Android ANDROID_FUSED_LOCATION unregisterReceiver on destroy

### [0.9.5] - 2016-04-05
#### Fixed
- Android ANDROID_FUSED_LOCATION startTracking when STILL after app has started

### [0.9.4] - 2016-01-31
#### Fixed
- Android 6.0 permissions issue #21

### [0.9.3] - 2016-01-29
#### Fixed
- iOS cordova 6 compilation error
- iOS fix for iOS 9

#### Changes
- iOS removing cordova-plugin-geolocation dependency
- iOS user prompt for using location services
- iOS error callback when location services are disabled
- iOS error callback when user denied location tracking
- iOS adding error callbacks to SampleApp

### [0.9.2] - 2016-01-29
#### Fixed
- iOS temporarily using cordova-plugin-geolocation-ios9-fix to fix issues with iOS9
- iOS fixing SampleApp indexedDB issues

### [0.9.1] - 2015-12-18
#### Fixed
- Android ANDROID_FUSED_LOCATION fix config setActivitiesInterval

### [0.9.0] - 2015-12-18
#### Changed
- Android ANDROID_FUSED_LOCATION using ActivityRecognition (saving battery)

### [0.8.3] - 2015-12-18
#### Fixed
- Android fixing crash on exit

### [0.8.2] - 2015-12-18
#### Fixed
- Android fixing #9 - immediate bg service crash

### [0.8.1] - 2015-12-15
#### Fixed
- Android fixing #9

### [0.8.0] - 2015-12-15 (Merry XMas Edition :-)
#### Fixed
- Android persist location when main activity was killed

#### Changed
- Android persisting position when debug is on

### [0.7.3] - 2015-11-06
#### Fixed
- Android issue #11

### [0.7.2] - 2015-10-21
#### Fixed
- iOS fixing plugin dependencies (build)
- iOS related fixes for SampleApp

### [0.7.1] - 2015-10-21
#### Changed
- Android ANDROID_FUSED_LOCATION ditching setSmallestDisplacement(stationaryRadius) (seems buggy)

### [0.7.0] - 2015-10-21
#### Changed
- Android deprecating config option.interval
- Android allow run in background for FusedLocationService (wakeLock)
- Android will try to persist locations when main activity is killed
- Android new methods: (getLocations, deleteLocation, deleteAllLocations)
- Android stop exporting implicit intents (security)
- SampleApp updates

### [0.6.0] - 2015-10-17
#### Changed
- deprecating window.plugins clobber
- SampleApp updates

#### Added
- Android showLocationSettings and watchLocationMode

### [0.5.4] - 2015-10-13
#### Changed
- Android only cosmetic changes, but we need stable base

### [0.5.3] - 2015-10-12
#### Changed
- Android not setting any default notificationIcon and notificationIconColor.
- Android refactoring
- Android updated SampleApp

### [0.5.2] - 2015-10-12
#### Fixed
- Android fixing FusedLocationService start and crash on stop

### [0.5.1] - 2015-10-12
#### Fixed
- Android fix return types
- Android fix #3 NotificationBuilder.setColor method not present in API Level <21

#### Changed
- Android replacing Notication.Builder for NotificationCompat.Builder
- SampleApp can send position to server.
- SampleApp offline mode (IndexedDB)

#### Removed
- Android unnecessary plugins
- Docs: removing instructions to enable cordova geolocation in foreground
 and user accept location services

### [0.5.0] - 2015-10-10
#### Changed
- Android FusedLocationService
- Android package names reverted
- Android configuration refactored
- WP8 merged improvements

#### Removed
- Android unused classes
- All removing deprecated url, params, headers

### [0.4.3] - 2015-10-09
#### Added
- Android Add icon color parameter

#### Changed
- Changed the plugin.xml dependencies to the new NPM-based plugin syntax
- updated SampleApp

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
