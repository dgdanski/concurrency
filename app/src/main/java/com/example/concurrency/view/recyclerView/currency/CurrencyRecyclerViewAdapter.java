package com.example.concurrency.view.recyclerView.currency;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.concurrency.R;
import com.example.concurrency.model.CurrencyMarketDataWrapper;

import org.jetbrains.annotations.NotNull;

import static com.example.concurrency.controller.Utils.round;

public class CurrencyRecyclerViewAdapter extends RecyclerView.Adapter<CurrencyRecyclerViewAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private CurrencyMarketDataWrapper currencyMarketDataWrapper;

    // data is passed into the constructor
    public CurrencyRecyclerViewAdapter(Context context, CurrencyMarketDataWrapper currencyMarketDataWrapper) {
        this.mInflater = LayoutInflater.from(context);
        this.currencyMarketDataWrapper = currencyMarketDataWrapper;
    }

    // inflates the row layout from xml when needed
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.currency_recycler_view_single_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String currencyFullName = currencyMarketDataWrapper.getCurrencyNameByIndex(position);
        holder.currencyTickerTextView.setText(currencyFullName);
        holder.currencyFullNameTextView.setText(currencyFullName);
        Double rate = currencyMarketDataWrapper.getRateByIndex(position);
        holder.currencyValueEditText.setText(String.valueOf(round(rate * 1, 2)));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return currencyMarketDataWrapper.getAllRates().size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView currencyTickerTextView;
        TextView currencyFullNameTextView;
        EditText currencyValueEditText;

        ViewHolder(View itemView) {
            super(itemView);
            currencyTickerTextView = itemView.findViewById(R.id.currency_ticker_text_view);
            currencyFullNameTextView = itemView.findViewById(R.id.currency_full_name_text_view);
            currencyValueEditText = itemView.findViewById(R.id.currency_value_edit_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return String.valueOf(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}