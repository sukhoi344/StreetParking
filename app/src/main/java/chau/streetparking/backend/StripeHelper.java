package chau.streetparking.backend;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.*;
import android.os.Process;

import com.parse.ParseCloud;
import com.stripe.model.Card;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCardCollection;

import java.util.HashMap;
import java.util.List;

import chau.streetparking.R;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 8/19/15.
 */
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
                    String apiKey = getStripeAPIKey();

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

                    // Get customer from Stripe server
                    Customer customer = Customer.retrieve(customerId, apiKey);
                    Logger.d(TAG, "customer: " + customer == null? "null" : Customer.PRETTY_PRINT_GSON.toJson(customer));

                    CustomerCardCollection cardCollection = customer.getCards();
                    final List<Card> cards = cardCollection.getData();

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
                } catch (Exception e) {
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
        }).start();
    }

    public static String getStripeAPIKey() {
        try {
            String functionName = LIVE ? "get_stripe_secret_key_live" : "get_stripe_secret_key_test";
            String apiKey = ParseCloud.callFunction(functionName, new HashMap<String, Object>());

            return apiKey;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
