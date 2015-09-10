package chau.streetparking.datamodels.foursquare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Chau Thai on 9/9/15.
 */
public class Location {
    private String address;
    private String crossStreet;
    private double latitude;
    private double longitude;
    private int distance;
    private String postalCode;
    private String cc;
    private String city;
    private String state;
    private String country;
    private String[] formattedAddress;

    public Location(
            String address,
            String crossStreet,
            double latitude,
            double longitude,
            int distance,
            String postalCode,
            String cc,
            String city,
            String state,
            String country,
            String[] formattedAddress) {
        this.address = address;
        this.crossStreet = crossStreet;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.postalCode = postalCode;
        this.cc = cc;
        this.city = city;
        this.state = state;
        this.country = country;
        this.formattedAddress = formattedAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCrossStreet() {
        return crossStreet;
    }

    public void setCrossStreet(String crossStreet) {
        this.crossStreet = crossStreet;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String[] getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String[] formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    @Override
    public String toString() {
        return "Location{" +
                "address='" + address + '\'' +
                ", crossStreet='" + crossStreet + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", distance=" + distance +
                ", postalCode='" + postalCode + '\'' +
                ", cc='" + cc + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", formattedAddress=" + Arrays.toString(formattedAddress) +
                '}';
    }

    public static Location fromJSON(JSONObject jsonObject) throws JSONException {
        String address = null;
        String crossStreet = null;
        double latitude = 0;
        double longitude = 0;
        int distance = 0;
        String postalCode = null;
        String cc = null;
        String city = null;
        String state = null;
        String country = null;
        String[] formattedAddress = null;

        if (jsonObject.has("address"))
            address = jsonObject.getString("address");
        if (jsonObject.has("crossStreet"))
            crossStreet = jsonObject.getString("crossStreet");
        if (jsonObject.has("lat"))
            latitude = jsonObject.getDouble("lat");
        if (jsonObject.has("lng"))
            longitude = jsonObject.getDouble("lng");
        if (jsonObject.has("distance"))
            distance = jsonObject.getInt("distance");
        if (jsonObject.has("postalCode"))
            postalCode = jsonObject.getString("postalCode");
        if (jsonObject.has("cc"))
            cc = jsonObject.getString("cc");
        if (jsonObject.has("city"))
            city = jsonObject.getString("city");
        if (jsonObject.has("state"))
            state = jsonObject.getString("state");
        if (jsonObject.has("country"))
            country = jsonObject.getString("country");
        formattedAddress = null;

        if (jsonObject.has("formattedAddress")) {
            JSONArray jsonArray = jsonObject.getJSONArray("formattedAddress");
            formattedAddress = new String[jsonArray.length()];

            for (int i = 0; i < formattedAddress.length; i++) {
                formattedAddress[i] = jsonArray.getString(i);
            }
        }

        return new Location(
                address,
                crossStreet,
                latitude,
                longitude,
                distance,
                postalCode,
                cc,
                city,
                state,
                country,
                formattedAddress
        );
    }
}
