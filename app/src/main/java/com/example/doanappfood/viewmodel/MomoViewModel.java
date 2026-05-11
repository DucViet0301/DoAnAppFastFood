package com.example.doanappfood.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.doanappfood.model.MessModel;
import com.example.doanappfood.repository.MomoRepository;

public class MomoViewModel extends ViewModel {
    private MomoRepository momoRepository;
    private MutableLiveData<MessModel> data;

    public void init() {
        momoRepository = new MomoRepository();
        data = momoRepository.messModelMutableLiveData();
    }

    public void createPayment(long amount, String orderInfo, String redirectUrl) {
        momoRepository.createPayment(amount, orderInfo, redirectUrl);
    }

    public MutableLiveData<MessModel> messModelMutableLiveData() {
        return data;
    }
}
