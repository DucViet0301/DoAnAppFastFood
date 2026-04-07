package com.example.doanappfood.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.NewModel;
import com.example.doanappfood.repository.NewRepository;

import java.util.List;

public class NewViewModel extends AndroidViewModel {
    private NewRepository newRepository;
    private MutableLiveData<List<NewModel>> newList;

    public NewViewModel(@NonNull Application application) {
        super(application);
        newRepository = new NewRepository();
    }
    public MutableLiveData<List<NewModel>> getNewList(){
        if (newList == null){
            newList = newRepository.getNew();
        }
        return  newList;
    }
    public  void refreshBanners(){
        newList = newRepository.getNew();
    }
}
