package chau.streetparking.datamodels;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * To be used in RecyclerView in {@link chau.streetparking.ui.PaymentActivity}
 * Created by Chau Thai on 6/20/2015.
 */
public class CardItem implements Parcelable {
    private long id;
    /**
     * See {@link com.google.android.gms.wallet.WalletConstants.CardNetwork}
     */
    private int network;

    /**
     * See {@link CardTypes}
     */
    private int type;
    private String number;
    private int expYear;     // Start from 1;
    private int expMonth;   // Start from 1;
    private int cvv;
    private String country;
    private String zipCode;

    public CardItem(
            long id,
            int network,
            int type,
            String number,
            int expYear,
            int expMonth,
            int cvv,
            String country,
            String zipCode) {
        this.id = id;
        this.network = network;
        this.type = type;
        this.number = number;
        this.expYear = expYear;
        this.expMonth = expMonth;
        this.cvv = cvv;
        this.country = country;
        this.zipCode = zipCode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNetwork() {
        return network;
    }

    public void setNetwork(int network) {
        this.network = network;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getExpYear() {
        return expYear;
    }

    public void setExpYear(int expYear) {
        this.expYear = expYear;
    }

    public int getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(int expMonth) {
        this.expMonth = expMonth;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    @Override
    public String toString() {
        return "CardItem{" +
                "id=" + id +
                ", network=" + network +
                ", type=" + type +
                ", number='" + number + '\'' +
                ", expYear=" + expYear +
                ", expMonth=" + expMonth +
                ", cvv=" + cvv +
                ", country='" + country + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }

    public static final Creator<CardItem> CREATOR = new Creator<CardItem>() {
        @Override
        public CardItem createFromParcel(Parcel source) {
            Bundle bundle = source.readBundle();

            return new CardItem(
                    bundle.getLong("id"),
                    bundle.getInt("network"),
                    bundle.getInt("type"),
                    bundle.getString("number"),
                    bundle.getInt("year"),
                    bundle.getInt("month"),
                    bundle.getInt("cvv"),
                    bundle.getString("country"),
                    bundle.getString("zipCode")
            );
        }

        @Override
        public CardItem[] newArray(int size) {
            return new CardItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();

        bundle.putLong("id", id);
        bundle.putInt("network", network);
        bundle.putInt("type", type);
        bundle.putString("number", number);
        bundle.putInt("year", expYear);
        bundle.putInt("month", expMonth);
        bundle.putInt("cvv", cvv);
        bundle.putString("country", country);
        bundle.putString("zipCode", zipCode);

        dest.writeBundle(bundle);
    }
}
