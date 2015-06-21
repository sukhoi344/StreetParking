package chau.streetparking.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.wallet.WalletConstants;

import java.util.List;

import chau.streetparking.R;
import chau.streetparking.datamodels.CardItem;
import chau.streetparking.datamodels.CardTypes;
import chau.streetparking.util.TextUtil;

/**
 * Created by Chau Thai on 6/20/2015.
 */
public class PaymentsAdapter extends RecyclerView.Adapter {
    private List<CardItem> dataSet;
    private Context context;

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

    public PaymentsAdapter(Context context, List<CardItem> dataSet) {
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
            final CardItem cardItem = dataSet.get(position);
            ViewHolder viewHolder = (ViewHolder) holder;

            // TODO: set CardIcon

            String title = "";
            switch (cardItem.getType()) {
                case CardTypes.BUSINESS:
                    title += "BUSINESS \t";
                    break;
                case CardTypes.PERSONAL:
                    title += "PERSONAL \t";
            }

            final String number = cardItem.getNumber();
            if (number != null) {
                title += TextUtil.getCodedNumber(number);
            }
            viewHolder.cardTitle.setText(title);
            viewHolder.cardIcon.setImageResource(getCardIconRes(cardItem));

            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PaymentDetailActivity.class);
                    intent.putExtra(PaymentDetailActivity.EXTRA_CARD, cardItem);
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

    private int getCardIconRes(CardItem cardItem) {
        int cardIconRes;
        switch (cardItem.getNetwork()) {
            case WalletConstants.CardNetwork.VISA:
                cardIconRes = R.drawable.ic_visa;
                break;
            case WalletConstants.CardNetwork.MASTERCARD:
                cardIconRes = R.drawable.ic_mastercard;
                break;
            case WalletConstants.CardNetwork.AMEX:
                cardIconRes = R.drawable.ic_american_express;
                break;
            case WalletConstants.CardNetwork.DISCOVER:
                cardIconRes = R.drawable.ic_discover;
                break;
            default:
                cardIconRes = R.drawable.ic_card_blank;
                break;
        }
        return cardIconRes;
    }
}
