package chau.streetparking.datamodels.foursquare;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Chau Thai on 9/9/15.
 */
public class Venue {
    private String id;
    private String name;
    private Location location;
    private Category[] categories;

    public Venue(String id, String name, Location location, Category[] categories) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Category[] getCategories() {
        return categories;
    }

    public void setCategories(Category[] categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "Venue{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location=" + location +
                ", categories=" + Arrays.toString(categories) +
                '}';
    }

    public static Venue fromJSON(JSONObject jsonObject) throws JSONException {
        String id = jsonObject.getString("id");
        String name = jsonObject.getString("name");

        return null;

    }

    public static Venue fromJSON(String jsonString) throws JSONException {
        return fromJSON(new JSONObject(jsonString));
    }
}
