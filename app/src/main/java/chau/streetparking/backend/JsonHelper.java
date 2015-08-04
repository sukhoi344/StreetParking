package chau.streetparking.backend;

import com.stripe.model.Charge;

import org.json.JSONObject;

import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 8/4/15.
 */
public class JsonHelper {

    /**
     * Convert json string to {@link Charge} object
     * @param jsonString input string
     * @return {@link Charge} object, null if fails.
     */
    public static Charge jsonToCharge(String jsonString) {
        if (jsonString == null)
            return null;

        try {
            // TODO: improve regex
            String fixedString = jsonString.replaceFirst(", refunds=\\{[^{}]+\\}", "");

            Charge charge = new Charge();
            JSONObject jsonObject = new JSONObject(fixedString);

            charge.setId(jsonObject.getString("id"));
            charge.setCreated(jsonObject.getLong("created"));
            charge.setLivemode(jsonObject.getBoolean("livemode"));
            charge.setPaid(jsonObject.getBoolean("paid"));
            charge.setAmount(jsonObject.getInt("amount"));
            charge.setCurrency(jsonObject.getString("currency"));
            charge.setRefunded(jsonObject.getBoolean("refunded"));
            charge.setCaptured(jsonObject.getBoolean("captured"));
            charge.setBalanceTransaction(jsonObject.getString("balance_transaction"));
            charge.setFailureMessage(jsonObject.getString("failure_message"));
            charge.setFailureCode(jsonObject.getString("failure_code"));
            charge.setAmountRefunded(jsonObject.getInt("amount_refunded"));
            charge.setCustomer(jsonObject.getString("customer"));
            charge.setInvoice(jsonObject.getString("invoice"));
            charge.setDescription(jsonObject.getString("description"));
            charge.setStatementDescription(jsonObject.getString("statement_descriptor"));



            // TODO: implement dispute parser
            // TODO: implement Metadata parser
            // TODO: implement fraud_detail parser
            // TODO: implement refund parser


            return charge;
        } catch (Exception e) {
            if (Logger.DEBUG)
                e.printStackTrace();
        }

        return null;
    }
}
