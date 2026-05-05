package com.example.doanappfood.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.AuthModel;
import com.example.doanappfood.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository repository;

    // LiveData để Activity quan sát kết quả
    private final MutableLiveData<AuthModel> authResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = new AuthRepository(application);
    }

    // Getter LiveData
    public LiveData<AuthModel> getAuthResult() {
        return authResult;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    // Đăng nhập
    public void login(String login, String password) {
        isLoading.setValue(true);

        repository.login(login, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(AuthModel response) {
                isLoading.setValue(false);
                authResult.setValue(response);
            }

            @Override
            public void onFailure(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    // Đăng ký
    public void register(String name, String phone, String email,
                         String password, String address, String birthDate) {
        isLoading.setValue(true);

        repository.register(name, phone, email, password, address, birthDate,
                new AuthRepository.AuthCallback() {
                    @Override
                    public void onSuccess(AuthModel response) {
                        isLoading.setValue(false);
                        authResult.setValue(response);
                    }

                    @Override
                    public void onFailure(String error) {
                        isLoading.setValue(false);
                        errorMessage.setValue(error);
                    }
                });
    }
}