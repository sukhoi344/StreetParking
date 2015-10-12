package chau.streetparking.ui.map;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Date;

import chau.streetparking.R;
import chau.streetparking.datamodels.parse.ParkingLot;
import chau.streetparking.util.Logger;
import chau.streetparking.util.ParkingUtil;

/**
 * Created by Chau Thai on 9/26/2015.
 */
public class ParkingDetailDisplayer {
    private Activity activity;
    private GoogleMap map;
    private SlidingUpPanelLayout panel;

    // Widgets
    private TextView tvPrice;
    private TextView tvAddress;
    private TextView tvType;
    private TextView tvItemAbout;
    private TextView tvItemPrice;
    private TextView tvItemInfo;

    private ParkingLot currentParkingLot;

    public ParkingDetailDisplayer(Activity activity, GoogleMap map, SlidingUpPanelLayout panel) {
        this.activity = activity;
        this.map = map;
        this.panel = panel;

        getWidgets();
        close();
    }

    public void display(ParkingLot parkingLot, Date startDate, Date endDate) {
        if (parkingLot == null)
            return;

        try {
            currentParkingLot = parkingLot;
            setupHeader(parkingLot, startDate, endDate);
            setupItems(parkingLot);

            // Show the panel
            panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } catch (Exception e) {
            Logger.printStackTrace(e);
        }
    }

    public void close() {
        panel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    public void setDates(Date startDate, Date endDate) {
        if (currentParkingLot != null) {
            showPrice(currentParkingLot, startDate, endDate);
        }
    }

    private void setupHeader(ParkingLot parkingLot, Date startDate, Date endDate) {
        // Set price
        showPrice(parkingLot, startDate, endDate);

        // Set address
        tvAddress.setText(parkingLot.getAddress());
    }

    private void setupItems(ParkingLot parkingLot) {
        String textPrice = "$" + parkingLot.getPrice();
        String priceType = parkingLot.getPriceType();

        switch (priceType) {
            case ParkingLot.PriceType.HOURLY:
                textPrice += " per Hour";
                break;
            case ParkingLot.PriceType.MONTHLY:
                textPrice += " per Month";
                break;
            case ParkingLot.PriceType.DAILY:
                textPrice += " per Day";
                break;
            default:
                textPrice += " per Hour";
                break;
        }


        tvItemPrice.setText(textPrice);

        if (parkingLot.getInfo() != null && !parkingLot.getInfo().isEmpty()) {
            tvItemInfo.setText(parkingLot.getInfo());
        }

        String textAbout = "<b>Garage Name:</b> " + parkingLot.getName() + "<br><br>"
                + "<b>Address</b>: " + parkingLot.getAddress() + "<br><br>"
                + "<b>Capacity</b>: " + parkingLot.getCapacity();
        tvItemAbout.setText(Html.fromHtml(textAbout));
    }

    private void showPrice(ParkingLot parkingLot, Date startDate, Date endDate) {
        int totalPrice = ParkingUtil.getPrice(parkingLot, startDate, endDate);
        String text = "$" + totalPrice;
        tvPrice.setText(text);
    }

    private void getWidgets() {
        View container = activity.findViewById(R.id.parking_detail_layout_include);
        tvPrice = (TextView) container.findViewById(R.id.tv_price);
        tvAddress = (TextView) container.findViewById(R.id.tv_address);
        tvType = (TextView) container.findViewById(R.id.tv_name_type);
        tvItemAbout = (TextView) container.findViewById(R.id.tv_item_about);
        tvItemInfo = (TextView) container.findViewById(R.id.tv_item_info);
        tvItemPrice = (TextView) container.findViewById(R.id.tv_item_price);
    }
}
