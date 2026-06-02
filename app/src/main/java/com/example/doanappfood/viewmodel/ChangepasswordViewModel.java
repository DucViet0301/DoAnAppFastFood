package com.example.doanappfood.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.ResponseModel;
import com.example.doanappfood.repository.AuthRepository;

public class ChangepasswordViewModel extends AndroidViewModel {
    private final MutableLiveData<Boolean> isLoading       = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> changeSuccess   = new MutableLiveData<>();
    private final MutableLiveData<String>  errorMessage    = new MutableLiveData<>();
    private final MutableLiveData<String>  fieldError      = new MutableLiveData<>();

    private final AuthRepository authRepository;

    public ChangepasswordViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
    }

    public LiveData<Boolean> getIsLoading()     { return isLoading; }
    public LiveData<Boolean> getChangeSuccess() { return changeSuccess; }
    public LiveData<String>  getErrorMessage()  { return errorMessage; }
    public LiveData<String>  getFieldError()    { return fieldError; }
    public void changePassword(String oldPass, String newPass, String confirmPass) {
        if (oldPass.isEmpty()) {
            fieldError.setValue("old");
            return;
        }
        if (newPass.isEmpty()) {
            fieldError.setValue("new");
            return;
        }
        if (!newPass.equals(confirmPass)) {
            fieldError.setValue("confirm");
            return;
        }

        isLoading.setValue(true);

        authRepository.changePassword(oldPass, newPass, new AuthRepository.ResponseCallback() {
            @Override
            public void onSuccess(ResponseModel response) {
                isLoading.setValue(false);
                changeSuccess.setValue(true);
            }

            @Override
            public void onFailure(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }
}
