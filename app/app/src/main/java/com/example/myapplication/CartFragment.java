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
    private TextView tvTotal, tvEmpty;
    private Button btnCheckout;
    private CartAdapter cartAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.recycler_cart);
        tvTotal = view.findViewById(R.id.tv_total);
        tvEmpty = view.findViewById(R.id.tv_empty_cart);
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
        if (tvTotal == null) return;
        double totalPrice = CartManager.getInstance().getTotalPrice();
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotal.setText("Tổng: " + format.format(totalPrice));
    }

    private void updateEmptyState() {
        if (tvEmpty == null || recyclerView == null) return;
        if (CartManager.getInstance().getCartItems().isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
