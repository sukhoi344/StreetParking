package chau.streetparking.backend;

import android.app.Activity;
import android.os.Process;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.stripe.Stripe;
import com.stripe.model.Card;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 8/19/15.
 */

//TODO: Create notification listener for changing API KEY event in the backend
public class StripeHelper {
    private static final String TAG = StripeHelper.class.getSimpleName();

    /**
     * Set LIVE to true to enable LIVE mode for Stripe
     */
    private static final boolean LIVE = false;

    public interface GetCardsCallBack {
        void done(List<Card> cards, String errorMessage);
    }

    /**
     * Get {@link Card} objects from Stripe customerId. This always runs on background Thread.
     * The callback always runs on UI thread
     * @param activity current activity
     * @param customerId Stripe customer ID
     * @param callBack Callback when the call is done. It will run on UI Thread
     */
    public static void getCardsFromCustomer(final Activity activity, final String customerId, final GetCardsCallBack callBack) {
        if (activity == null)
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

                try {
                    // Get the secret Stripe apikey from the server
                    final String apiKey = getStripeAPIKey();

                    if (apiKey == null) {
                        if (activity != null && callBack != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.done(null, "Cannot connect to the server");
                                }
                            });
                        }

                        return;
                    }

                    // Get customer from Parse server
                    Map<String, Object> params = new HashMap<>();
                    params.put("customerId", customerId);
                    String customerString = ParseCloud.callFunction("getCustomer", params);

                    // Decode the response json and call the callback function
                    decodeCustomerJSON(customerString, callBack, activity);
                } catch (Exception e) {
                    handleExeption(e, callBack, activity);
                }
            }
        }).start();
    }

    public static String getStripeAPIKey() {
        if (Stripe.apiKey == null || Stripe.apiKey.isEmpty()) {
            try {
                String functionName = LIVE ? RequestManager.Stripe.GET_SECRET_KEY_LIVE :
                        RequestManager.Stripe.GET_SECRET_KEY_TEST;
                String apiKey = ParseCloud.callFunction(functionName, new HashMap<String, Object>());

                Stripe.apiKey = apiKey;

                return apiKey;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return Stripe.apiKey;
    }

    public static void getStripeApiKeyAsync(FunctionCallback<String> functionCallback) {
        if (Stripe.apiKey == null || Stripe.apiKey.isEmpty()) {
            String functionName = LIVE ? RequestManager.Stripe.GET_SECRET_KEY_LIVE :
                    RequestManager.Stripe.GET_SECRET_KEY_TEST;

            ParseCloud.callFunctionInBackground(functionName, new HashMap<String, Object>(), functionCallback);
        } else {
            functionCallback.done(Stripe.apiKey, null);
        }
    }

    private static void decodeCustomerJSON(String customerString, final GetCardsCallBack callBack, Activity activity) {
        CustomerWithCards customerWithCards = JsonHelper.jsonToCustomerWithCards(customerString);
        final List<Card> cards = customerWithCards.getCards();

        if (callBack != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        callBack.done(cards, null);
                    } catch (Exception e) {
                        if (Logger.DEBUG)
                            e.printStackTrace();
                    }
                }
            });
        }
    }

    private static void handleExeption(Exception e, final GetCardsCallBack callBack, Activity activity) {
        if (Logger.DEBUG)
            e.printStackTrace();

        if (callBack != null && activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        callBack.done(null, "Unknown error");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
