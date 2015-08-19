package chau.streetparking.datamodels.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Chau Thai on 8/19/15.
 */

// TODO: change the class name from "Credit" to something else

@ParseClassName("Credit")
public class Credit extends ParseObject {
    private static final String KEY_USER = "user";
    private static final String KEY_CUSTOMER_ID = "customerId";

    public User getUser() {
        return (User) getParseObject(KEY_USER);
    }

    public String getCustomerId() {
        return getString(KEY_CUSTOMER_ID);
    }
}
