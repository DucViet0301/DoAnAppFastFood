package com.example.doanappfood.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.doanappfood.R;

public class CheckoutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);

        // NÚT BACK
        ImageView btnBack = view.findViewById(R.id.btnBackCheckout);
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // BẢNG CHỌN PHƯƠNG THỨC THANH TOÁN
        androidx.cardview.widget.CardView cardPaymentMethod = view.findViewById(R.id.cardPaymentMethod);
        android.widget.TextView tvSelectedPayment = view.findViewById(R.id.tvSelectedPayment);

        if (cardPaymentMethod != null) {
            cardPaymentMethod.setOnClickListener(v -> {
                com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog =
                        new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());

                View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.layout_payment_bottom_sheet, null);
                bottomSheetDialog.setContentView(sheetView);

                android.widget.LinearLayout layoutCOD = sheetView.findViewById(R.id.layoutCOD);
                layoutCOD.setOnClickListener(v1 -> {
                    tvSelectedPayment.setText("Tiền mặt (COD)");
                    tvSelectedPayment.setTextColor(android.graphics.Color.parseColor("#E53935"));
                    bottomSheetDialog.dismiss();
                });

                android.widget.LinearLayout layoutMoMo = sheetView.findViewById(R.id.layoutMoMo);
                layoutMoMo.setOnClickListener(v12 -> {
                    tvSelectedPayment.setText("Ví MoMo");
                    tvSelectedPayment.setTextColor(android.graphics.Color.parseColor("#A50064"));
                    bottomSheetDialog.dismiss();
                });

                bottomSheetDialog.show();
            });
        }

        // Đã xóa bỏ sự kiện click chuyển sang AddressFragment theo ý nhóm trưởng !

        return view;
    }
}