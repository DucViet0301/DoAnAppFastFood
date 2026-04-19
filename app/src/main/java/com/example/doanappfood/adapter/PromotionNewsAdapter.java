package com.example.doanappfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanappfood.R;
import com.example.doanappfood.model.PromotionNewsModel;

import java.util.List;

public class PromotionNewsAdapter extends RecyclerView.Adapter<PromotionNewsAdapter.ViewHolder> {
    private List<PromotionNewsModel> list;
    private OnPromotionNewsClickListener listener;
    private Context context;

    public interface OnPromotionNewsClickListener {
        void OnClick(PromotionNewsModel promotionNewsModel, int position);
    }

    public void setOnPromotionNewsClickListener(OnPromotionNewsClickListener listener) {
        this.listener = listener;
    }

    public PromotionNewsAdapter(List<PromotionNewsModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgNew;
        TextView tvTitleNew, tvDateNew;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgNew = itemView.findViewById(R.id.imgPromotionNew);
            tvTitleNew = itemView.findViewById(R.id.tvTitlePromotionNew);
            tvDateNew = itemView.findViewById(R.id.tvDatePromotionNew);
        }
    }

    public void setData(List<PromotionNewsModel> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promotion_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PromotionNewsModel item = list.get(position);
        if (item != null) {
            holder.tvTitleNew.setText(item.getTitle());
            holder.tvDateNew.setText(item.getCreated_at());
            Glide.with(context).load(item.getImage()).into(holder.imgNew);

            // Xử lý sự kiện Click
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.OnClick(item, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }
}