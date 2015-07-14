package com.devspark.metro.util;

import android.content.SharedPreferences;

public class IntPreference {
    private final SharedPreferences mPreferences;
    private final String mKey;
    private final int mDefaultValue;

    public IntPreference(SharedPreferences preferences, String key) {
        this(preferences, key, 0);
    }

    public IntPreference(SharedPreferences preferences, String key, int defaultValue) {
        mPreferences = preferences;
        mKey = key;
        mDefaultValue = defaultValue;
    }

    public int get() {
        return mPreferences.getInt(mKey, mDefaultValue);
    }

    public boolean isSet() {
        return mPreferences.contains(mKey);
    }

    public void set(int value) {
        mPreferences.edit().putInt(mKey, value).apply();
    }

    public void delete() {
        mPreferences.edit().remove(mKey).apply();
    }
}