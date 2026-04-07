package com.example.doanappfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doanappfood.R;
import com.example.doanappfood.model.CartItem;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItemList;

    // Hàm nhận dữ liệu từ ngoài truyền vào
    public CartAdapter(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nạp cái "Lõi" item_cart.xml vào đây
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);
        if (item == null) return;

        // Đổ dữ liệu vào các thẻ
        holder.tvName.setText(item.getProductName());
        holder.tvDesc.setText(item.getDescription());
        holder.tvPrice.setText(item.getPrice() + " đ");
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // Gắn sự kiện bấm dấu Cộng (+)
        holder.btnPlus.setOnClickListener(v -> {
            int currentQty = Integer.parseInt(holder.tvQuantity.getText().toString());
            holder.tvQuantity.setText(String.valueOf(currentQty + 1));
        });

        // Gắn sự kiện bấm dấu Trừ (-)
        holder.btnMinus.setOnClickListener(v -> {
            int currentQty = Integer.parseInt(holder.tvQuantity.getText().toString());
            if (currentQty > 1) { // Không cho giảm xuống dưới 1
                holder.tvQuantity.setText(String.valueOf(currentQty - 1));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (cartItemList != null) return cartItemList.size();
        return 0;
    }

    // Class con để ánh xạ các thành phần trong 1 hộp món ăn
    public class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvDesc, tvPrice, tvQuantity, btnMinus, btnPlus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProductCart);
            tvName = itemView.findViewById(R.id.tvProductNameCart);
            tvDesc = itemView.findViewById(R.id.tvProductDesc);
            tvPrice = itemView.findViewById(R.id.tvProductPriceCart);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }
    }
}