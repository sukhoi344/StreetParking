package chau.streetparking.ui.curtain;

import chau.streetparking.R;
import chau.streetparking.ui.ColoredBarActivity;

/**
 * Created by idgmi_dc on 6/19/15.
 */
public class PaymentActivity extends ColoredBarActivity {

    @Override
    protected int getLayout() {
        return R.layout.payment_activity;
    }

    @Override
    protected String getTitleToolbar() {
        return "Payment";
    }
}
