package com.example.doanappfood.activity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.doanappfood.model.Message;

import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.Keyboard;
import com.example.doanappfood.adapter.ChatBotAdapter;
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

public class ChatBotActivity extends AppCompatActivity {
    RecyclerView recyclerViewChatBox;
    TextView welcome_text;
    EditText message_edit_text;
    ImageButton send_btn;
    List<Message> messageList;
    ChatBotAdapter chatBoxAdapter;
    private ApiApp apiApp;
    ImageView btnBackChatBot;
    private static final String PREFS_NAME = "chatbot_prefs";
    private static final String HISTORY_KEY = "chat_history";
    private static final String LAST_TIME_KEY = "last_chat_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_bot);
        initViews();
        messageList = new ArrayList<>();
        apiApp = RetrofitInstance.getRetrofit().create(ApiApp.class);

        chatBoxAdapter = new ChatBotAdapter(messageList);
        recyclerViewChatBox.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChatBox.setAdapter(chatBoxAdapter);
        loadChatHistory();
    }

    private void initViews() {
        recyclerViewChatBox = findViewById(R.id.recyclerViewChatBox);
        welcome_text = findViewById(R.id.welcome_text);
        message_edit_text = findViewById(R.id.message_edit_text);
        send_btn = findViewById(R.id.send_btn);
        btnBackChatBot = findViewById(R.id.btnBackChatBot);

        send_btn.setOnClickListener(v -> {
            String message = message_edit_text.getText().toString().trim();
            if (!message.isEmpty()) {
                recyclerViewChatBox.setVisibility(View.VISIBLE);
                addToChat(message, Message.SENT_BY_ME);
                message_edit_text.setText("");
                welcome_text.setVisibility(View.GONE);
                CallApi(message);
            }
        });
        btnBackChatBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
            }
        });
    }

    public void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                chatBoxAdapter.notifyDataSetChanged();
                recyclerViewChatBox.smoothScrollToPosition(chatBoxAdapter.getItemCount());
                saveChatHistory();
            }
        });
    }
    private void saveChatHistory() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(messageList);

        editor.putString(HISTORY_KEY, json);
        editor.putLong(LAST_TIME_KEY, System.currentTimeMillis());
        editor.apply();
    }

    private void loadChatHistory() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        long lastTime = prefs.getLong(LAST_TIME_KEY, 0);
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastTime > 600000) {
            prefs.edit().remove(HISTORY_KEY).apply();
            return;
        }

        String json = prefs.getString(HISTORY_KEY, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Message>>() {}.getType();
            List<Message> savedList = gson.fromJson(json, type);

            if (savedList != null && !savedList.isEmpty()) {
                messageList.addAll(savedList);
                chatBoxAdapter.notifyDataSetChanged();
                recyclerViewChatBox.setVisibility(View.VISIBLE);
                welcome_text.setVisibility(View.GONE);
                recyclerViewChatBox.smoothScrollToPosition(messageList.size() - 1);
            }
        }
    }
    public void CallApi(String question) {
        messageList.add(new Message("Đang trả lời ...", Message.SENT_BY_BOT));
        chatBoxAdapter.notifyDataSetChanged();
        recyclerViewChatBox.smoothScrollToPosition(chatBoxAdapter.getItemCount());

        SharedPreferences prefs = getSharedPreferences("chatbot_prefs", MODE_PRIVATE);
        String savedSessionId = prefs.getString("session_id", null);

        Map<String, String> body = new HashMap<>();
        body.put("prompt", question);
        if (savedSessionId != null) {
            body.put("session_id", savedSessionId);
        }

        apiApp.sendMessage(body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                messageList.remove(messageList.size() - 1);
                if (response.isSuccessful() && response.body() != null) {
                    String newSessionId = response.body().get("session_id");
                    if (newSessionId != null) {
                        prefs.edit().putString("session_id", newSessionId).apply();
                    }
                    String reply = response.body().get("reply");
                    addToChat(reply != null ? reply : "Không có phản hồi.", Message.SENT_BY_BOT);
                } else {
                    addToChat("Server không phản hồi.", Message.SENT_BY_BOT);
                }
                saveChatHistory();
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                messageList.remove(messageList.size() - 1);
                addToChat("Lỗi kết nối: " + t.getMessage(), Message.SENT_BY_BOT);
                saveChatHistory();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Keyboard.hideKeyboardOnTouchOutside(this, event);
        return super.dispatchTouchEvent(event);
    }
}