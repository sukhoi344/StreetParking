package chau.streetparking.datamodels.parse;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

/**
 * Created by Chau Thai on 8/25/15.
 */

@ParseClassName("Request")
public class Request extends ParseObject {
    private static final String KEY_USER = "user";
    private static final String KEY_TO = "to";
    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_END_TIME = "endTime";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_RADIUS = "radius";
    private static final String KEY_STATUS = "status";

    public User getUser() {
        return (User) getParseObject(KEY_USER);
    }

    public void setUser(User user) {
        put(KEY_USER, user);
    }

    public List<ParkingLot> getTo() {
        return getList(KEY_TO);
    }

    public void setTo(List<ParkingLot> parkingLots) {
        put(KEY_TO, parkingLots);
    }

    public Date getStartTime() {
        return getDate(KEY_START_TIME);
    }

    public void setStartTime(Date date) {
        put(KEY_START_TIME, date);
    }

    public Date getEndTime() {
        return getDate(KEY_END_TIME);
    }

    public void setEndTime(Date date) {
        put(KEY_END_TIME, date);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }

    public void setLocation(ParseGeoPoint parseGeoPoint) {
        put(KEY_LOCATION, parseGeoPoint);
    }

    public int getRadius() {
        return getInt(KEY_RADIUS);
    }

    public void setRadius(int radius) {
        put(KEY_RADIUS, radius);
    }

    public String getStatus() {
        return getString(KEY_STATUS);
    }

    public void setStatus(String status) {
        put(KEY_STATUS, status);
    }

}
