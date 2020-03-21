package com.example.concurrency.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.concurrency.R;
import com.example.concurrency.model.CurrencyMarketDataModel;
import com.example.concurrency.model.LiveDataTimerViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.concurrency.controller.Utils.isNetworkAvailable;
import static com.example.concurrency.controller.Utils.parseJson;

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

    private class MarketDataAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String response = null;
            try {
                String baseCurrency = params[0];
                URL url = new URL("https://hiring.revolut.codes/api/android/latest?base=" + baseCurrency);
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                httpConn.setRequestMethod("GET");
                InputStream inputStream = httpConn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                response = bufferedReader.readLine();
                httpConn.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            Log.d("RESPONSE", response);
            CurrencyMarketDataModel marketData = parseJson(response, CurrencyMarketDataModel.class);
        }
    }
}
