package com.saralweb.bgloc.data;

import android.content.Context;

import com.saralweb.bgloc.data.sqlite.SQLiteLocationDAO;
import com.saralweb.bgloc.data.sqlite.SQLiteConfigurationDAO;

public abstract class DAOFactory {
    public static LocationDAO createLocationDAO(Context context) {
        return new SQLiteLocationDAO(context);
    }

    public static ConfigurationDAO createConfigurationDAO(Context context) {
        return new SQLiteConfigurationDAO(context);
    }
}
