package com.example.doanappfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanappfood.databinding.ActivityProductDetailBinding;
import com.example.doanappfood.databinding.ItemFeverBinding;
import com.example.doanappfood.databinding.ItemProductDetailBinding;
import com.example.doanappfood.model.ProductModel;
import com.example.doanappfood.model.SaucesModel;

import java.util.List;

public class ProductDetailComboAdapter extends  RecyclerView.Adapter<ProductDetailComboAdapter.MyViewHolder>{
    private List<ProductModel> list;
    ActivityProductDetailBinding binding;

    public ProductDetailComboAdapter(List<ProductModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductDetailBinding binding = ItemProductDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductDetailComboAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setBinding(list.get(position));
        holder.binding.tvItemName.setText(list.get(position).getName());
        holder.binding.tvQuantity.setText("x "+ String.valueOf(list.get(position).getQuantity()));

    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ItemProductDetailBinding binding;

        public MyViewHolder(ItemProductDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
        private void setBinding(ProductModel productModel){
            binding.setProduct(productModel);
            binding.executePendingBindings();
        }
    }

}
