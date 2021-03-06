package com.example.concurrency.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.concurrency.R;
import com.example.concurrency.controller.Utils;
import com.example.concurrency.controller.server.MarketDataAsyncTask;
import com.example.concurrency.model.CurrencyMarketDataModel;
import com.example.concurrency.model.CurrencyMarketDataWrapper;
import com.example.concurrency.model.LiveDataTimerViewModel;
import com.example.concurrency.view.recyclerView.MarginItemDecoration;
import com.example.concurrency.view.recyclerView.currency.CurrencyRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity {

    private CurrencyRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LiveDataTimerViewModel liveDataTimerViewModel = new ViewModelProvider(this).get(LiveDataTimerViewModel.class);
        subscribe(liveDataTimerViewModel);

        MarketDataAsyncTask asyncTask = new MarketDataAsyncTask(new MarketDataAsyncTask.RequestStateListener() {
            @Override
            public void onSuccess(CurrencyMarketDataModel marketData) {
                initRecyclerView(marketData);
            }

            @Override
            public void onError() {

            }
        });
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "GBP");
    }

    private void initRecyclerView(CurrencyMarketDataModel marketData) {
        RecyclerView recyclerView = findViewById(R.id.currency_recycler_view);
        adapter = new CurrencyRecyclerViewAdapter(MainActivity.this, new CurrencyMarketDataWrapper(marketData), recyclerView);
//        adapter.setClickListener((view, position) -> Toast.makeText(MainActivity.this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show());
        adapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.addItemDecoration(new MarginItemDecoration(15));
        recyclerView.setAdapter(adapter);
        recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (oldScrollY != 0) {
//                Utils.hideKeyboard(MainActivity.this);
            }
        });
    }

    private void subscribe(LiveDataTimerViewModel liveDataTimerViewModel) {
        final Observer<Long> elapsedTimeObserver = timeInSeconds -> {
            MarketDataAsyncTask asyncTask = new MarketDataAsyncTask(new MarketDataAsyncTask.RequestStateListener() {
                @Override
                public void onSuccess(CurrencyMarketDataModel marketData) {
                    adapter.updateRates(new CurrencyMarketDataWrapper(marketData));
                }

                @Override
                public void onError() {

                }
            });
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "GBP");
        };

        liveDataTimerViewModel.getElapsedTime().observe(this, elapsedTimeObserver);
    }
}
