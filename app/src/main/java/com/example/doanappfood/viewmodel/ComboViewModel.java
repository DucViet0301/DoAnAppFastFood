package com.example.doanappfood.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.doanappfood.model.ComboModel;
import com.example.doanappfood.repository.ComboRepository;
import java.util.List;

public class ComboViewModel extends AndroidViewModel {
    private ComboRepository comboRepository;
    private MutableLiveData<List<ComboModel>> comboList;

    public ComboViewModel(@NonNull Application application) {
        super(application);
        comboRepository = new ComboRepository();
    }

    public MutableLiveData<List<ComboModel>> getComboList() {
        if (comboList == null) {
            comboList = comboRepository.getCombos();
        }
        return comboList;
    }


    public void refreshCombo() {
        if (comboList != null) {
            // Lấy LiveData mới từ repo và observe nó một lần duy nhất để lấy data đổ vào comboList cũ
            comboRepository.getCombos().observeForever(newData -> {
                if (newData != null) {
                    comboList.postValue(newData);
                }
            });
        } else {
            getComboList();
        }
    }
}