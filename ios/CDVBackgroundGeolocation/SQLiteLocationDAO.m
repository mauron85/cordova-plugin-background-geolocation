//
//  SQLiteLocationDAO.m
//  CDVBackgroundGeolocation
//
//  Created by Marian Hello on 10/06/16.
//

#import "sqlite3.h"
#import "SQLiteLocationDAO.h"
#import "BackgroundLocation.h"

#define INTEGER_TYPE    " INTEGER"
#define REAL_TYPE       " REAL"
#define TEXT_TYPE       " TEXT"
#define COMMA_SEP       ","

#define DATABASE_FILENAME               "cordova_bg_geolocation.db"
#define TABLE_NAME                      "location"
#define COLUMN_NAME_ID                  "id"
#define COLUMN_NAME_NULLABLE            "NULLHACK"
#define COLUMN_NAME_TIME                "time"
#define COLUMN_NAME_ACCURACY            "accuracy"
#define COLUMN_NAME_SPEED               "speed"
#define COLUMN_NAME_BEARING             "bearing"
#define COLUMN_NAME_ALTITUDE            "altitude"
#define COLUMN_NAME_LATITUDE            "latitude"
#define COLUMN_NAME_LONGITUDE           "longitude"
#define COLUMN_NAME_PROVIDER            "provider"
#define COLUMN_NAME_LOCATION_PROVIDER   "service_provider"
#define COLUMN_NAME_DEBUG               "debug"

@implementation SQLiteLocationDAO

sqlite3* database;
NSString *databasePath;

#pragma mark Singleton Methods

+ (instancetype) sharedInstance
{
    static SQLiteLocationDAO *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    
    return instance;
}

- (id) init {
    if (self = [super init]) {
        databasePath = [self getDatabasePath];
        if (sqlite3_open_v2([databasePath UTF8String], &database, SQLITE_OPEN_CREATE|SQLITE_OPEN_READWRITE|SQLITE_OPEN_FULLMUTEX, NULL) != SQLITE_OK) {
            sqlite3_close(database);
        } else {
            [self prepareDatabase];
        }
    }
    return self;
}

- (NSArray<BackgroundLocation*>*) getAllLocations
{
    NSMutableArray* locations = [[NSMutableArray alloc] init];

    NSString *sql = @"SELECT " \
    @COLUMN_NAME_ID @COMMA_SEP \
    @COLUMN_NAME_TIME @COMMA_SEP \
    @COLUMN_NAME_ACCURACY @COMMA_SEP \
    @COLUMN_NAME_SPEED @COMMA_SEP \
    @COLUMN_NAME_BEARING @COMMA_SEP \
    @COLUMN_NAME_ALTITUDE @COMMA_SEP \
    @COLUMN_NAME_LATITUDE @COMMA_SEP \
    @COLUMN_NAME_LONGITUDE @COMMA_SEP \
    @COLUMN_NAME_PROVIDER @COMMA_SEP \
    @COLUMN_NAME_LOCATION_PROVIDER @COMMA_SEP \
    @COLUMN_NAME_DEBUG
    @" FROM " @TABLE_NAME @" ORDER BY " @COLUMN_NAME_ID;
    
    sqlite3_stmt *stmt;
    if (sqlite3_prepare_v2(database, [sql UTF8String], -1, &stmt, NULL) == SQLITE_OK) {
        while(sqlite3_step(stmt) == SQLITE_ROW) {
            BackgroundLocation *location = [[BackgroundLocation alloc] init];
            location.id = [NSNumber numberWithDouble:(double)sqlite3_column_double(stmt, 0)];
            NSTimeInterval timestamp = (double)sqlite3_column_double(stmt, 1);
            location.time = [NSDate dateWithTimeIntervalSince1970:timestamp];
            location.accuracy = [NSNumber numberWithDouble:(double)sqlite3_column_double(stmt, 2)];
            location.speed = [NSNumber numberWithDouble:(double)sqlite3_column_double(stmt, 3)];
            location.heading = [NSNumber numberWithDouble:(double)sqlite3_column_double(stmt, 4)];
            location.altitude = [NSNumber numberWithDouble:(double)sqlite3_column_double(stmt, 5)];
            location.latitude = [NSNumber numberWithDouble:(double)sqlite3_column_double(stmt, 6)];
            location.longitude = [NSNumber numberWithDouble:(double)sqlite3_column_double(stmt, 7)];
            const char *provider = (char *)sqlite3_column_text(stmt, 8);
            if (provider) {
                location.provider = [NSString stringWithUTF8String:(char *)provider];
            }
            location.service_provider = [NSNumber numberWithInt:(int)sqlite3_column_int(stmt, 9)];
            location.debug = sqlite3_column_int(stmt, 10) != 0;
            
            [locations addObject:location];
        }
    } else {
        NSLog(@"Retrieving locations failed code: %d: message: %s", sqlite3_errcode(database), sqlite3_errmsg(database));
    }
    sqlite3_finalize(stmt);

    return locations;
}

