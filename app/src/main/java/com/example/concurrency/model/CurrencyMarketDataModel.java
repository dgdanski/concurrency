package com.example.concurrency.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class CurrencyMarketDataModel {
    private String baseCurrency;
    private Map<String, Double> rates;

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public LinkedHashMap<String, Double> getRates() {
        LinkedHashMap<String, Double> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put(baseCurrency, 1.0);
        linkedHashMap.putAll(rates);
        return linkedHashMap;
    }
}