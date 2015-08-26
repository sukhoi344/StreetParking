package chau.streetparking.datamodels.parse;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

/**
 * Created by Chau Thai on 8/25/15.
 */
@ParseClassName("ParkingLot")
public class ParkingLot extends ParseObject {
    public static final String KEY_OWNER = "owner";
    public static final String KEY_CAPACITY = "capacity";
    public static final String KEY_IN_USE = "inUse";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_PRICE = "price";
    public static final String KEY_PRICE_TYPE = "priceType";
    public static final String KEY_PHOTOS = "photos";
    public static final String KEY_INFO = "info";
    public static final String KEY_NAME = "name";
    public static final String KEY_ADDRESS = "address";

    public static abstract class PriceType {
        public static final String HOURLY = "hourly";
        public static final String DAILY = "daily";
        public static final String MONTHLY = "monthly";
    }

    public User getOwner() {
        return (User) getParseObject(KEY_OWNER);
    }

    public void setOwner(User owner) {
        put(KEY_OWNER, owner);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
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

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(KEY_LOCATION);
    }

    public void setLocation(ParseGeoPoint parseGeoPoint) {
        put(KEY_LOCATION, parseGeoPoint);
    }

    public String getAddress() {
        return getString(KEY_ADDRESS);
    }

    public void setAddress(String address) {
        put(KEY_ADDRESS, address);
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

    public void setPriceType(String priceType) {
        put(KEY_PRICE_TYPE, priceType);
    }

    public String getInfo() {
        return getString(KEY_INFO);
    }

    public void setInfo(String info) {
        put(KEY_INFO, info);
    }

    public List<ParseFile> getPhotos() {
        return getList(KEY_PHOTOS);
    }

    public void setPhotos(List<ParseFile> photos) {
        put(KEY_PHOTOS, photos);
    }

}
