package chau.streetparking.backend;

import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.HashMap;

/**
 * Created by Chau Thai on 9/9/15.
 */
public class FoursquareManager {
    private static String clientSecretKey;

    public static boolean isSecretKeyReady() {
        return clientSecretKey != null;
    }

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
                            clientSecretKey = secretKey;

                        onSecretKeyReadyListener.onSecretKeyReady(secretKey, e);
                    }
                });
    }

    public static String getSecretKey() {
        if (isSecretKeyReady())
            return clientSecretKey;

        try {
            String key = ParseCloud.callFunction(RequestManager.Foursquare.GET_CLIENT_SECRET_KEY,
                            new HashMap<String, Object>());
            if (key != null && !key.isEmpty()) {
                clientSecretKey = key;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return clientSecretKey;
    }

    public interface OnSecretKeyReadyListener {
        void onSecretKeyReady(String secretKey, ParseException e);
    }
}
