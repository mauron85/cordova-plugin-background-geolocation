//
//  Config.h
//  CDVBackgroundGeolocation
//
//  Created by Marian Hello on 11/06/16.
//

#ifndef Config_h
#define Config_h

#import <CoreLocation/CoreLocation.h>

@interface Config : NSObject

enum BGDistanceFilterCalculationAlgorithm {
    NONE = 0,
    HALF_SPEED_SQUARED = 1 //(rounded-speed-to-nearest-5) / 2)^2
};

typedef NSUInteger BGDistanceFilterCalculationAlgorithm;


@property NSInteger stationaryRadius;
@property NSInteger distanceFilter;
@property BGDistanceFilterCalculationAlgorithm distanceFilterCalculationAlgorithm;
@property NSInteger desiredAccuracy;
@property BOOL isDebugging;
@property NSString* activityType;
@property BOOL stopOnTerminate;
@property NSString* url;
@property NSString* syncUrl;
@property NSInteger syncThreshold;
@property NSMutableDictionary* httpHeaders;
@property BOOL saveBatteryOnBackground;
@property NSInteger maxLocations;
@property BOOL pauseLocationUpdates;

+ (instancetype) fromDictionary:(NSDictionary*)config;
- (CLActivityType) decodeActivityType;
- (NSInteger) decodeDesiredAccuracy;
- (BOOL) hasUrl;
- (BOOL) hasSyncUrl;

@end;

#endif /* Config_h */
