package com.example.doanappfood.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanappfood.databinding.ItemFeverBinding;
import com.example.doanappfood.model.SaucesModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductDetailSaucesAdapter extends RecyclerView.Adapter<ProductDetailSaucesAdapter.MyViewHolder>{
    private List<SaucesModel> list;

    public ProductDetailSaucesAdapter(List<SaucesModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFeverBinding binding = ItemFeverBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setBinding(list.get(position));
        double price = Double.parseDouble(list.get(position).getPrice());

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(price);

        holder.binding.tvPrice.setText("+ " + formattedPrice + " đ");
        Glide.with(holder.itemView).load(list.get(position).getImage()).into(holder.binding.imgFever);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ItemFeverBinding binding;

        public MyViewHolder(ItemFeverBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        private void setBinding(SaucesModel saucesModel){
            binding.setFever(saucesModel);
            binding.executePendingBindings();
        }
    }
}
