package com.ehmer.usa.persistance.prefs;

import android.content.SharedPreferences;

public class StringPreference {
    protected final String key;
    private final SharedPreferences preferences;
    private final String defaultValue;

    public StringPreference(SharedPreferences preferences, String key) {
        this(preferences, key, null);
    }

    public StringPreference(SharedPreferences preferences, String key, String defaultValue) {
        this.preferences = preferences;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String get() {
        return preferences.getString(key, defaultValue);
    }

    public boolean isSet() {
        return preferences.contains(key);
    }

    public void set(String value) {
        preferences.edit().putString(key, value).apply();
    }

    public void setNow(String value) {
        preferences.edit().putString(key, value).commit();
    }

    public void delete() {
        preferences.edit().remove(key).apply();
    }
}
