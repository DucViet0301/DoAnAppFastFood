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
import com.example.doanappfood.model.ComboModel;
import com.example.doanappfood.model.NewModel;

import java.util.List;

public class NewAdapter extends RecyclerView.Adapter<NewAdapter.ViewHolder> {
    private List<NewModel> list;
    private Context context;

    public NewAdapter(List<NewModel> list, Context context) {
        this.list = list;
        this.context = context;
    }
    public  class  ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgNew;
        TextView tvTitleNew;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgNew = itemView.findViewById(R.id.imgNew);
            tvTitleNew = itemView.findViewById(R.id.tvTitleNew);
        }
    }
    public void setData(List<NewModel> newList){
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewAdapter.ViewHolder holder, int position) {
        NewModel item = list.get(position);
        holder.tvTitleNew.setText(item.getTitle());
        Glide.with(context).load(item.getImage()).into(holder.imgNew);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
