package com.example.doanappfood.activity;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.Keyboard;
import com.example.doanappfood.adapter.ChatBotAdapter;
import com.example.doanappfood.viewmodel.ChatBotViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatBotActivity extends AppCompatActivity {
    private RecyclerView recyclerViewChatBox;
    private TextView welcomeText;
    private EditText messageEditText;
    private ImageButton sendBtn;
    private ImageView btnBackChatBot;

    private ChatBotAdapter chatAdapter;
    private ChatBotViewModel viewModel;

    private final List<Message> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_bot);

        initViews();
        initViews();
        setupRecyclerView();
        setupViewModel();
        setupClickListeners();
    }

    private void initViews() {
        recyclerViewChatBox = findViewById(R.id.recyclerViewChatBox);
        welcomeText         = findViewById(R.id.welcome_text);
        messageEditText     = findViewById(R.id.message_edit_text);
        sendBtn             = findViewById(R.id.send_btn);
        btnBackChatBot      = findViewById(R.id.btnBackChatBot);

    }

    private  void setupRecyclerView(){
        chatAdapter = new ChatBotAdapter(messageList);
        recyclerViewChatBox.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChatBox.setAdapter(chatAdapter);
    }
    private  void setupViewModel(){
        viewModel = new ViewModelProvider(this).get(ChatBotViewModel.class);
        viewModel.getMessages().observe(this, messages -> {
            messageList.clear();
            messageList.addAll(messages);
            chatAdapter.notifyDataSetChanged();
           if(messages.isEmpty()){
               welcomeText.setVisibility(View.VISIBLE);
               recyclerViewChatBox.setVisibility(View.GONE);
           }
           else{
               welcomeText.setVisibility(View.GONE);
               recyclerViewChatBox.setVisibility(View.VISIBLE);
               recyclerViewChatBox.smoothScrollToPosition(messages.size() - 1);
           }
        });
        viewModel.getIsLoading().observe(this, loading -> {
            sendBtn.setEnabled(!Boolean.TRUE.equals(loading));
        });
    }
    private void setupClickListeners() {
        sendBtn.setOnClickListener(v -> {
            String msg = messageEditText.getText().toString().trim();
            if (!msg.isEmpty()) {
                messageEditText.setText("");
                viewModel.sendMessage(msg);
            }
        });

        btnBackChatBot.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
        });
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Keyboard.hideKeyboardOnTouchOutside(this, event);
        return super.dispatchTouchEvent(event);
    }
}