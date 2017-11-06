package com.saralweb.cdvbackgroundgeolocation;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.saralweb.bgloc.Config;
import com.saralweb.bgloc.data.sqlite.SQLiteConfigurationContract;
import com.saralweb.bgloc.data.sqlite.SQLiteConfigurationDAO;
import com.saralweb.bgloc.data.sqlite.SQLiteOpenHelper;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by finch on 13/07/16.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class SQLiteConfigurationDAOTest {
    @Before
    public void deleteDatabase() {
        Context ctx = InstrumentationRegistry.getTargetContext();
        ctx.deleteDatabase(SQLiteOpenHelper.SQLITE_DATABASE_NAME);
    }

    @Test
    public void persistConfiguration() {
        Context ctx = InstrumentationRegistry.getTargetContext();
        SQLiteDatabase db = new SQLiteOpenHelper(ctx).getWritableDatabase();
        SQLiteConfigurationDAO dao = new SQLiteConfigurationDAO(db);

        Config config = new Config();
        config.setActivitiesInterval(1000);
        config.setDesiredAccuracy(200);
        config.setDistanceFilter(300);
        config.setFastestInterval(5000);
        config.setInterval(10000);
        config.setLocationProvider(0);
        config.setMaxLocations(15000);
        config.setUrl("http://server:1234/locations");
        config.setSyncUrl("http://server:1234/syncLocations");
        config.setSyncThreshold(200);
        config.setStopOnTerminate(false);
        config.setStopOnStillActivity(false);
        config.setStationaryRadius(50);
        config.setStartOnBoot(true);
        config.setStartForeground(true);
        config.setSmallNotificationIcon("smallico");
        config.setLargeNotificationIcon("largeico");
        config.setNotificationTitle("test");
        config.setNotificationText("in progress");
        config.setNotificationIconColor("yellow");

        dao.persistConfiguration(config);
        dao.persistConfiguration(config); // try once more

        Cursor cursor = db.query(SQLiteConfigurationContract.ConfigurationEntry.TABLE_NAME, null, null, null, null, null, null);
        Assert.assertEquals(1, cursor.getCount());
        cursor.close();

        try {
            Config storedConfig = dao.retrieveConfiguration();
            Assert.assertEquals(1000, storedConfig.getActivitiesInterval().intValue());
            Assert.assertEquals(200, storedConfig.getDesiredAccuracy().intValue());
            Assert.assertEquals(300, storedConfig.getDistanceFilter().intValue());
            Assert.assertEquals(5000, storedConfig.getFastestInterval().intValue());
            Assert.assertEquals(10000, storedConfig.getInterval().intValue());
            Assert.assertEquals(0, storedConfig.getLocationProvider().intValue());
            Assert.assertEquals(15000, storedConfig.getMaxLocations().intValue());
            Assert.assertEquals("http://server:1234/locations", storedConfig.getUrl());
            Assert.assertEquals("http://server:1234/syncLocations", storedConfig.getSyncUrl());
            Assert.assertEquals(200, storedConfig.getSyncThreshold().intValue());
            Assert.assertEquals(Boolean.FALSE, storedConfig.getStopOnTerminate());
            Assert.assertEquals(Boolean.FALSE, storedConfig.getStopOnStillActivity());
            Assert.assertEquals(50, storedConfig.getStationaryRadius(), 0);
            Assert.assertEquals(Boolean.TRUE, storedConfig.getStartOnBoot());
            Assert.assertEquals(Boolean.TRUE, storedConfig.getStartForeground());
            Assert.assertEquals("smallico", storedConfig.getSmallNotificationIcon());
            Assert.assertEquals("largeico", storedConfig.getLargeNotificationIcon());
            Assert.assertEquals("test", storedConfig.getNotificationTitle());
            Assert.assertEquals("in progress", storedConfig.getNotificationText());
            Assert.assertEquals("yellow", storedConfig.getNotificationIconColor());

        } catch (JSONException e) {
            Assert.fail(e.getMessage());
        }
    }
}
