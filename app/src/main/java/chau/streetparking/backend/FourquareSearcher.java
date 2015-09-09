package chau.streetparking.backend;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;

import chau.streetparking.VolleySingleton;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 9/9/15.
 */
public class FourquareSearcher {

    public static void test(final Context context, final double latitude, final double longitude) {
        FoursquareManager.getAuthParamUrlAsync(context, new FoursquareManager.OnAuthParamReadyListener() {
            @Override
            public void onAuthParamReady(String authParam, Exception e) {
                if (e != null) {
                    Logger.printStackTrace(e);
                } else if (authParam == null) {
                    Logger.d("yolo", "error: null auth params");
                } else {
                    final String URL = RequestManager.Foursquare.Search.URL
                            + "?" + RequestManager.Foursquare.Search.PARAM_LATLNG + "="
                            + latitude + "," + longitude
                            + authParam
                            + RequestManager.Foursquare.VERSIONING_PARAMS;

                    Logger.d("yolo", "requestURL: " + URL);

                    StringRequest request = new StringRequest(Request.Method.GET, URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Logger.d("yolo", "response: " + response);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Logger.printStackTrace(error);
                                }
                            });


                    VolleySingleton.getInstance(context).addToRequestQueue(request);
                }
            }
        });

    }
}
