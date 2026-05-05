package com.example.doanappfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanappfood.R;
import com.example.doanappfood.model.Message;

import java.util.List;

public class ChatBotAdapter extends RecyclerView.Adapter<ChatBotAdapter.ViewHolder> {
    private List<Message> messageList;
        public ChatBotAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatView, rightChatView;
        TextView leftChatText, rightChatText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatView = itemView.findViewById(R.id.left_chat_view);
            rightChatView = itemView.findViewById(R.id.right_chat_view);
            leftChatText = itemView.findViewById(R.id.left_chat_text_view);
            rightChatText = itemView.findViewById(R.id.right_chat_text_view);
        }
    }
    @NonNull
    @Override
    public ChatBotAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatBotAdapter.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if(message.getSentBy().equals(Message.SENT_BY_ME)){
            holder.leftChatView.setVisibility(View.GONE);
            holder.rightChatView.setVisibility(View.VISIBLE);
            holder.rightChatText.setText(message.getMessage());
        }
        else{
            holder.rightChatView.setVisibility(View.GONE);
            holder.leftChatView.setVisibility(View.VISIBLE);
            holder.leftChatText.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
