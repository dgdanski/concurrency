package com.example.concurrency.view;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.concurrency.R;
import com.example.concurrency.model.CurrencyMarketDataModel;
import com.example.concurrency.model.LiveDataTimerViewModel;

import static com.example.concurrency.controller.Utils.isNetworkAvailable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LiveDataTimerViewModel liveDataTimerViewModel = new ViewModelProvider(this).get(LiveDataTimerViewModel.class);
        subscribe(liveDataTimerViewModel);
    }

    private void subscribe(LiveDataTimerViewModel liveDataTimerViewModel) {
        final Observer<Long> elapsedTimeObserver = timeInSeconds -> {
            if (isNetworkAvailable(MainActivity.this)) {
                (new MarketDataAsyncTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "GBP");
            }
        };

        liveDataTimerViewModel.getElapsedTime().observe(this, elapsedTimeObserver);
    }
}
