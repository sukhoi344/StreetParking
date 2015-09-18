package chau.streetparking.util;

/**
 * Created by Chau Thai on 9/15/15.
 */
public class Wrapper<T> {
    private T item;

    public Wrapper() {}

    public Wrapper(T item) {
        this.item = item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }
}
