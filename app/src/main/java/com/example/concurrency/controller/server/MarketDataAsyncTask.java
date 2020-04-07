package com.example.concurrency.controller.server;

import android.os.AsyncTask;
import android.util.Log;

import com.example.concurrency.model.CurrencyMarketDataModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.concurrency.controller.Utils.parseJson;

public class MarketDataAsyncTask extends AsyncTask<String, String, String> {
    RequestStateListener requestStateListener;

    public MarketDataAsyncTask(RequestStateListener requestStateListener) {
        this.requestStateListener = requestStateListener;
    }

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
        if (marketData == null) {
            requestStateListener.onError();
        } else {
            requestStateListener.onSuccess(marketData);
        }
    }

    public interface RequestStateListener {
        void onSuccess(CurrencyMarketDataModel marketData);

        void onError();
    }
}