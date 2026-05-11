package com.example.doanappfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.DateUtils;
import com.example.doanappfood.model.OrderModel;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private final Context context;
    private final List<OrderModel> list;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onClick(OrderModel orderModel, int position);
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }

    public OrderAdapter(Context context, List<OrderModel> list) {
        this.context = context;
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvItemCount, tvOrderDate, tvDeliveryAddress,
                tvDeliveryInfo, tvTotalPrice, tvStatusBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId         = itemView.findViewById(R.id.tvOrderId);
            tvItemCount       = itemView.findViewById(R.id.tvItemCount);
            tvOrderDate       = itemView.findViewById(R.id.tvOrderDate);
            tvDeliveryAddress = itemView.findViewById(R.id.tvDeliveryAddress);
            tvDeliveryInfo    = itemView.findViewById(R.id.tvDeliveryInfo);
            tvTotalPrice      = itemView.findViewById(R.id.tvTotalPrice);
            tvStatusBadge     = itemView.findViewById(R.id.tvStatusBadge);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = list.get(position);

        holder.tvOrderId.setText("#Mã đơn hàng-DV" + order.getId());
        holder.tvItemCount.setText(order.getTotal_items() + " sản phẩm");
        holder.tvOrderDate.setText(DateUtils.formatDate(order.getCreated_at()));
        holder.tvDeliveryAddress.setText(order.getAddress());
        holder.tvDeliveryInfo.setText("Dự kiến còn " + order.getTime() + " phút");
        if(order.getStatus().equals("Đang xử lý")){
            holder.tvStatusBadge.setText("Chưa Thanh Toán");
        }else if (order.getStatus().equals("cancelled")){
            holder.tvStatusBadge.setText("Đã hủy đơn hàng");
        }else if (order.getStatus().equals("Đã Thanh Toán")){
            holder.tvStatusBadge.setText("Đã Thanh Toán");
        }

        try {
            double price = Double.parseDouble(order.getTotal_price());
            holder.tvTotalPrice.setText(
                    NumberFormat.getNumberInstance(new Locale("vi", "VN")).format(price) + " đ"
            );
        } catch (NumberFormatException e) {
            holder.tvTotalPrice.setText(order.getTotal_price() + " đ");
        }


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(order, position);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}