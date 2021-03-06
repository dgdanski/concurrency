package com.example.concurrency.view.recyclerView.currency;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.concurrency.R;
import com.example.concurrency.controller.Utils;
import com.example.concurrency.model.CurrencyItemViewModel;
import com.example.concurrency.model.CurrencyMarketDataWrapper;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.example.concurrency.controller.Utils.round;

public class CurrencyRecyclerViewAdapter extends RecyclerView.Adapter<CurrencyRecyclerViewAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private LinkedList<Map.Entry<String, Double>> ratesList = new LinkedList<>();
    private RecyclerView recyclerView;
    private Context context;
    private CurrencyItemViewModel currencyItemViewModel;

    // data is passed into the constructor
    public CurrencyRecyclerViewAdapter(Context context, CurrencyMarketDataWrapper currencyMarketDataWrapper, RecyclerView recyclerView) {
        this.mInflater = LayoutInflater.from(context);
        HashMap<String, Double> rates = currencyMarketDataWrapper.getAllRates();
        ratesList.addAll(rates.entrySet());
        this.recyclerView = recyclerView;
        this.context = context;
    }

    private EditText editTextFromFirstItem;
    private Double firstItemRate;

    public void updateRates(CurrencyMarketDataWrapper newMarketData) {
        for (Map.Entry<String, Double> entry : ratesList) {
            Double newRate = newMarketData.getAllRates().get(entry.getKey());
            entry.setValue(newRate);
        }
        int cursorPosition = editTextFromFirstItem.getSelectionEnd();
        editTextFromFirstItem.setText(String.valueOf(round(firstItemRate * 1, 2)));
        editTextFromFirstItem.setSelection(cursorPosition);
    }

    // inflates the row layout from xml when needed
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.currency_recycler_view_single_item, parent, false);
        currencyItemViewModel = new ViewModelProvider((AppCompatActivity) context).get(CurrencyItemViewModel.class);
        return new ViewHolder(view, recyclerView);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String currencyFullName = ratesList.get(position).getKey();
        holder.currencyTickerTextView.setText(currencyFullName);
        holder.currencyFullNameTextView.setText(currencyFullName);
        Double rate = ratesList.get(position).getValue();
        if (position == 0) {
            editTextFromFirstItem = holder.currencyValueEditText;
            firstItemRate = rate;
            holder.currencyValueEditText.setText(String.valueOf(round(rate * 1, 2)));
        } else if (position == 1) {
            holder.currencyValueEditText.setText(String.valueOf(round(rate * firstItemRate, 2)));
        } else {
            holder.currencyValueEditText.setText(String.valueOf(round(rate * firstItemRate, 2)));
        }

        subscribe(holder.currencyValueEditText, position);
    }
    String previousValue;
    private void subscribe(EditText editText, int position) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (position == 0 && !s.toString().equals(previousValue)) {
                    currencyItemViewModel.firstItemValue.setValue(Double.valueOf(s.toString()));
                    firstItemRate = Double.valueOf(s.toString());
                    previousValue = s.toString();
                }
            }
        });

        currencyItemViewModel.firstItemValue.observe((AppCompatActivity) context, new Observer<Double>() {
            @Override
            public void onChanged(@Nullable Double value) {
                Double rateFromFirstItem = Double.valueOf(editTextFromFirstItem.getText().toString());
                Double ownRate = ratesList.get(position).getValue();
                if (position != 0) {
                    editText.setText(String.valueOf(round(ownRate * rateFromFirstItem, 2)));
                }
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return ratesList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

//    @Override
//    public int getItemViewType(int position) {
//        return position;
//    }

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
            currencyValueEditText.setOnScrollChangeListener((editText, a, b, c, d) -> editText.clearFocus());
            itemView.setOnClickListener(this);
            this.recyclerView = recyclerView;
        }

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard((Activity) context);
            clearFocusOfTheFirstItem();
            moveItemToTop(getAdapterPosition());

        }
    }

    private void clearFocusOfTheFirstItem() {
        if (recyclerView.getLayoutManager() != null) {
            View currentFirstItem = recyclerView.getLayoutManager().findViewByPosition(0);
            if (currentFirstItem != null) {
                currentFirstItem.clearFocus();
            }
        }
    }

    private void moveItemToTop(int itemCurrentPosition) {
        Map.Entry<String, Double> removedItem = ratesList.get(itemCurrentPosition);
        ratesList.remove(itemCurrentPosition);
        ratesList.add(0, removedItem);
        notifyItemMoved(itemCurrentPosition, 0);
        recyclerView.scrollToPosition(0);
    }
}