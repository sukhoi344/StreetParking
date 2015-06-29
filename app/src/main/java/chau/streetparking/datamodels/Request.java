package chau.streetparking.datamodels;

import android.location.Address;

/**
 * Created by Chau Thai on 6/27/2015.
 */
public class Request {
    private long id;
    private Address address;
    private String name;
    private int radius;
    private String from;
    private String to;

    public Request(long id, Address address, String name, int radius, String from, String to) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.radius = radius;
        this.from = from;
        this.to = to;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", address=" + address +
                ", name='" + name + '\'' +
                ", radius=" + radius +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
