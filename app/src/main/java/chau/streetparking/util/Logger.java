package chau.streetparking.util;

import android.util.Log;

/**
 * Created by Chau Thai on 7/27/15.
 */
public class Logger {
    public static final boolean DEBUG = true;

    public static void d(String TAG, String message) {
        if (DEBUG)
            Log.d(TAG, message);
    }

    public static void e(String TAG, String message) {
        if (DEBUG)
            Log.e(TAG, message);
    }
}
