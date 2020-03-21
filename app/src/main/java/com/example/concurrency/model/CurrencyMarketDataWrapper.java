package com.example.concurrency.model;

import androidx.annotation.NonNull;

import java.util.LinkedHashMap;

public class CurrencyMarketDataWrapper {
    private LinkedHashMap<String, Double> allRates;

    public LinkedHashMap<String, Double> getAllRates() {
        return allRates;
    }

    public CurrencyMarketDataWrapper(@NonNull CurrencyMarketDataModel currencyMarketDataModel) {
        allRates = currencyMarketDataModel.getRates();
    }

    public String getCurrencyNameByIndex(int index) {
        return (String) allRates.keySet().toArray()[index];
    }

    public Double getRateByIndex(int index) {
        return (Double) allRates.values().toArray()[index];
    }
}
