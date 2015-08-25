package chau.streetparking.datamodels.parse;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.Date;

/**
 * Created by Chau Thai on 8/25/15.
 */
@ParseClassName("ParkingLot")
public class ParkingLot extends ParseObject {
    private static final String KEY_OWNER = "owner";
    private static final String KEY_CAPACITY = "capacity";
    private static final String KEY_IN_USE = "inUse";
    private static final String KEY_AVAILABLE_START_TIME = "availableStartTime";
    private static final String KEY_AVAILABLE_END_TIME = "availableEndTime";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_PRICE = "price";
    private static final String KEY_PRICE_TYPE = "priceType";

    public User getOwner() {
        return (User) getParseObject(KEY_OWNER);
    }

    public void setOwner(User owner) {
        put(KEY_OWNER, owner);
    }

    public int getCapacity() {
        return getInt(KEY_CAPACITY);
    }

    public void setCapacity(int capacity) {
        put(KEY_CAPACITY, capacity);
    }

    public int getInUse() {
        return getInt(KEY_IN_USE);
    }

    public void setInUse(int inUse) {
        put(KEY_IN_USE, inUse);
    }

    public Date getAvailableStartTime() {
        return getDate(KEY_AVAILABLE_START_TIME);
    }

    public void setAvailableStartTime(Date date) {
        put(KEY_AVAILABLE_START_TIME, date);
    }

    public Date getAvailableEndTime() {
        return getDate(KEY_AVAILABLE_END_TIME);
    }

    public void setAvailableEndTime(Date date) {
        put(KEY_AVAILABLE_END_TIME, date);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }

    public void setLocation(ParseGeoPoint parseGeoPoint) {
        put(KEY_LOCATION, parseGeoPoint);
    }

    public double getPrice() {
        return getDouble(KEY_PRICE);
    }

    public void setPrice(double price) {
        put(KEY_PRICE, price);
    }

    public String getPriceType() {
        return getString(KEY_PRICE_TYPE);
    }

    public void setKeyPriceType(String priceType) {
        put(KEY_PRICE_TYPE, priceType);
    }
}
