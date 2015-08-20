package chau.streetparking.backend;

import com.stripe.model.Card;
import com.stripe.model.Customer;

import java.util.List;

/**
 * Created by Chau Thai on 8/20/15.
 */
public class CustomerWithCards {
    private Customer customer;
    private List<Card> cards;

    public CustomerWithCards(Customer customer, List<Card> cards) {
        this.customer = customer;
        this.cards = cards;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}
