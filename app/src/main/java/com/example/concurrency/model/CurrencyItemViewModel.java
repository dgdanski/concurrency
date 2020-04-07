package com.example.concurrency.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CurrencyItemViewModel extends ViewModel {
    public MutableLiveData<Double> firstItemValue = new MutableLiveData<>();
}
