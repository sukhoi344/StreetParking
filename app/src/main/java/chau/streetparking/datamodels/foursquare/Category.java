package chau.streetparking.datamodels.foursquare;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Chau Thai on 9/9/15.
 */
public class Category {
    private String id;
    private String name;
    private String pluralName;
    private String shortName;
    private String iconPrefix;
    private String iconSuffix;
    private boolean primary;

    public Category(String id,
                    String name,
                    String pluralName,
                    String shortName,
                    String iconPrefix,
                    String iconSuffix,
                    boolean primary)
    {
        this.id = id;
        this.name = name;
        this.pluralName = pluralName;
        this.shortName = shortName;
        this.iconPrefix = iconPrefix;
        this.iconSuffix = iconSuffix;
        this.primary = primary;
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

    public String getPluralName() {
        return pluralName;
    }

    public void setPluralName(String pluralName) {
        this.pluralName = pluralName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getIconPrefix() {
        return iconPrefix;
    }

    public void setIconPrefix(String iconPrefix) {
        this.iconPrefix = iconPrefix;
    }

    public String getIconSuffix() {
        return iconSuffix;
    }

    public void setIconSuffix(String iconSuffix) {
        this.iconSuffix = iconSuffix;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", pluralName='" + pluralName + '\'' +
                ", shortName='" + shortName + '\'' +
                ", iconPrefix='" + iconPrefix + '\'' +
                ", iconSuffix='" + iconSuffix + '\'' +
                ", primary=" + primary +
                '}';
    }

    public static Category fromJSON(JSONObject jsonObject) throws JSONException {
        String id = null;
        String name = null;
        String pluralName = null;
        String shortName = null;
        String iconPrefix = null;
        String iconSuffix = null;

        if (jsonObject.has("id"))
            id = jsonObject.getString("id");
        if (jsonObject.has("name"))
            name = jsonObject.getString("name");
        if (jsonObject.has("pluralName"))
            pluralName = jsonObject.getString("pluralName");
        if (jsonObject.has("shortName"))
            shortName = jsonObject.getString("shortName");

        if (jsonObject.has("icon")) {
            JSONObject iconObject = jsonObject.getJSONObject("icon");
            iconPrefix = iconObject.getString("prefix");
            iconSuffix = iconObject.getString("suffix");
        }

        boolean primary = jsonObject.getBoolean("primary");

        return new Category(
                id,
                name,
                pluralName,
                shortName,
                iconPrefix,
                iconSuffix,
                primary
        );
    }
}
