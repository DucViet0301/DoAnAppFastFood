package com.example.doanappfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.doanappfood.R;
import com.example.doanappfood.model.BannerModel;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {
    private List<BannerModel> list;
    Context context;

    public BannerAdapter(List<BannerModel> list, Context context) {
        this.list = list;
        this.context = context;
    }
    public  void  updateList(List<BannerModel> newList){
        this.list = newList;
        notifyDataSetChanged();
    }

    public  class ViewHolder extends  RecyclerView.ViewHolder{
        ImageView imageBanner;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageBanner = itemView.findViewById(R.id.imageBanner);
        }
    }

    @NonNull
    @Override
    public BannerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerAdapter.ViewHolder holder, int position) {
        BannerModel item = list.get(position);
        Glide.with(holder.imageBanner.getContext())
                .load(item.getImage())
                .into(holder.imageBanner);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
