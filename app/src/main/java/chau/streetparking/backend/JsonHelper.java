package chau.streetparking.backend;

import com.stripe.model.Card;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCardCollection;
import com.stripe.model.StripeColllectionAPIResource;

import org.json.JSONObject;

import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 8/4/15.
 */
public class JsonHelper {
    private static final String TAG = JsonHelper.class.getSimpleName();

    /**
     * Convert json string to {@link Charge} object
     * @param jsonString input string
     * @return {@link Charge} object, null if fails.
     */
    public static Charge jsonToCharge(String jsonString) {
        if (jsonString == null)
            return null;

        try {
            Charge charge = Charge.GSON.fromJson(jsonString, Charge.class);

            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONObject cardJson = jsonObject.getJSONObject("source");

                Card card = Card.GSON.fromJson(cardJson.toString(), Card.class);
                charge.setCard(card);
            } catch (Exception ignore) {}

            return charge;
        } catch (Exception e) {
            if (Logger.DEBUG)
                e.printStackTrace();
        }

        return null;
    }

    /**
     * Convert json string to {@link Customer} object
     * @param jsonString input string
     * @return {@link Customer} object, null if fails
     */
    public static Customer jsonToCustomer(String jsonString) {
        try {
            Customer customer = Customer.GSON.fromJson(jsonString, Customer.class);
            return customer;

        } catch (Exception e) {
            Logger.printStackTrace(e);
        }

        return null;
    }


    public static CustomerWithCards jsonToCustomerWithCards(String jsonString) {
        try {
            long t1 = System.currentTimeMillis();
            Customer customer = jsonToCustomer(jsonString);
            CustomerCardCollection cardCollection = getCustomerCardCollection(jsonString);

            long delta = System.currentTimeMillis() - t1;

            Logger.d(TAG, "total json time: " + delta + "ms");

            if (cardCollection != null)
                return new CustomerWithCards(customer, cardCollection.getData());

            return new CustomerWithCards(customer, null);
        } catch (Exception e) {
            Logger.printStackTrace(e);
        }

        return null;
    }

    private static CustomerCardCollection getCustomerCardCollection(String jsonString) {
        if (jsonString == null)
            return null;

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.has("sources")) {
                JSONObject sources = jsonObject.getJSONObject("sources");

                CustomerCardCollection cards = CustomerCardCollection.GSON.fromJson(sources.toString(), CustomerCardCollection.class);
                return cards;
            }

        } catch (Exception e) {
            Logger.printStackTrace(e);
        }

        return null;
    }
}
