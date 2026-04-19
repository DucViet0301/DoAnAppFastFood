package com.example.doanappfood.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.PromotionNewsModel;

import com.example.doanappfood.repository.PromotionNewsRepository;

import java.util.List;

public class PromotionNewsViewModel extends AndroidViewModel {
    private PromotionNewsRepository newRepository;
    private MutableLiveData<List<PromotionNewsModel>> newList;

    public PromotionNewsViewModel(@NonNull Application application) {
        super(application);
        newRepository = new PromotionNewsRepository();
    }
    public MutableLiveData<List<PromotionNewsModel>> getPromotionNewsList(){
        if (newList == null){
            newList = newRepository.getPromotionNews();
        }
        return  newList;
    }
    public  void refreshBanners(){
        newList = newRepository.getPromotionNews();
    }

}
