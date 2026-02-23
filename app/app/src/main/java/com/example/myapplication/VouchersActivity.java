package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.adapter.VoucherAdapter;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.model.Voucher;
import com.example.myapplication.utils.CartManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VouchersActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private final List<Voucher> vouchers = new ArrayList<>();
    private VoucherAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vouchers);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Kho voucher");
        }

        swipeRefreshLayout = findViewById(R.id.swipe_vouchers);
        recyclerView = findViewById(R.id.recycler_vouchers);
        tvEmpty = findViewById(R.id.tv_empty_vouchers);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VoucherAdapter(vouchers, this::onVoucherSelected);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::loadVouchers);

        loadVouchers();
    }

    private void loadVouchers() {
        swipeRefreshLayout.setRefreshing(true);

        RetrofitClient.getService().getVouchers().enqueue(new Callback<List<Voucher>>() {
            @Override
            public void onResponse(Call<List<Voucher>> call, Response<List<Voucher>> response) {
                swipeRefreshLayout.setRefreshing(false);
                vouchers.clear();

                if (response.isSuccessful() && response.body() != null) {
                    Date today = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                    for (Voucher v : response.body()) {
                        if (!"Hoạt động".equals(v.getStatus())) continue;
                        if (v.getUsed() >= v.getLimit()) continue;

                        try {
                            Date expiry = sdf.parse(v.getExpiry());
                            if (expiry != null && expiry.before(today)) {
                                continue;
                            }
                        } catch (ParseException e) {
                            // Nếu parse lỗi thì vẫn cho hiển thị
                        }

                        vouchers.add(v);
                    }
                } else {
                    Toast.makeText(VouchersActivity.this,
                            "Không tải được voucher (mã: " + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }

                adapter.notifyDataSetChanged();
                updateEmptyState();
            }

            @Override
            public void onFailure(Call<List<Voucher>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(VouchersActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                updateEmptyState();
            }
        });
    }

    private void updateEmptyState() {
        if (vouchers.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void onVoucherSelected(Voucher voucher) {
        CartManager.getInstance().applyVoucher(voucher);
        Toast.makeText(this,
                "Đã áp dụng voucher " + voucher.getCode(),
                Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

