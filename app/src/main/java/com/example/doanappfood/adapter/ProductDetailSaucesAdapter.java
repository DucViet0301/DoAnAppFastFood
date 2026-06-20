package com.example.doanappfood.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanappfood.databinding.ItemFeverBinding;
import com.example.doanappfood.model.SaucesModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductDetailSaucesAdapter extends RecyclerView.Adapter<ProductDetailSaucesAdapter.MyViewHolder>{
    private List<SaucesModel> list;
    private  OnProductSaucesClickListener listener;
    public  interface OnProductSaucesClickListener{
        void OnClick(SaucesModel saucesModel, boolean isSelected);
    }
    public  void setOnProductSaucesClickListener(OnProductSaucesClickListener listener){
        this.listener = listener;
    }


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
        SaucesModel item = list.get(position);
        holder.setBinding(list.get(position));
        double price = Double.parseDouble(list.get(position).getPrice());

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(price);

        holder.binding.tvPriceDisplay.setText("+ " + formattedPrice + " đ");
        Glide.with(holder.itemView).load(list.get(position).getImage()).into(holder.binding.imgFever);

        holder.binding.getRoot().setSelected(item.isSelected());
        holder.binding.setIsSelected(item.isSelected());

        holder.itemView.setOnClickListener(v-> {
            item.setQuantity(item.getQuantity() + 1);
            holder.binding.setFever(item);
            holder.binding.setIsSelected(item.getQuantity() > 0);
            holder.binding.executePendingBindings();

            if (listener != null) {
                listener.OnClick(item, true);
            }
        });

        holder.binding.btnRemoveSauce.setOnClickListener(v -> {
            int currentQty = item.getQuantity();
            if (currentQty > 0) {
                int newQty = currentQty - 1;
                item.setQuantity(newQty);

                boolean isStillSelected = newQty > 0;
                holder.binding.setIsSelected(isStillSelected);

                holder.binding.executePendingBindings();


                if (listener != null) {
                    listener.OnClick(item, false);
                }
            }
        });
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
    public  List<SaucesModel> getSelected(){
        List<SaucesModel> selected = new ArrayList<>();
        for(SaucesModel item : list){
            if(item.isSelected()){
                selected.add(item);
            }
        }
        return  selected;
    }
}
