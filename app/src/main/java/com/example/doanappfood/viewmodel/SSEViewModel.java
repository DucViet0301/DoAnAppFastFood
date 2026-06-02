package com.example.doanappfood.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.Utlis.SSEClient;

import org.json.JSONObject;


public class SSEViewModel extends AndroidViewModel {

    private static final String TAG     = "SSEViewModel";
    private static final String SSE_URL = "https://adminappfood-gih0.onrender.com/sse";


    private final MutableLiveData<String> comboChanged   = new MutableLiveData<>();
    private final MutableLiveData<String> productChanged = new MutableLiveData<>();

    private SSEClient sseClient;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public SSEViewModel(@NonNull Application application) {
        super(application);
        startSSE();
    }

    public LiveData<String> getComboChanged()   { return comboChanged; }
    public LiveData<String> getProductChanged() { return productChanged; }

    private void startSSE() {
        sseClient = new SSEClient(SSE_URL, new SSEClient.SSEListener() {

            @Override
            public void onConnected() {
                Log.d(TAG, "SSE connected ✓");
            }

            @Override
            public void onEvent(String event, String data) {
                Log.d(TAG, "event=" + event + " data=" + data);
                mainHandler.post(() -> {
                    try {
                        JSONObject json   = new JSONObject(data);
                        String action     = json.optString("action", "");
                        switch (event) {
                            case "product_changed":
                                productChanged.setValue(action);
                                break;
                            case "combo_changed":
                                comboChanged.setValue(action);
                                break;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "parse error: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onDisconnected() {
                Log.d(TAG, "SSE disconnected, reconnecting...");
            }
        });
        sseClient.connect();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (sseClient != null) sseClient.disconnect();
    }
}