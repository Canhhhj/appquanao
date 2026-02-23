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

import com.example.myapplication.adapter.OrderAdapter;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.model.Order;
import com.example.myapplication.utils.UserSession;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrdersActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private OrderAdapter adapter;
    private final List<Order> myOrders = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Đơn mua của tôi");
        }

        swipeRefreshLayout = findViewById(R.id.swipe_orders);
        recyclerView = findViewById(R.id.recycler_orders);
        tvEmpty = findViewById(R.id.tv_empty_orders);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(myOrders);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::loadOrders);

        loadOrders();
    }

    private void loadOrders() {
        swipeRefreshLayout.setRefreshing(true);
        String currentCustomer = UserSession.getInstance(this).getUserName();

        RetrofitClient.getService().getOrders().enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                swipeRefreshLayout.setRefreshing(false);
                myOrders.clear();

                if (response.isSuccessful() && response.body() != null) {
                    for (Order o : response.body()) {
                        if (o.getCustomer() != null && o.getCustomer().equals(currentCustomer)) {
                            myOrders.add(o);
                        }
                    }
                } else {
                    Toast.makeText(MyOrdersActivity.this,
                            "Không tải được đơn hàng (mã: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }

                adapter.notifyDataSetChanged();
                updateEmptyState();
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MyOrdersActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                updateEmptyState();
            }
        });
    }

    private void updateEmptyState() {
        if (myOrders.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

