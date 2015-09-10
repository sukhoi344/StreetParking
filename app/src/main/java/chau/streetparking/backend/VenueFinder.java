package chau.streetparking.backend;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import chau.streetparking.VolleySingleton;
import chau.streetparking.datamodels.foursquare.Venue;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 9/9/15.
 */
public class VenueFinder {
    private static final String TAG = VenueFinder.class.getSimpleName();
    private static final int DEFAULT_RADIUS = 200;

    private Context context;
    private int limit = 20;

    public interface OnSearchDoneListener {
        void onSearchDone(int code, String requestId, List<Venue> venues);
        void onSearchError(int code, String errorType, String errorDetail);
    }

    public VenueFinder(Context context) {
        this.context = context;
    }

    public void find(double latitude, double longitude, OnSearchDoneListener onSearchDoneListener) {
        find(latitude, longitude, DEFAULT_RADIUS, onSearchDoneListener);
    }

    public void find(final double latitude, final double longitude, final int radius,
                     final OnSearchDoneListener onSearchDoneListener) {
        if (onSearchDoneListener == null)
            return;

        FoursquareManager.getAuthParamUrlAsync(context, new FoursquareManager.OnAuthParamReadyListener() {
            @Override
            public void onAuthParamReady(String authParam, Exception e) {
                if (e != null || authParam == null || authParam.isEmpty()) {
                    onSearchDoneListener.onSearchError(-1, "Auth error", "Cannot authenticate the server");
                } else {
                    final String URL = getUrl(authParam, latitude, longitude, radius);
                    StringRequest request = new StringRequest(Request.Method.GET, URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    handleReponse(onSearchDoneListener, response);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    onSearchDoneListener.onSearchError(error.networkResponse.statusCode,
                                            "Network error", "Cannot connect to the server");
                                    Logger.printStackTrace(error);
                                }
                            });

                    VolleySingleton.getInstance(context).addToRequestQueue(request);
                }
            }
        });
    }

    private String getUrl(String authParam, double latitude, double longitude, int radius) {
        String url = RequestManager.Foursquare.Search.URL
                + "?" + RequestManager.Foursquare.Search.PARAM_LATLNG + "="
                + latitude + "," + longitude
                + "&" + RequestManager.Foursquare.Search.PARAM_INTENT + "="
                + RequestManager.Foursquare.Search.INTENT_BROWSE
                + "&" + RequestManager.Foursquare.Search.PARAM_RADIUS + "=" + radius
                + "&" + RequestManager.Foursquare.Search.PARAM_LIMIT + "=" + limit
                + authParam
                + RequestManager.Foursquare.VERSIONING_PARAMS;

        return url;
    }

    private void handleReponse(OnSearchDoneListener onSearchDoneListener, String response) {
        if (onSearchDoneListener == null)
            return;

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject metaObject = jsonObject.getJSONObject("meta");

            int code = metaObject.getInt("code");

            if (metaObject.has("errorType")) {
                String errorType = metaObject.getString("errorType");
                String errorDetail = metaObject.getString("errorDetail");

                onSearchDoneListener.onSearchError(code, errorType, errorDetail);
            } else {
                String requestId = metaObject.getString("requestId");
                List<Venue> venues = new ArrayList<>();

                JSONObject responseObject = jsonObject.getJSONObject("response");
                JSONArray venuesArray = responseObject.getJSONArray("venues");

                for (int i = 0; i < venuesArray.length(); i++) {
                    venues.add(Venue.fromJSON(venuesArray.getJSONObject(i)));
                }

                onSearchDoneListener.onSearchDone(code, requestId, venues);
            }
        } catch (Exception e) {
            Logger.printStackTrace(e);
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

}
