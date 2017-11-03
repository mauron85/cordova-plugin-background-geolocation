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


#import "CDVBackgroundGeolocation.h"
#import "Config.h"
#import "Logging.h"

@implementation CDVBackgroundGeolocation {
    FMDBLogger *sqliteLogger;
    NSString* syncCallbackId;
    NSString* locationModeCallbackId;
    NSMutableArray* stationaryRegionListeners;
    LocationManager* manager;
}

- (void)pluginInitialize
{
    [DDLog addLogger:[DDASLLogger sharedInstance] withLevel:DDLogLevelInfo];
    [DDLog addLogger:[DDTTYLogger sharedInstance] withLevel:DDLogLevelDebug];

    sqliteLogger = [[FMDBLogger alloc] initWithLogDirectory:[self loggerDirectory]];
    sqliteLogger.saveThreshold     = 1;
    sqliteLogger.saveInterval      = 0;
    sqliteLogger.maxAge            = 60 * 60 * 24 * 7; //  7 days
    sqliteLogger.deleteInterval    = 60 * 60 * 24;     //  1 day
    sqliteLogger.deleteOnEverySave = NO;
    
    [DDLog addLogger:sqliteLogger withLevel:DDLogLevelDebug];

    manager = [[LocationManager alloc] init];
    manager.delegate = self;

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPause:) name:UIApplicationDidEnterBackgroundNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onResume:) name:UIApplicationWillEnterForegroundNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onFinishLaunching:) name:UIApplicationDidFinishLaunchingNotification object:nil];
}

- (NSString *)loggerDirectory
{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES);
    NSString *basePath = ([paths count] > 0) ? [paths objectAtIndex:0] : NSTemporaryDirectory();
    
    return [basePath stringByAppendingPathComponent:@"SQLiteLogger"];
}

/**
 * configure plugin
 * @param {Number} stationaryRadius
 * @param {Number} distanceFilter
 * @param {Number} locationTimeout
 */
- (void) configure:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        Config* config = [Config fromDictionary:[command.arguments objectAtIndex:0]];
        syncCallbackId = command.callbackId;
        
        NSError *error = nil;
        CDVPluginResult* result = nil;
        if (![manager configure:config error:&error]) {
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Configuration error"];
            [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        }
    }];
}

/**
 * Turn on background geolocation
 * in case of failure it calls error callback from configure method
 * may fire two callback when location services are disabled and when authorization failed
 */
- (void) start:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSError *error = nil;
        CDVPluginResult* result = nil;

        [manager start:&error];
        if (error == nil) {
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        } else {
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:[error userInfo]];
        }
        
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    }];
}

/**
 * Turn it off
 */
- (void) stop:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSError *error = nil;
        CDVPluginResult* result = nil;
        if ([manager stop:&error]) {
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        } else {
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:[error userInfo]];
        }
        
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    }];
}

/**
 * Change 
 * @param {Number} operation mode BACKGROUND/FOREGROUND
 */
- (void) switchMode:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        BGOperationMode mode = [[command.arguments objectAtIndex: 0] intValue];
        [manager switchMode:mode];
    }];
}

- (void) addStationaryRegionListener:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        if (stationaryRegionListeners == nil) {
            stationaryRegionListeners = [[NSMutableArray alloc] init];
        }
        [stationaryRegionListeners addObject:command.callbackId];
        NSMutableDictionary* stationaryLocation = [manager getStationaryLocation];
        if (stationaryLocation != nil) {
            // TODO: do this in background thread
            CDVPluginResult* result =[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:stationaryLocation];
            [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        }
    }];
}

/**
 * Fetches current stationaryLocation
 */
- (void) getStationaryLocation:(CDVInvokedUrlCommand *)command
{
    [self.commandDelegate runInBackground:^{
        CDVPluginResult* result = nil;

        NSMutableDictionary* stationaryLocation = [manager getStationaryLocation];
        if (stationaryLocation) {
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:stationaryLocation];
        } else {
            result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:NO];
        }

        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    }];
}

- (void) isLocationEnabled:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        BOOL isLocationEnabled = [manager isLocationEnabled];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:isLocationEnabled];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    }];
}

