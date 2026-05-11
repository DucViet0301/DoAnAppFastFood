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
import com.example.doanappfood.model.CartSauceItem;
import com.example.doanappfood.model.OrderDetailModel;
import com.example.doanappfood.model.ProductDetailModel;
import com.example.doanappfood.model.ProductModel;
import com.example.doanappfood.model.SaucesModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    private Context context;
    private List<ProductDetailModel> items;

    public OrderDetailAdapter(Context context, List<ProductDetailModel> items) {
        this.context = context;
        this.items = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private TextView tvProductName, tvQuantityOrder, tvProductPrice, tvProductDesc, tvSaucesDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantityOrder = itemView.findViewById(R.id.tvQuantityOrder);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductDesc = itemView.findViewById(R.id.tvProductDesc);
            tvSaucesDesc = itemView.findViewById(R.id.tvSaucesDesc);

        }
    }

    @NonNull
    @Override
    public OrderDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductDetailModel item = items.get(position);

        holder.tvProductName.setText(item.getName());
        holder.tvQuantityOrder.setText("x" + item.getQuantity());
        holder.tvProductPrice.setText(formatPrice(item.getPrice()));
        // Sub-items
        if (holder.tvProductDesc != null) {
            if (item.isCombo()
                    && item.getProductModels() != null
                    && !item.getProductModels().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (var product : item.getProductModels()) {
                    sb.append(product.getQuantity()).append(" x ").append(product.getName()).append("\n");
                }
                holder.tvProductDesc.setText(sb.toString().trim());
                holder.tvProductDesc.setVisibility(View.VISIBLE);
            } else {
                holder.tvProductDesc.setVisibility(View.GONE);
            }
        }
        if (holder.tvSaucesDesc != null) {
            if (item.getSaucesModel() != null && !item.getSaucesModel().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (SaucesModel sauce : item.getSaucesModel()) {
                    sb.append(sauce.getQuantity()).append(" x ").append(sauce.getName()).append("\n");
                }
                holder.tvSaucesDesc.setText(sb.toString().trim());
                holder.tvSaucesDesc.setVisibility(View.VISIBLE);
            } else {
                holder.tvSaucesDesc.setVisibility(View.GONE);
            }
        }

        Glide.with(context)
                .load(item.getImage())
                .placeholder(R.drawable.avatar)
                .into(holder.ivProductImage);

    }

    private String formatPrice(double price) {
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return nf.format(price) + " đ";
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
