package com.example.doanappfood.viewmodel;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.doanappfood.model.ProductDetailModel;
import com.example.doanappfood.repository.ProductDetailRepository;

public class ProductDetailViewModel extends ViewModel {
    private ProductDetailRepository productDetailRepository;
    public ProductDetailViewModel(){
        productDetailRepository = new ProductDetailRepository();
    }
    public MutableLiveData<ProductDetailModel> productDetailModelMutableLiveData(int id){
        return productDetailRepository.getProductDetail(id);
    }
}
