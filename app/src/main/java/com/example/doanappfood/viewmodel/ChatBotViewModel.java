package com.example.doanappfood.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.Message;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatBotViewModel extends AndroidViewModel {

    private static final String PREFS_NAME   = "chatbot_prefs";
    private static final String HISTORY_KEY  = "chat_history";
    private static final String LAST_TIME_KEY = "last_chat_time";
    private static final String SESSION_KEY  = "session_id";
    private static final long   EXPIRE_MS    = 600_000L;

    private final MutableLiveData<List<Message>> messages = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading      = new MutableLiveData<>(false);

    private final ApiApp apiApp;
    private final SharedPreferences prefs;

    public ChatBotViewModel(@NonNull Application application) {
        super(application);
        apiApp = RetrofitInstance.getRetrofit().create(ApiApp.class);
        prefs  = application.getSharedPreferences(PREFS_NAME, Application.MODE_PRIVATE);
        loadHistory();
    }
    public LiveData<List<Message>> getMessages() { return messages; }
    public LiveData<Boolean> getIsLoading()      { return isLoading; }
    public void sendMessage(String text) {
        if (text == null || text.trim().isEmpty()) return;
        addMessage(text, Message.SENT_BY_ME);

        isLoading.setValue(true);
        addMessage("Đang trả lời ...", Message.SENT_BY_BOT);

        String sessionId = prefs.getString(SESSION_KEY, null);
        Map<String, String> body = new HashMap<>();
        body.put("prompt", text);
        if (sessionId != null) body.put("session_id", sessionId);

        apiApp.sendMessage(body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call,
                                   Response<Map<String, String>> response) {
                removeLastMessage(); // Xóa placeholder
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    String newSession = response.body().get("session_id");
                    if (newSession != null) {
                        prefs.edit().putString(SESSION_KEY, newSession).apply();
                    }
                    String reply = response.body().get("reply");
                    addMessage(reply != null ? reply : "Không có phản hồi.", Message.SENT_BY_BOT);
                } else {
                    addMessage("Server không phản hồi.", Message.SENT_BY_BOT);
                }
                saveHistory();
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                removeLastMessage(); // Xóa placeholder
                isLoading.setValue(false);
                addMessage("Lỗi kết nối: " + t.getMessage(), Message.SENT_BY_BOT);
                saveHistory();
            }
        });
    }
    private void addMessage(String text, String sentBy) {
        List<Message> current = new ArrayList<>(
                messages.getValue() != null ? messages.getValue() : new ArrayList<>());
        current.add(new Message(text, sentBy));
        messages.setValue(current);
    }

    private void removeLastMessage() {
        List<Message> current = messages.getValue();
        if (current != null && !current.isEmpty()) {
            List<Message> updated = new ArrayList<>(current);
            updated.remove(updated.size() - 1);
            messages.setValue(updated);
        }
    }

    private void saveHistory() {
        List<Message> current = messages.getValue();
        if (current == null) return;
        prefs.edit()
                .putString(HISTORY_KEY, new Gson().toJson(current))
                .putLong(LAST_TIME_KEY, System.currentTimeMillis())
                .apply();
    }

    private void loadHistory() {
        long lastTime = prefs.getLong(LAST_TIME_KEY, 0);
        if (System.currentTimeMillis() - lastTime > EXPIRE_MS) {
            prefs.edit().remove(HISTORY_KEY).apply();
            return;
        }
        String json = prefs.getString(HISTORY_KEY, null);
        if (json == null) return;

        Type type = new TypeToken<ArrayList<Message>>() {}.getType();
        List<Message> saved = new Gson().fromJson(json, type);
        if (saved != null && !saved.isEmpty()) {
            messages.setValue(saved);
        }
    }
}