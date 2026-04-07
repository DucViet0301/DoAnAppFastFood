package com.example.doanappfood.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.ProductModel;
import com.example.doanappfood.repository.ProductRepository;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {
    private ProductRepository productRepository;
    private MutableLiveData<List<ProductModel>> productlist;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository();
    }
    public MutableLiveData<List<ProductModel>> getProducts(int idCate){

            return productRepository.getProduct(idCate);
    }
}
