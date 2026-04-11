package com.example.doanappfood.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanappfood.R;
import com.example.doanappfood.model.CategoryModel;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<CategoryModel> list;
    private Context context;
    private  int selectedPosition = 0;

    public  interface  OnCategoryClickListener{
        void onClick(CategoryModel categoryModel, int position);
    }
    private  OnCategoryClickListener listener;
    public void setOnCategoryClickListener(OnCategoryClickListener listener){
        this.listener = listener;
    }

    public CategoryAdapter(Context context, List<CategoryModel> list) {
        this.context = context;
        this.list = list;
    }

    public  class ViewHolder extends  RecyclerView.ViewHolder{
        CardView cardViewCategory;
        ImageView imgcategory;
        TextView tvCategoryName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewCategory = itemView.findViewById(R.id.cardViewCategory);
            imgcategory = itemView.findViewById(R.id.imgCategory);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
    public  void setData(List<CategoryModel> newlist){
        this.list = newlist;
        notifyDataSetChanged();
    }
    public  void setSelectedCategory(int idCate){
        for( int i = 0 ; i < list.size(); i++){
            if (list.get(i).getId() == idCate){
                selectedPosition = i;
                notifyDataSetChanged();
                break;
            }
        }
    }


    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        CategoryModel item = list.get(position);
        holder.tvCategoryName.setText(item.getName());
        Glide.with(context).load(item.getImage()).into(holder.imgcategory);

        if(position == selectedPosition){
            holder.cardViewCategory.setForeground(context.getDrawable(R.drawable.circle_boder_selected_category));
            holder.tvCategoryName.setTextColor(context.getColor(R.color.do_nhat));
        }
        else{
            holder.cardViewCategory.setForeground(context.getDrawable(R.drawable.circle_boder_category));
            holder.tvCategoryName.setTextColor(context.getColor(R.color.black));
        }

        holder.itemView.setOnClickListener(v ->{
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);

            if (listener!= null) {
                listener.onClick(item, selectedPosition);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