- (void) showAppSettings:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        [manager showAppSettings];
    }];
}

- (void) showLocationSettings:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        [manager showLocationSettings];
    }];
}

- (void) watchLocationMode:(CDVInvokedUrlCommand*)command
{
    locationModeCallbackId = command.callbackId;
}

- (void) stopWatchingLocationMode:(CDVInvokedUrlCommand*)command
{
    locationModeCallbackId = nil;
}

- (void) getLocations:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSArray *locations = [manager getLocations];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:locations];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    }];
}

- (void) getValidLocations:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSArray *locations = [manager getValidLocations];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:locations];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    }];
}

- (void) deleteLocation:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        int locationId = [[command.arguments objectAtIndex: 0] intValue];
        [manager deleteLocation:[[NSNumber alloc] initWithInt:locationId]];
    }];
}

- (void) deleteAllLocations:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        [manager deleteAllLocations];
    }];
}

/**
 * Called by js to signify the end of a background-geolocation event
 */
-(void) finish:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        [manager finish];
    }];
}

- (void) getLogEntries:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSInteger limit = [command.arguments objectAtIndex: 0] == [NSNull null]
            ? 0 : [[command.arguments objectAtIndex: 0] integerValue];
        NSString *path = [[self loggerDirectory] stringByAppendingPathComponent:@"log.sqlite"];
        NSArray *logs = [LogReader getEntries:path limit:limit];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:logs];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    }];
}


/**@
 * Resume.  Turn background off
 */
-(void) onResume:(NSNotification *)notification
{
    DDLogDebug(@"CDVBackgroundGeoLocation resumed");
    [manager switchMode:FOREGROUND];
}

-(void) onPause:(NSNotification *)notification
{
    DDLogDebug(@"CDVBackgroundGeoLocation paused");
    [manager switchMode:BACKGROUND];
}

/**@
 * on UIApplicationDidFinishLaunchingNotification
 */
-(void) onFinishLaunching:(NSNotification *)notification
{
    NSDictionary *dict = [notification userInfo];
    
    if ([dict objectForKey:UIApplicationLaunchOptionsLocationKey]) {
        DDLogInfo(@"CDVBackgroundGeolocation started by system on location event.");
        [manager startMonitoringLocationWhenSuspended];
    }
}

-(void) onAppTerminate
{
    DDLogInfo(@"CDVBackgroundGeoLocation appTerminate");
    [manager onAppTerminate];
}

- (void) onAuthorizationChanged:(NSInteger)authStatus
{
    [self.commandDelegate runInBackground:^{
        if (locationModeCallbackId != nil) {
            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:(authStatus == ALLOWED) ? YES : NO];
            [result setKeepCallbackAsBool:YES];
            [self.commandDelegate sendPluginResult:result callbackId:locationModeCallbackId];
        }
    }];
}

- (void) onLocationChanged:(NSMutableDictionary*)location
{
    [self.commandDelegate runInBackground:^{
        DDLogDebug(@"CDVBackgroundGeolocation onLocationChanged");
        CDVPluginResult* result = nil;
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:location];
        [result setKeepCallbackAsBool:YES];
        [self.commandDelegate sendPluginResult:result callbackId:syncCallbackId];
    }];
}

- (void) onStationaryChanged:(NSMutableDictionary*)location
{
    [self.commandDelegate runInBackground:^{
        DDLogDebug(@"CDVBackgroundGeolocation onStationaryChanged");
        
        if (![stationaryRegionListeners count]) {
            return;
        }
        
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:location];
        [result setKeepCallbackAsBool:YES];
        for (NSString *callbackId in stationaryRegionListeners) {
            [self.commandDelegate sendPluginResult:result callbackId:callbackId];
        }
    }];
}

- (void) onError:(NSError*)error
{
    [self.commandDelegate runInBackground:^{
        DDLogError(@"CDVBackgroundGeolocation onError");
        CDVPluginResult* result = nil;
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:[error userInfo]];
        [result setKeepCallbackAsBool:YES];
        [self.commandDelegate sendPluginResult:result callbackId:syncCallbackId];
    }];
}

@end
