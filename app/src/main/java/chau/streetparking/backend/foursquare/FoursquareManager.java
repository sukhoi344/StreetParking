package chau.streetparking.backend.foursquare;

import android.app.Application;
import android.content.Context;
import android.os.*;
import android.os.Process;

import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.HashMap;

import chau.streetparking.R;
import chau.streetparking.backend.RequestManager;

/**
 * Created by Chau Thai on 9/9/15.
 */
public class FoursquareManager {
    private static String clientSecretKey;

    /**
     * Check if the secret key is ready to use
     * @return true if ready, false otherwise
     */
    public static boolean isSecretKeyReady() {
        return clientSecretKey != null;
    }

    /**
     * Get the secret key asynchronously from the server, or from cached memory.
     * @param onSecretKeyReadyListener will be called on after done retrieving the key
     */
    public static void getSecretKeyAsync(final OnSecretKeyReadyListener onSecretKeyReadyListener) {
        if (onSecretKeyReadyListener == null)
            return;

        if (isSecretKeyReady()) {
            onSecretKeyReadyListener.onSecretKeyReady(clientSecretKey, null);
            return;
        }

        ParseCloud.callFunctionInBackground(
                RequestManager.Foursquare.GET_CLIENT_SECRET_KEY,
                new HashMap<String, Object>(),
                new FunctionCallback<String>() {
                    @Override
                    public void done(String secretKey, ParseException e) {
                        if (e == null && secretKey != null && !secretKey.isEmpty())
                            setClientSecretKey(secretKey);

                        onSecretKeyReadyListener.onSecretKeyReady(secretKey, e);
                    }
                });
    }

    /**
     * Get the secret key from the memory cache if available, or from the server.
     * This call must be called outside the UI thread.
     * @return the secret key, null if fail to retrieve
     */
    public static String getSecretKey() {
        if (isSecretKeyReady())
            return clientSecretKey;

        try {
            String key = ParseCloud.callFunction(RequestManager.Foursquare.GET_CLIENT_SECRET_KEY,
                            new HashMap<String, Object>());
            if (key != null && !key.isEmpty()) {
                setClientSecretKey(key);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return clientSecretKey;
    }

    /**
     * Get the auth params (with the clientID and clientSecret params) to be embedded into
     * a request url.
     * The call must be called outside the UI thread.
     * @return auth param
     */
    public static String getAuthParamUrl(Context context) {
        String authParam = "&client_id=" + context.getString(R.string.foursquare_client_id)
                + "&client_secret=" + getSecretKey();
        return authParam;
    }

    /**
     *  Asynchronously get the auth params (with the clientID and clientSecret params) to
     *  be embedded into a request url.
     * @param listener will be called after done building the auth param url
     */
    public static void getAuthParamUrlAsync(Context context, final OnAuthParamReadyListener listener) {
        if (listener == null)
            return;

        final String clientId = context.getString(R.string.foursquare_client_id);

        getSecretKeyAsync(new OnSecretKeyReadyListener() {
            @Override
            public void onSecretKeyReady(String secretKey, Exception e) {
                if (e != null) {
                    listener.onAuthParamReady(null, e);
                } else if (secretKey == null || secretKey.isEmpty()) {
                    listener.onAuthParamReady(null, new Exception("Unknown error"));
                } else {
                    String authParam = "&client_id=" + clientId + "&client_secret=" + secretKey;
                    listener.onAuthParamReady(authParam, null);
                }
            }
        });
    }

    /**
     * Try to get the client secret key if possible. This can be called in
     * {@link Application#onCreate()}
     * @param context
     */
    public static void initKeyIfPossible(Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                setClientSecretKey(getSecretKey());
            }
        }).start();
    }

    private synchronized static void setClientSecretKey(String key) {
        if (key != null && !key.isEmpty())
            clientSecretKey = key;
    }

    public interface OnSecretKeyReadyListener {
        void onSecretKeyReady(String secretKey, Exception e);
    }

    public interface OnAuthParamReadyListener {
        void onAuthParamReady(String authParam, Exception e);
    }
}
