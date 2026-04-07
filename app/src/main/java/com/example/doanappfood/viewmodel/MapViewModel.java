package com.example.doanappfood.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.DirectionModel;
import com.example.doanappfood.model.StoreModel;
import com.example.doanappfood.repository.MapRepository;

import java.util.List;

public class MapViewModel extends AndroidViewModel {
    private final MapRepository mapRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);


    public MapViewModel(@NonNull Application application) {
        super(application);
        mapRepository = new MapRepository();
    }
    public LiveData<List<StoreModel>> getAllStores() {
        return mapRepository.getALLStores();
    }

    public LiveData<StoreModel> getNearestStore(double lat, double lng) {
        return mapRepository.getNearestStore(lat, lng);
    }

    public LiveData<DirectionModel> getDirections(
            double fromLat, double fromLng, double toLat, double toLng) {
        return mapRepository.getDirections(fromLat, fromLng, toLat, toLng);
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }

}
