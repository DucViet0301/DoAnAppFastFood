package com.example.doanappfood.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.doanappfood.model.MessModel;
import com.example.doanappfood.model.OrderModel;
import com.example.doanappfood.repository.OrderRepository;

import java.util.List;

public class OrderViewModel extends AndroidViewModel {
    private OrderRepository orderRepository;
    private MutableLiveData<MessModel> data;

    public OrderViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(){
        orderRepository = new OrderRepository(getApplication());
        data = orderRepository.messModelMutableLiveData();
    }
    public void CheckOut(String detail){
        orderRepository.checkOut(detail);
    }
    public MutableLiveData<MessModel> getMessModelMutableLiveData(){
        return data;
    }
    public  MutableLiveData<List<OrderModel>> getAllOrder(int userId){
        return orderRepository.getOrder(userId);
    }
}
