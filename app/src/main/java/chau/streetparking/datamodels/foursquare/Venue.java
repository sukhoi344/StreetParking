package chau.streetparking.datamodels.foursquare;

import org.json.JSONArray;
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
    private boolean verified;

    public Venue(
            String id,
            String name,
            Location location,
            Category[] categories,
            boolean verified)
    {
        this.id = id;
        this.name = name;
        this.location = location;
        this.categories = categories;
        this.verified = verified;
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

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Override
    public String toString() {
        return "Venue{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location=" + location +
                ", categories=" + Arrays.toString(categories) +
                ", verified=" + verified +
                '}';
    }

    public static Venue fromJSON(JSONObject jsonObject) throws JSONException {
        String id = null;
        String name = null;
        Location location = null;
        Category[] categories = null;
        boolean verified = false;

        if (jsonObject.has("id"))
            id = jsonObject.getString("id");
        if (jsonObject.has("name"))
            name = jsonObject.getString("name");
        if (jsonObject.has("location"))
            location = Location.fromJSON(jsonObject.getJSONObject("location"));

        if (jsonObject.has("categories")) {
            JSONArray jsonArray = jsonObject.getJSONArray("categories");
            categories = new Category[jsonArray.length()];

            for (int i = 0; i < categories.length; i++) {
                categories[i] = Category.fromJSON(jsonArray.getJSONObject(i));
            }
        }

        if (jsonObject.has("verified"))
            verified = jsonObject.getBoolean("verified");

        return new Venue(
                id,
                name,
                location,
                categories,
                verified
        );
    }

    public static Venue fromJSON(String jsonString) throws JSONException {
        return fromJSON(new JSONObject(jsonString));
    }
}
