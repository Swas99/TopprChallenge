package com.archer.thought_works_got.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by deb on 12/17/2015.
 */
public class SharedPreferenceManager {

    public SharedPreferences sharedpreferences = null;
    private static final String SHARED_PREF_NAMESPACE = "user_pref";
    private static SharedPreferenceManager sharedPreferenceManager;

    private final String EMPTY_STRING = "";

    private SharedPreferenceManager(Context context) {
        sharedpreferences = context.getSharedPreferences(SHARED_PREF_NAMESPACE,
                Context.MODE_PRIVATE);
    }

    public static SharedPreferenceManager getInstance(Context context) {
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferenceManager(context);
        }
        return sharedPreferenceManager;
    }

    public void saveKeyData(String key,String data) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, String.valueOf(data)).apply();
    }
    public String getKeyData(String key) {
        return sharedpreferences.getString(key,EMPTY_STRING);
    }

    public void deleteKey(String id) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(id);
        editor.apply();
    }

}