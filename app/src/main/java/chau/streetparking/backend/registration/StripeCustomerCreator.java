package chau.streetparking.backend.registration;

import android.content.Context;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.model.Customer;

import java.util.HashMap;
import java.util.Map;

import chau.streetparking.R;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 8/7/15.
 */
public class StripeCustomerCreator {
    private Card card;
    private Context context;
    private String email;
    private ResultCallBack resultCallback;

    public StripeCustomerCreator(Context context, Card card, String email, ResultCallBack resultCallback) {
        this.context = context;
        this.card = card;
        this.email = email;
        this.resultCallback = resultCallback;
    }

    public void createCustomer() {
        if (resultCallback == null)
            return;

        try {
            Stripe stripe = new Stripe(context.getString(R.string.stripe_test_publishable_key));
            stripe.createToken(card, new TokenCallback() {
                @Override
                public void onError(Exception e) {
                    resultCallback.failure(e.getLocalizedMessage());
                }

                @Override
                public void onSuccess(Token token) {
                    saveCustomer(token);
                }
            });


        } catch (Exception e) {
            if (Logger.DEBUG)
                e.printStackTrace();
            resultCallback.failure("Unknown error");
        }
    }


    private void saveCustomer(Token token) {
        Map<String, Object> params = new HashMap<>();
        params.put("stripeToken", token.getId());
        params.put("email", email);

        ParseCloud.callFunctionInBackground("createCustomer", params, new FunctionCallback<String>() {
            @Override
            public void done(String s, ParseException e) {
                if (e == null) {
                    Customer customer = Customer.GSON.fromJson(s, Customer.class);

                    if (customer != null) {
                        resultCallback.success(customer.getId());
                    } else {
                        resultCallback.failure("Error");
                    }

                } else {
                    resultCallback.failure(e.getLocalizedMessage());
                }
            }
        });
    }
}
