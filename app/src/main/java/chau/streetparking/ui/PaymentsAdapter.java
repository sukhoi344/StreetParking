package chau.streetparking.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stripe.model.Card;
import com.stripe.model.Customer;

import java.util.List;

import chau.streetparking.R;
import chau.streetparking.util.TextUtil;

/**
 * Created by Chau Thai on 8/19/15.
 */
public class PaymentsAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Card> dataSet;

    private static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cardIcon;
        TextView cardTitle;
        View view;

        public ViewHolder(View v) {
            super(v);
            cardIcon = (ImageView) v.findViewById(R.id.card_icon);
            cardTitle = (TextView) v.findViewById(R.id.card_title);
            view = v;
        }
    }

    public PaymentsAdapter(Context context, List<Card> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payment_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (dataSet != null && position < dataSet.size()) {
            final Card card = dataSet.get(position);

            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.cardTitle.setText(TextUtil.getCodedNumber(card.getLast4()));
            viewHolder.cardIcon.setImageResource(getCardIconRes(card.getBrand()));

            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PaymentDetailActivity.class);
                    intent.putExtra(PaymentDetailActivity.EXTRA_CARD, Card.GSON.toJson(card));
                    context.startActivity(intent);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        if (dataSet == null)
            return 0;
        return dataSet.size();
    }

    private int getCardIconRes(String brand) {
        // TODO: add icon for other card brands (JCB, Diners Club)
        switch (brand) {
            case "Visa":
                return R.drawable.ic_visa;
            case "American Express":
                return R.drawable.ic_american_express;
            case "MasterCard":
                return R.drawable.ic_mastercard;
            case "Discover":
                return R.drawable.ic_discover;
            default:
                return R.drawable.ic_card_blank;
        }
    }
}
