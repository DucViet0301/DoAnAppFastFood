package com.example.doanappfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanappfood.R;
import com.example.doanappfood.model.CartItem;
import com.example.doanappfood.model.CartSauceItem;
import com.example.doanappfood.model.ProductDetailModel;
import com.example.doanappfood.model.ProductModel;
import com.example.doanappfood.model.SaucesModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends  RecyclerView.Adapter<CartAdapter.ViewHolder>{
    private List<CartItem> list;
    public  interface  CartListener {
        void onQuanityChange(int cartId, int quantity, int position);
        void onItemClick(CartItem item);
    }
    private  CartListener listener;
    private final NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
    public CartAdapter(List<CartItem> list, CartListener listener) {
        this.list = list;
        this.listener = listener;
    }
    public  void setItems(List<CartItem> newItems){
        this.list = newItems;
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgProduct;
        TextView tvName, tvDesc, tvSauces, tvQuantity, tvPrice, btnMinus, btnPlus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProductCart);
            tvName      = itemView.findViewById(R.id.tvProductNameCart);
            tvDesc      = itemView.findViewById(R.id.tvProductDesc);
            tvSauces    = itemView.findViewById(R.id.tvSaucesDesc);
            tvQuantity  = itemView.findViewById(R.id.tvQuantity);
            tvPrice     = itemView.findViewById(R.id.tvProductPriceCart);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }
    }
    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        CartItem item = list.get(position);

        holder.tvName.setText(item.getName());
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        refreshPrice(holder, item);

        Glide.with(holder.itemView.getContext()).load(item.getImageUrl()).into(holder.imgProduct);

        if(item.getComboDetail() != null && !item.getComboDetail().isEmpty()){
            String[] parts = item.getComboDetail().split(",");
            StringBuilder builder = new StringBuilder();
            for (String part : parts){
                builder.append(part.trim()).append("\n");
            }
            holder.tvDesc.setText(builder.toString().trim());
            holder.tvDesc.setVisibility(View.VISIBLE);
        }
        else{
            holder.tvDesc.setVisibility(View.GONE);
        }
        if (item.getSauces() != null && !item.getSauces().isEmpty()) {
            java.util.Map<String, Integer> counts = new java.util.HashMap<>();
            for (CartSauceItem s : item.getSauces()) {
                counts.put(s.getName(), counts.getOrDefault(s.getName(), 0) + 1);
            }

            StringBuilder sb = new StringBuilder();
            for (String sauceName : counts.keySet()) {
                sb.append(counts.get(sauceName)).append(" x ").append(sauceName).append("\n");
            }

            holder.tvSauces.setText(sb.toString().trim());
            holder.tvSauces.setVisibility(View.VISIBLE);
        }
        else{
            holder.tvSauces.setVisibility(View.GONE);
        }
        holder.btnMinus.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if(pos == RecyclerView.NO_ID || item.getQuantity() <= 1) return;
            item.setQuantity(item.getQuantity() - 1);
            holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
            refreshPrice(holder, item);
            listener.onQuanityChange(item.getId(), item.getQuantity(), position);
        });
        holder.btnPlus.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if( pos == RecyclerView.NO_ID) return;
            item.setQuantity(item.getQuantity() +1);
            holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
            refreshPrice(holder, item);
            listener.onQuanityChange(item.getId(), item.getQuantity(), position);
        });

        // Click xem chi tiet
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    private void refreshPrice(ViewHolder holder, CartItem item) {
        double unitPirce = (item.getSale_price() > 0  && item.getSale_price() < item.getList_price())
                ? item.getSale_price() :
                item.getList_price();
        holder.tvPrice.setText(fmt.format(unitPirce) + " đ");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}