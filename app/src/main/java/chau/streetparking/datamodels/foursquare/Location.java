package chau.streetparking.datamodels.foursquare;

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
    private String[] formattedAdress;

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
            String[] formattedAdress) {
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
        this.formattedAdress = formattedAdress;
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

    public String[] getFormattedAdress() {
        return formattedAdress;
    }

    public void setFormattedAdress(String[] formattedAdress) {
        this.formattedAdress = formattedAdress;
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
                ", formattedAdress=" + Arrays.toString(formattedAdress) +
                '}';
    }

    public static Location fromJSON(JSONObject jsonObject) throws JSONException {
        String id = jsonObject.getString("id");
        String name = jsonObject.getString("name");
        String pluralName = jsonObject.getString("pluralName");
        String shortName = jsonObject.getString("shortName");

        return null;
    }
}
