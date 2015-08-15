package chau.streetparking.backend.registration;

/**
 * Created by Chau Thai on 8/10/15.
 */
public interface ResultCallBack {
    void success(String objectId);
    void failure(String errorMessage);
}
