//
//  Config.m
//  CDVBackgroundGeolocation
//
//  Created by Marian Hello on 11/06/16.
//

#import <Foundation/Foundation.h>
#import "Config.h"

#define isNull(value) (value == nil || value == (id)[NSNull null])
#define isNotNull(value) (value != nil && value != (id)[NSNull null])

@implementation Config

@synthesize stationaryRadius, distanceFilter, distanceFilterCalculationAlgorithm, desiredAccuracy, isDebugging, activityType, stopOnTerminate, url, syncUrl, syncThreshold, httpHeaders, saveBatteryOnBackground, maxLocations, pauseLocationUpdates;

-(id) init {
    self = [super init];

    if (self == nil) {
        return self;
    }

    stationaryRadius = 50;
    distanceFilter = 500;
	distanceFilterCalculationAlgorithm = HALF_SPEED_SQUARED;
    desiredAccuracy = 100;
    isDebugging = NO;
    activityType = @"OTHER";
    stopOnTerminate = NO;
    saveBatteryOnBackground = YES;
    maxLocations = 10000;
    syncThreshold = 100;
    pauseLocationUpdates = YES;

    return self;
}

+(instancetype) fromDictionary:(NSDictionary*)config
{
    Config *instance = [[Config alloc] init];

    if (isNotNull(config[@"stationaryRadius"])) {
        instance.stationaryRadius = [config[@"stationaryRadius"] integerValue];
    }
    if (isNotNull(config[@"distanceFilter"])) {
        instance.distanceFilter = [config[@"distanceFilter"] integerValue];
    }
    if (isNotNull(config[@"distanceFilterCalculationAlgorithm"])) {
        instance.distanceFilterCalculationAlgorithm = [config[@"distanceFilterCalculationAlgorithm"] unsignedIntegerValue];
    }
    if (isNotNull(config[@"desiredAccuracy"])) {
        instance.desiredAccuracy = [config[@"desiredAccuracy"] integerValue];
    }
    if (isNotNull(config[@"debug"])) {
        instance.isDebugging = [config[@"debug"] boolValue];
    }
    if (isNotNull(config[@"activityType"])) {
        instance.activityType = config[@"activityType"];
    }
    if (isNotNull(config[@"stopOnTerminate"])) {
        instance.stopOnTerminate = [config[@"stopOnTerminate"] boolValue];
    }
    if (isNotNull(config[@"url"])) {
        instance.url = config[@"url"];
    }
    if (isNotNull(config[@"syncUrl"])) {
        instance.syncUrl = config[@"syncUrl"];
    } else if (isNull(config[@"url"])) {
        instance.syncUrl = config[@"url"];
    }
    if (isNotNull(config[@"syncThreshold"])) {
        instance.syncThreshold = [config[@"syncThreshold"] integerValue];
    }
    if (isNotNull(config[@"httpHeaders"])) {
        instance.httpHeaders = config[@"httpHeaders"];
    }
    if (isNotNull(config[@"saveBatteryOnBackground"])) {
        instance.saveBatteryOnBackground = [config[@"saveBatteryOnBackground"] boolValue];
    }
    if (isNotNull(config[@"maxLocations"])) {
        instance.maxLocations = [config[@"maxLocations"] integerValue];
    }
    if (isNotNull(config[@"pauseLocationUpdates"])) {
        instance.pauseLocationUpdates = [config[@"pauseLocationUpdates"] boolValue];
    }

    return instance;
}

- (BOOL) hasUrl
{
    return (url != nil && url.length > 0);
}

- (BOOL) hasSyncUrl
{
    return (syncUrl != nil && syncUrl.length > 0);
}

- (CLActivityType) decodeActivityType
{
    if ([activityType caseInsensitiveCompare:@"AutomotiveNavigation"]) {
        return CLActivityTypeAutomotiveNavigation;
    }
    if ([activityType caseInsensitiveCompare:@"OtherNavigation"]) {
        return CLActivityTypeOtherNavigation;
    }
    if ([activityType caseInsensitiveCompare:@"Fitness"]) {
        return CLActivityTypeFitness;
    }

    return CLActivityTypeOther;
}

- (NSInteger) decodeDesiredAccuracy
{
    if (desiredAccuracy >= 1000) {
        return kCLLocationAccuracyKilometer;
    }
    if (desiredAccuracy >= 100) {
        return kCLLocationAccuracyHundredMeters;
    }
    if (desiredAccuracy >= 10) {
        return kCLLocationAccuracyNearestTenMeters;
    }
    if (desiredAccuracy >= 0) {
        return kCLLocationAccuracyBest;
    }

    return kCLLocationAccuracyHundredMeters;
}

- (NSString *) description
{
    return [NSString stringWithFormat:@"Config: distanceFilter=%ld stationaryRadius=%ld desiredAccuracy=%ld activityType=%@ isDebugging=%d stopOnTerminate=%d url=%@ httpHeaders=%@ pauseLocationUpdates=%d", (long)distanceFilter, (long)stationaryRadius, (long)desiredAccuracy, activityType, isDebugging, stopOnTerminate, url, httpHeaders, pauseLocationUpdates];
}


@end
