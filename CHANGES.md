## Changelog

### [2.4.0] - 2017-1-06
### Fix
- iOS bugs #324, #294
###Add
- iOS LOCATION_ALWAYS_AND_WHEN_IN_USE_USAGE_DESCRIPTION in plugin.xml to support background location tracking in iOS-11

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
