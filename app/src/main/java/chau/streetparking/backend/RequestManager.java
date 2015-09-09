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

    public static abstract class Foursquare {
        /**
         * /**
         * @see <a href="https://developer.foursquare.com/overview/versioning">
         *     https://developer.foursquare.com/overview/versioning</a>
         */
        public static final String V = "20150909";

        /**
         * /**
         * @see <a href="https://developer.foursquare.com/overview/versioning">
         *     https://developer.foursquare.com/overview/versioning</a>
         */
        public static final String M = "foursquare";

        public static final String GET_CLIENT_SECRET_KEY = "getFourSquareKey";
        public static final String API_BASE_URL = "https://api.foursquare.com/v2/";
        public static final String VERSIONING_PARAMS = "&v=" + V + "&m=" + M;

        public static abstract class Search {
            public static final String URL = API_BASE_URL + "venues/search";
            public static final String PARAM_LATLNG = "ll";
            public static final String PARAM_LIMIT = "limit";
        }

        public static final String SEARCH_VALUE_URL = API_BASE_URL + "venues/search";
    }
}
