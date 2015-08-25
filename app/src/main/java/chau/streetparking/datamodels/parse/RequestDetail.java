package chau.streetparking.datamodels.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Chau Thai on 8/25/15.
 */

@ParseClassName("RequestDetail")
public class RequestDetail extends ParseObject {
    private static final String KEY_REQUEST = "request";
    private static final String KEY_FROM = "from";
    private static final String KEY_TO = "to";
    private static final String KEY_STATUS = "status";

    public Request getRequest() {
        return (Request) getParseObject(KEY_REQUEST);
    }

    public void setRequest(Request request) {
        put(KEY_REQUEST, request);
    }

    public User getFrom() {
        return (User) getParseObject(KEY_FROM);
    }

    public void setFrom(User from) {
        put(KEY_FROM, from);
    }

    public User getTo() {
        return (User) getParseObject(KEY_TO);
    }

    public void setTo(User to) {
        put(KEY_FROM, to);
    }

    public String getStatus() {
        return getString(KEY_STATUS);
    }

    public void setStatus(String status) {
        put(KEY_STATUS, status);
    }
}
