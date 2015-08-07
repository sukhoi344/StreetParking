package chau.streetparking.backend;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.net.APIResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chau.streetparking.R;
import chau.streetparking.datamodels.parse.User;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 7/26/2015.
 */
public class BackendTest {
    private static final String TAG = "BackendTest";
    private Context context;

    public BackendTest(Context context) {
        this.context = context;
    }


    public void testParse() {
        HashMap<String, Object> params = new HashMap<>();
        ParseCloud.callFunctionInBackground("hello2", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {
                Logger.d(TAG, "Parse response: " + o.toString());
            }
        });


//        ParseObject testObject = new ParseObject("TestObject");
//        testObject.put("foo", "bar");
//        testObject.saveInBackground();

    }

    public void testUser() {
        User user = new User();
        user.setUsername("sukhoi344@yahoo.com");
        user.setPassword("123456");
        user.setEmail("sukhoi344@yahoo.com");
        user.setAvatar("myAvatar");
        user.setMobile("2023301969");
        user.setFirstName("Chau");
        user.setLastName("Thai");

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                Logger.d(TAG, "sign-up done");

                if (e != null) {
                    Logger.d(TAG, "sign-up error: " + e.getLocalizedMessage());
                }
            }
        });
    }

    public void testStripe(Card card) {
        try {
            Stripe stripe = new Stripe(context.getString(R.string.stripe_test_publishable_key));
            stripe.createToken(card, new TokenCallback() {
                @Override
                public void onError(Exception e) {
                    Logger.d(TAG, "Error: " + e.getLocalizedMessage());
                }

                @Override
                public void onSuccess(Token token) {
                    Logger.d(TAG, "Got the token, now charging...");
                    saveCustomer(token, "sukhoi344@yahoo.com");
//                    charge(token);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error linking payment", Toast.LENGTH_SHORT).show();
        }
    }

    private void charge(Token token) {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", 100); // 100 cents
        params.put("currency", "usd");
        params.put("card", token.getId());

//        params.put("customer", "cus_6k37Tt5tqY7fDy");
//        params.put("card", "card_16WZ0lF9d2GPjMKMEsiFWNxc");

        ParseCloud.callFunctionInBackground("charge", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {
                if (o != null) {
                    Logger.d(TAG, "success");

                    Charge charge = JsonHelper.jsonToCharge(o.toString());

                    if (charge != null) {
                        Logger.d(TAG, "charge: " + charge.toString());
                    } else {
                        Logger.d(TAG, "json error");
                    }

                } else if (e != null) {
                    Logger.d(TAG, "error: " + e.getLocalizedMessage());
                } else {
                    Logger.d(TAG, "unknown error");
                }
            }
        });
    }

    private void saveCustomer(Token token, String email) {
        Map<String, Object> params = new HashMap<>();
        params.put("stripeToken", token.getId());
        params.put("email", email);

        ParseCloud.callFunctionInBackground("createCustomer", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object response, ParseException e) {
                if (response != null) {
                    Customer customer = JsonHelper.jsonToCustomer(response.toString());

                    if (customer != null) {
                        Logger.d(TAG, "customer: " + customer.toString());
                    } else {
                        Logger.d(TAG, "json error");
                    }

                } else if (e != null) {
                    Logger.d(TAG, "error: " + e.getLocalizedMessage());
                } else {
                    Logger.d(TAG, "unknown error");
                }
            }
        });
    }



}
