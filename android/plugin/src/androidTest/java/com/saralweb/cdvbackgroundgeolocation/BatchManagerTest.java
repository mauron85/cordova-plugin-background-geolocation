package com.saralweb.cdvbackgroundgeolocation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.JsonReader;

import com.saralweb.bgloc.data.BackgroundLocation;
import com.saralweb.bgloc.data.sqlite.SQLiteLocationDAO;
import com.saralweb.bgloc.data.sqlite.SQLiteOpenHelper;
import com.saralweb.bgloc.sync.BatchManager;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by finch on 22/07/16.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class BatchManagerTest {
    @Before
    public void deleteDatabase() {
        Context ctx = InstrumentationRegistry.getTargetContext();
        ctx.deleteDatabase(SQLiteOpenHelper.SQLITE_DATABASE_NAME);
    }

    private List<BackgroundLocation> readLocationsArray(JsonReader reader) throws IOException {
        List<BackgroundLocation> locations = new ArrayList<BackgroundLocation>();
        reader.beginArray();
        while (reader.hasNext()) {
            BackgroundLocation l = new BackgroundLocation();
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("time")) {
                    l.setTime(reader.nextLong());
                } else if (name.equals("latitude")) {
                    l.setLatitude(reader.nextDouble());
                } else if (name.equals("longitude")) {
                    l.setLongitude(reader.nextDouble());
                } else if (name.equals("accuracy")) {
                    l.setAccuracy((float)reader.nextDouble());
                } else if (name.equals("speed")) {
                    l.setSpeed((float)reader.nextDouble());
                } else if (name.equals("bearing")) {
                    l.setBearing((float)reader.nextDouble());
                } else if (name.equals("altitude")) {
                    l.setAltitude(reader.nextDouble());
                } else if (name.equals("radius")) {
                    l.setRadius((float)reader.nextDouble());
                } else if (name.equals("provider")) {
                    l.setProvider(reader.nextString());
                } else if (name.equals("locationProvider")) {
                    l.setLocationProvider(reader.nextInt());
                }
            }
            reader.endObject();
            locations.add(l);
        }
        reader.endArray();
        return locations;
    }

    @Test
    public void createBatch() {
        Context ctx = InstrumentationRegistry.getTargetContext();
        SQLiteDatabase db = new SQLiteOpenHelper(ctx).getWritableDatabase();
        SQLiteLocationDAO dao = new SQLiteLocationDAO(db);

        int i = 1;
        BackgroundLocation location;
        for (int j = i; j < 100; j++) {
            location = new BackgroundLocation();
            location.setTime(1000 + i);
            location.setLatitude(40.21 + i);
            location.setLongitude(23.45 + i);
            location.setAccuracy(200 +i);
            location.setAltitude(900 +i);
            location.setSpeed(20 + i);
            location.setBearing(2 + i);
            location.setProvider("test");
            location.setLocationProvider(1);
            if ((j % 3) == 0) {
                location.setBatchStartMillis(1000L);
            } else if ((j % 2) == 0) {
                location.setValid(false);
            } else {
                i++;
            }
            dao.persistLocation(location);
        }

        List<BackgroundLocation> locations = null;
        BatchManager batchManager = new BatchManager(ctx);
        try {
            File batchFile = batchManager.createBatch(1000L);
            JsonReader reader = new JsonReader(new FileReader(batchFile));
            locations = readLocationsArray(reader);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(33, locations.size());
        i = 1;
        for (BackgroundLocation l : locations) {
            Assert.assertEquals(200 + i, l.getAccuracy(), 0);
            Assert.assertEquals(900 + i, l.getAltitude(), 0);
            Assert.assertEquals(2 + i, l.getBearing(), 0);
            Assert.assertEquals(40.21 + i, l.getLatitude(), 0);
            Assert.assertEquals(23.45 + i,l.getLongitude(), 0);
            Assert.assertEquals(20 + i, l.getSpeed(), 0);
            Assert.assertEquals("test", l.getProvider());
            Assert.assertEquals(1000 + i, l.getTime(), 0);
            i++;
        }
    }

    @Test
    public void setBatchCompleted() {
        Context ctx = InstrumentationRegistry.getTargetContext();
        SQLiteDatabase db = new SQLiteOpenHelper(ctx).getWritableDatabase();
        SQLiteLocationDAO dao = new SQLiteLocationDAO(db);

        int i = 1;
        BackgroundLocation location;
        for (int j = i; j < 100; j++) {
            location = new BackgroundLocation();
            location.setTime(1000 + i);
            location.setLatitude(40.21 + i);
            location.setLongitude(23.45 + i);
            location.setAccuracy(200 +i);
            location.setAltitude(900 +i);
            location.setSpeed(20 + i);
            location.setBearing(2 + i);
            location.setProvider("test");
            location.setLocationProvider(1);
            if ((j % 99) == 0) {
                location.setBatchStartMillis(1000L);
            } else if ((j % 2) == 0) {
                location.setValid(false);
            } else {
                i++;
            }
            dao.persistLocation(location);
        }

        // distribution of location: 49 of invalid, (49 + 1 in sync) of valid, 1 in sync

        Assert.assertEquals(99, dao.getAllLocations().size());
        Assert.assertEquals(50, dao.getValidLocations().size());
        Assert.assertEquals(Long.valueOf(49), dao.locationsForSyncCount(1000L));
        BatchManager batchManager = new BatchManager(ctx);
        try {
            batchManager.createBatch(1000L);
            batchManager.setBatchCompleted(1000L);
            Assert.assertEquals(0, dao.getValidLocations().size());
            Assert.assertEquals(Long.valueOf(0), dao.locationsForSyncCount(2000L));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
