package chau.streetparking.backend;

/**
 * Created by Chau Thai on 7/30/15.
 */
public class RequestManager {
    public static abstract class RequestToken {
        public static final String NAME = "RequestToken";
        public static final String KEY_TOKEN = "token";
    }

    public static abstract class Stripe {
        public static final String GET_SECRET_KEY_TEST = "get_stripe_secret_key_test";
        public static final String GET_SECRET_KEY_LIVE = "get_stripe_secret_key_live";
        public static final String API_BASE_URL = "api.stripe.com/v1";
    }
}
