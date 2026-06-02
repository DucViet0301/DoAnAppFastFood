package com.example.doanappfood.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.doanappfood.model.MessModel;
import com.example.doanappfood.model.OrderDetailModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.repository.OrderDetailRepository;

public class OrderDetailViewModel extends AndroidViewModel {
    private final OrderDetailRepository repository;
    public OrderDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new OrderDetailRepository(application);
    }
    public LiveData<OrderDetailModel> getOrderDetail(int orderId) {
        return repository.getOrderDetail(orderId);
    }
    public  MutableLiveData<MessModel> cancel(int orderId){
        return repository.cancel(orderId);
    }

}
