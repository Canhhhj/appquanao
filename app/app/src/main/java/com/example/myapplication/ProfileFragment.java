package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.myapplication.utils.UserSession;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        UserSession session = UserSession.getInstance(requireContext());

        TextView tvName = view.findViewById(R.id.tv_profile_name);
        TextView tvEmail = view.findViewById(R.id.tv_profile_email);
        Button btnLogout = view.findViewById(R.id.btn_logout);
        View tvOrders = view.findViewById(R.id.tv_my_orders);
        View tvVouchers = view.findViewById(R.id.tv_my_vouchers);

        if (session.isLoggedIn()) {
            // Hiển thị thông tin user đã đăng nhập
            if (tvName != null) tvName.setText(session.getUserName());
            if (tvEmail != null) tvEmail.setText(session.getUserEmail());
            if (btnLogout != null) {
                btnLogout.setText("ĐĂNG XUẤT");
                btnLogout.setOnClickListener(v -> {
                    session.logout();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
            }
        } else {
            // Chưa đăng nhập
            if (tvName != null) tvName.setText("Khách");
            if (tvEmail != null) tvEmail.setText("Chưa đăng nhập");
            if (btnLogout != null) {
                btnLogout.setText("ĐĂNG NHẬP");
                btnLogout.setBackgroundTintList(
                    requireContext().getColorStateList(android.R.color.holo_orange_dark));
                btnLogout.setOnClickListener(v -> {
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                });
            }
        }

        // Đơn mua - mở màn hình danh sách đơn hàng nếu đã đăng nhập
        if (tvOrders != null) {
            tvOrders.setOnClickListener(v -> {
                if (!session.isLoggedIn()) {
                    android.widget.Toast.makeText(requireContext(),
                        "Vui lòng đăng nhập để xem đơn hàng", android.widget.Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                } else {
                    startActivity(new Intent(requireContext(), MyOrdersActivity.class));
                }
            });
        }

        // Kho voucher - hiển thị danh sách voucher
        if (tvVouchers != null) {
            tvVouchers.setOnClickListener(v -> {
                if (!session.isLoggedIn()) {
                    android.widget.Toast.makeText(requireContext(),
                            "Vui lòng đăng nhập để xem voucher", android.widget.Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                } else {
                    startActivity(new Intent(requireContext(), VouchersActivity.class));
                }
            });
        }

        return view;
    }
}
