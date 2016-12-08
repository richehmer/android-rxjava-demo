package com.ehmer.usa.persistance.prefs;

import android.content.SharedPreferences;

import java.util.Set;

public class StringSetPreference {
    protected final String key;
    private final SharedPreferences preferences;
    private final Set<String> defaultValue;

    public StringSetPreference(SharedPreferences preferences, String key) {
        this(preferences, key, null);
    }

    public StringSetPreference(SharedPreferences preferences, String key, Set<String> defaultValue) {
        this.preferences = preferences;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public Set<String> get() {
        return preferences.getStringSet(key, defaultValue);
    }

    public boolean isSet() {
        return preferences.contains(key);
    }

    public void set(Set<String> value) {
        preferences.edit().putStringSet(key, value).apply();
    }

    public void setNow(Set<String> value) {
        preferences.edit().putStringSet(key, value).commit();
    }

    public void delete() {
        preferences.edit().remove(key).apply();
    }
}