- (NSNumber*) persistLocation:(BackgroundLocation*)location
{
    NSNumber* locationId = nil;

    NSString *sql = @"INSERT INTO " @TABLE_NAME @" ("\
    @COLUMN_NAME_TIME @COMMA_SEP \
    @COLUMN_NAME_ACCURACY @COMMA_SEP \
    @COLUMN_NAME_SPEED @COMMA_SEP \
    @COLUMN_NAME_BEARING @COMMA_SEP \
    @COLUMN_NAME_ALTITUDE @COMMA_SEP \
    @COLUMN_NAME_LATITUDE @COMMA_SEP \
    @COLUMN_NAME_LONGITUDE @COMMA_SEP \
    @COLUMN_NAME_PROVIDER @COMMA_SEP \
    @COLUMN_NAME_LOCATION_PROVIDER @COMMA_SEP \
    @COLUMN_NAME_DEBUG \
    @") VALUES (?,?,?,?,?,?,?,?,?,?)";
    
    sqlite3_stmt *stmt;
    if (sqlite3_prepare_v2(database, [sql UTF8String], -1, &stmt, NULL) == SQLITE_OK) {
        sqlite3_bind_double(stmt, 1, [[NSNumber numberWithDouble:[location.time timeIntervalSince1970]] doubleValue]);
        sqlite3_bind_double(stmt, 2, [location.accuracy doubleValue]);
        sqlite3_bind_double(stmt, 3, [location.speed doubleValue]);
        sqlite3_bind_double(stmt, 4, [location.heading doubleValue]);
        sqlite3_bind_double(stmt, 5, [location.altitude doubleValue]);
        sqlite3_bind_double(stmt, 6, [location.latitude doubleValue]);
        sqlite3_bind_double(stmt, 7, [location.longitude doubleValue]);
        sqlite3_bind_text(stmt, 8, [location.provider UTF8String], -1, SQLITE_TRANSIENT);
        sqlite3_bind_double(stmt, 9, [location.service_provider doubleValue]);
        sqlite3_bind_int(stmt, 10, location.debug ? 1 : 0);
        
        if (SQLITE_DONE == sqlite3_step(stmt)) {
            locationId = [NSNumber numberWithLongLong:sqlite3_last_insert_rowid(database)];
        } else {
             NSLog(@"Error while inserting data. '%s'", sqlite3_errmsg(database));
        }
    } else {
        NSLog(@"Inserting location %@ failed code: %d: message: %s", location.time, sqlite3_errcode(database), sqlite3_errmsg(database));
    }
    sqlite3_finalize(stmt);

    return locationId;
}

- (BOOL) deleteLocation:(NSNumber*)locationId
{
    BOOL result = NO;

    NSString *sql = @"DELETE FROM " @TABLE_NAME @" WHERE " @COLUMN_NAME_ID @" = ?";
    
    sqlite3_stmt *stmt;
    if (sqlite3_prepare_v2(database, [sql UTF8String], -1, &stmt, NULL) == SQLITE_OK) {
        sqlite3_bind_int64(stmt, 1, [locationId longLongValue]);
        result = SQLITE_DONE == sqlite3_step(stmt);
    } else {
        NSLog(@"Delete location %@ failed code: %d: message: %s", locationId, sqlite3_errcode(database), sqlite3_errmsg(database));
    }
    sqlite3_finalize(stmt);
    
    return result;
}

- (BOOL) deleteAllLocations
{
    char *error;
    NSString *sql = @"DELETE FROM " @TABLE_NAME;

    if (sqlite3_exec(database, [sql UTF8String], NULL, NULL, &error) != SQLITE_OK) {
        NSLog(@"Deleting all location failed: %s", error);
    }
    
    return YES;
}

-(NSString *) getDatabasePath
{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, YES);
    NSString *libraryDirectory = [paths objectAtIndex:0];
    NSString *databasePath = [[NSString alloc]initWithString:[libraryDirectory stringByAppendingPathComponent:@DATABASE_FILENAME]];

    return databasePath;
}


-(BOOL) prepareDatabase
{
    NSLog(@"Prepare database: %@", @DATABASE_FILENAME);
    
    NSString *sql = @"CREATE TABLE IF NOT EXISTS " @TABLE_NAME @" (" \
    @COLUMN_NAME_ID @" INTEGER PRIMARY KEY AUTOINCREMENT" @COMMA_SEP \
    @COLUMN_NAME_TIME @INTEGER_TYPE @COMMA_SEP \
    @COLUMN_NAME_ACCURACY @REAL_TYPE @COMMA_SEP \
    @COLUMN_NAME_SPEED @REAL_TYPE @COMMA_SEP \
    @COLUMN_NAME_BEARING @REAL_TYPE @COMMA_SEP \
    @COLUMN_NAME_ALTITUDE @REAL_TYPE @COMMA_SEP \
    @COLUMN_NAME_LATITUDE @REAL_TYPE @COMMA_SEP \
    @COLUMN_NAME_LONGITUDE @REAL_TYPE @COMMA_SEP \
    @COLUMN_NAME_PROVIDER @TEXT_TYPE @COMMA_SEP \
    @COLUMN_NAME_LOCATION_PROVIDER @INTEGER_TYPE @COMMA_SEP \
    @COLUMN_NAME_DEBUG @INTEGER_TYPE \
    @" )";
    
    char *error;
    if (sqlite3_exec(database, [sql UTF8String], NULL, NULL,  &error) != SQLITE_OK) {
        // we're doomed
        NSLog(@"Creation of locations table failed: %s", error);
        return NO;
    }

    return YES;
}

- (BOOL) clearDatabase
{
    NSLog(@"Opening database: %@", @DATABASE_FILENAME);
    
    char *error;
    NSString *sql = @"DROP TABLE " @TABLE_NAME;
    if (sqlite3_exec(database, [sql UTF8String], NULL, NULL,  &error) != SQLITE_OK) {
        NSLog(@"Drop of table %@ failed: %s", @TABLE_NAME, error);
        return NO;
    }
    [self prepareDatabase];
    
    return YES;
}

- (void) dealloc {
    // Should never be called, but just here for clarity really.
    sqlite3_close(database);
}

@end
