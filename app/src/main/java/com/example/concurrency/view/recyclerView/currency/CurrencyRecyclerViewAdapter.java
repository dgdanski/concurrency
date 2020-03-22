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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.example.concurrency.controller.Utils.round;

public class CurrencyRecyclerViewAdapter extends RecyclerView.Adapter<CurrencyRecyclerViewAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private LinkedList<Map.Entry<String, Double>> ratesList = new LinkedList<>();
    private RecyclerView recyclerView;

    // data is passed into the constructor
    public CurrencyRecyclerViewAdapter(Context context, CurrencyMarketDataWrapper currencyMarketDataWrapper, RecyclerView recyclerView) {
        this.mInflater = LayoutInflater.from(context);
        HashMap<String, Double> rates = currencyMarketDataWrapper.getAllRates();
        ratesList.addAll(rates.entrySet());
        this.recyclerView = recyclerView;
    }

    // inflates the row layout from xml when needed
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.currency_recycler_view_single_item, parent, false);
        return new ViewHolder(view, recyclerView);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String currencyFullName = ratesList.get(position).getKey();
        holder.currencyTickerTextView.setText(currencyFullName);
        holder.currencyFullNameTextView.setText(currencyFullName);
        Double rate = ratesList.get(position).getValue();
        holder.currencyValueEditText.setText(String.valueOf(round(rate * 1, 2)));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return ratesList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView currencyTickerTextView;
        TextView currencyFullNameTextView;
        EditText currencyValueEditText;
        RecyclerView recyclerView;

        ViewHolder(View itemView, RecyclerView recyclerView) {
            super(itemView);
            currencyTickerTextView = itemView.findViewById(R.id.currency_ticker_text_view);
            currencyFullNameTextView = itemView.findViewById(R.id.currency_full_name_text_view);
            currencyValueEditText = itemView.findViewById(R.id.currency_value_edit_text);
            currencyValueEditText.setOnFocusChangeListener((view, hasFocus) -> {
                int currentPosition = getAdapterPosition();
                if (currentPosition != 0 && !recyclerView.isComputingLayout()) {
                    moveItemToTop(currentPosition);
                }
            });
            itemView.setOnClickListener(this);
            this.recyclerView = recyclerView;
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            moveItemToTop(getAdapterPosition());
        }
    }

    private void moveItemToTop(int itemCurrentPosition) {
        Map.Entry<String, Double> removedItem = ratesList.get(itemCurrentPosition);
        ratesList.remove(itemCurrentPosition);
        ratesList.add(0, removedItem);
        notifyItemMoved(itemCurrentPosition, 0);
        recyclerView.scrollToPosition(0);
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