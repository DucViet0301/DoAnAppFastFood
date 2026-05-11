package com.example.doanappfood.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.doanappfood.model.MessModel;
import com.example.doanappfood.model.OrderModel;
import com.example.doanappfood.repository.OrderRepository;

import java.util.List;

public class OrderViewModel extends ViewModel {
    private OrderRepository orderRepository;
    private MutableLiveData<MessModel> data;
    public void init(){
        orderRepository = new OrderRepository();
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
