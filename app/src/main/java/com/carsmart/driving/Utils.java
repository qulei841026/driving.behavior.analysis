package com.carsmart.driving;

import android.content.Context;
import android.preference.PreferenceManager;

public class Utils {

    public static String getString(Context context, String key, String defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defValue);
    }
    
}

