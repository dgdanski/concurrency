package com.example.concurrency.model;

import java.util.Map;

public class CurrencyMarketDataModel {
    private String baseCurrency;
    private Map<String, Double> rates;

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public Map<String, Double> getRates() {
        return rates;
    }
}