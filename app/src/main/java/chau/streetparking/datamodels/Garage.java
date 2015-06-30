package chau.streetparking.datamodels;

import android.location.Address;

/**
 * Created by Chau Thai on 6/21/2015.
 */
public class Garage {
    public static final int PRICE_TYPE_HOURLY = 0;
    public static final int PRICE_TYPE_DAYLY = 1;
    public static final int PRICE_TYPE_MONTHLY = 2;

    private String name;
    private Address address;
    private int capacity;
    private int currentAvailable;
    private double price;
    private int priceType;

    public Garage(String name,
                  Address address,
                  int capacity,
                  int currentAvailable,
                  double price,
                  int priceType) {
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.currentAvailable = currentAvailable;
        this.price = price;
        this.priceType = priceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCurrentAvailable() {
        return currentAvailable;
    }

    public void setCurrentAvailable(int currentAvailable) {
        this.currentAvailable = currentAvailable;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getPriceType() {
        return priceType;
    }

    public void setPriceType(int priceType) {
        this.priceType = priceType;
    }

    @Override
    public String toString() {
        return "Garage{" +
                "name='" + name + '\'' +
                ", address=" + address +
                ", capacity=" + capacity +
                ", currentAvailable=" + currentAvailable +
                ", price=" + price +
                ", priceType=" + priceType +
                '}';
    }
}
