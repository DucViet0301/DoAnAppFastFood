package com.example.doanappfood.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.DateUtils;
import com.example.doanappfood.model.NotificationModel;
import com.example.doanappfood.model.OrderModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final Context context;
    private final List<NotificationModel> list;

    public NotificationAdapter(Context context, List<NotificationModel> list) {
        this.context = context;
        this.list = list;
    }
    public  class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvOrderId, tvNotifyTime, tvSuccessMessage, tvTotalPrice, tvStatusBadge, tvNotifTitle;
        ImageView icon_notification;
        LinearLayout LLNotification;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvNotifyTime = itemView.findViewById(R.id.tvNotifyTime);
            tvSuccessMessage = itemView.findViewById(R.id.tvSuccessMessage);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvNotifTitle = itemView.findViewById(R.id.tvNotifTitle);
            LLNotification = itemView.findViewById(R.id.LLNotification);
            icon_notification = itemView.findViewById(R.id.icon_notification);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel item = list.get(position);
        holder.tvOrderId.setText("Mã đơn hàng-DV" + item.getOrderId());
        holder.tvNotifTitle.setText(item.getTitle());
        holder.tvNotifyTime.setText(
                DateUtils.formatDate(item.getTime())
        );
        holder.tvSuccessMessage.setText(item.getMessage());
        holder.tvTotalPrice.setText(item.getTotalPrice());
        holder.tvStatusBadge.setText(item.getStatus());
        String type = item.getType();
        if("cancel".equals(type)){
            holder.tvNotifTitle.setTextColor(Color.parseColor("#D32F2F"));
            holder.tvOrderId.setTextColor(Color.parseColor("#D32F2F"));
            holder.tvNotifyTime.setTextColor(Color.parseColor("#D32F2F"));
            holder.LLNotification.setBackgroundColor(Color.parseColor("#FFEBEE"));
            holder.icon_notification.setImageResource(R.drawable.ic_cannel_order);
        }else if("success".equals(type)){
            holder.tvNotifTitle.setTextColor(Color.parseColor("#2E7D32"));
            holder.tvOrderId.setTextColor(Color.parseColor("#2E7D32"));
            holder.tvNotifyTime.setTextColor(Color.parseColor("#2E7D32"));
            holder.LLNotification.setBackgroundColor(Color.parseColor("#E8F5E9"));
            holder.icon_notification.setImageResource(R.drawable.ic_check_success);
        }else{
            holder.tvNotifTitle.setTextColor(Color.parseColor("#2E7D32"));
            holder.tvOrderId.setTextColor(Color.parseColor("#2E7D32"));
            holder.tvNotifyTime.setTextColor(Color.parseColor("#2E7D32"));

        }

    }

    @Override
    public int getItemCount() { return list.size(); }


}