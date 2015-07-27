package chau.streetparking.datamodels;

import java.util.Date;

/**
 * Created by Chau Thai on 7/25/2015.
 */
public abstract class BackendObject {
    protected String    objectId;
    protected Date      created;
    protected Date      updated;

    /**
     * Default constructor which is required by Backendless
     */
    public BackendObject() {}

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
