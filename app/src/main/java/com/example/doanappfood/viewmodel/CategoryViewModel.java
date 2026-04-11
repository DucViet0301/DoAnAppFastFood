package com.example.doanappfood.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.CategoryModel;
import com.example.doanappfood.repository.CategoryRepository;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private CategoryRepository categoryRepository;
    private MutableLiveData<List<CategoryModel>> categorylist;
    public CategoryViewModel(@NonNull Application application) {
        super(application);
        categoryRepository = new CategoryRepository();
    }
    public MutableLiveData<List<CategoryModel>> getCategoryList(){
        if(categorylist == null){
            categorylist = categoryRepository.getCategorys();
        }
        return categorylist;
    }
}
