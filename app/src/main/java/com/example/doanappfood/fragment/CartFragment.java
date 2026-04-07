package com.example.doanappfood.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.doanappfood.R;

public class CartFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Nối file Java này với vỏ giao diện fragment_cart.xml của bạn
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // 2. Tìm cái chữ giá tiền cũ và gạch ngang nó
        TextView tvOldPrice = view.findViewById(R.id.tvOldPrice);
        if (tvOldPrice != null) {
            tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // 3. Trả về toàn bộ view để hiển thị lên màn hình
        // === KHÚC NÀY ĐỂ HIỂN THỊ DANH SÁCH MÓN ĂN ===
        // 1. Ánh xạ cái RecyclerView trong vỏ
        androidx.recyclerview.widget.RecyclerView rvCartItems = view.findViewById(R.id.rvCartItems);
        rvCartItems.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));

        // 2. Tạo một list dữ liệu "ảo" để test giao diện trước khi nối API
        java.util.List<com.example.doanappfood.model.CartItem> dummyList = new java.util.ArrayList<>();
        dummyList.add(new com.example.doanappfood.model.CartItem("Tiện Lợi 1", "1 x Tiện Lợi 1 (gà thường)\n1 x Pepsi (S)", 53000, 1, ""));
        dummyList.add(new com.example.doanappfood.model.CartItem("Gà Rán Yêu Thương 3", "1 x Vị yêu thương\n2 x Pepsi", 142000, 1, ""));
        dummyList.add(new com.example.doanappfood.model.CartItem("Khoai Tây Chiên", "Size L", 30000, 2, ""));

        // 3. Đổ keo dán (Adapter) vào
        com.example.doanappfood.adapter.CartAdapter adapter = new com.example.doanappfood.adapter.CartAdapter(dummyList);
        rvCartItems.setAdapter(adapter);
        // Lắp dây điện cho nút Đặt hàng chuyển sang trang Thanh Toán
        android.widget.Button btnCheckout = view.findViewById(R.id.btnCheckout);
        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new CheckoutFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }
// Bắt sự kiện cho nút Back ở góc trái trên cùng
        ImageView btnBackCart = view.findViewById(R.id.btnBackCart);
        if (btnBackCart != null) {
            btnBackCart.setOnClickListener(v -> {
                if (getActivity() != null) {
                    // Lệnh này sẽ tự động lùi về đúng cái trang mà bạn vừa đứng trước khi bấm vào giỏ hàng
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
        return view;
    }
}