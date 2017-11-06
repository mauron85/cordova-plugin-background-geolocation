package com.saralweb.bgloc.data;

import java.util.Date;
import java.util.Collection;

import org.json.JSONException;

import com.saralweb.bgloc.Config;

public interface ConfigurationDAO {
    public boolean persistConfiguration(Config config) throws NullPointerException;
    public Config retrieveConfiguration() throws JSONException;
}
