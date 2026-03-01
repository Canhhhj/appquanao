package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.adapter.CartAdapter;
import com.example.myapplication.utils.CartManager;
import com.example.myapplication.utils.UserSession;
import java.text.NumberFormat;
import java.util.Locale;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvTotal, tvEmpty, tvVoucherInfo;
    private Button btnCheckout;
    private CartAdapter cartAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.recycler_cart);
        tvTotal = view.findViewById(R.id.tv_total);
        tvEmpty = view.findViewById(R.id.tv_empty_cart);
        tvVoucherInfo = view.findViewById(R.id.tv_voucher_info);
        btnCheckout = view.findViewById(R.id.btn_checkout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupAdapter();

        btnCheckout.setOnClickListener(v -> {
            if (CartManager.getInstance().getCartItems().isEmpty()) {
                Toast.makeText(getContext(), "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!UserSession.getInstance(requireContext()).isLoggedIn()) {
                Toast.makeText(getContext(), "Vui lòng đăng nhập để đặt hàng", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(requireContext(), LoginActivity.class));
                return;
            }
            startActivity(new Intent(requireContext(), CheckoutActivity.class));
        });

        return view;
    }

    private void setupAdapter() {
        cartAdapter = new CartAdapter(CartManager.getInstance().getCartItems(), this::updateUI);
        recyclerView.setAdapter(cartAdapter);
        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupAdapter();
    }

    private void updateUI() {
        updateTotal();
        updateEmptyState();
    }

    private void updateTotal() {
        CartManager cm = CartManager.getInstance();
        if (tvVoucherInfo != null) {
            if (cm.getAppliedVoucher() != null) {
                tvVoucherInfo.setVisibility(View.VISIBLE);
                tvVoucherInfo.setText("Đang dùng voucher: " + cm.getAppliedVoucher().getCode() + " (" + cm.getAppliedVoucher().getDiscount() + ")");
            } else {
                tvVoucherInfo.setVisibility(View.GONE);
            }
        }
        if (tvTotal == null) return;
        double totalPrice = cm.getTotalPrice();
        double discount = cm.getDiscountAmount();
        double finalTotal = cm.getFinalTotal();
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        if (discount > 0) {
            tvTotal.setText("Tổng sau giảm: " + format.format(finalTotal) + " (đã giảm " + format.format(discount) + ")");
        } else {
            tvTotal.setText("Tổng: " + format.format(totalPrice));
        }
    }

    private void updateEmptyState() {
        View layoutEmpty = getView() != null ? getView().findViewById(R.id.layout_empty_cart) : null;
        if (layoutEmpty == null || recyclerView == null) return;
        if (CartManager.getInstance().getCartItems().isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
