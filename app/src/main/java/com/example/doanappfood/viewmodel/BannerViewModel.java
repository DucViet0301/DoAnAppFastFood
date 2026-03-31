package com.example.doanappfood.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.BannerModel;
import com.example.doanappfood.repository.BannerRepository;

import java.util.List;

public class BannerViewModel  extends AndroidViewModel {
    private BannerRepository bannerRepository;
    private MutableLiveData<List<BannerModel>> bannerList;

    public BannerViewModel(@NonNull Application application) {
        super(application);
        bannerRepository = new BannerRepository();
    }
    public  MutableLiveData<List<BannerModel>> getBannerList(){
        if (bannerList == null){
            bannerList = bannerRepository.getBanner();
        }
        return bannerList;
    }
    public  void refreshBanners(){
        bannerList = bannerRepository.getBanner();
    }
}
