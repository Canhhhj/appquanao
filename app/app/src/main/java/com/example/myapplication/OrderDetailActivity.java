package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.model.Order;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "order_id";

    private TextView tvOrderId, tvOrderDate, tvOrderAddress, tvOrderPhone, tvOrderTotal;
    private LinearLayout layoutStatusTimeline;
    private RecyclerView recyclerOrderItems;

    private static final String[] STATUS_STEPS = {
        "Chờ xác nhận",
        "Đang xử lý",
        "Đang giao",
        "Đã giao"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết đơn hàng");
        }

        tvOrderId = findViewById(R.id.tv_order_id);
        tvOrderDate = findViewById(R.id.tv_order_date);
        tvOrderAddress = findViewById(R.id.tv_order_address);
        tvOrderPhone = findViewById(R.id.tv_order_phone);
        tvOrderTotal = findViewById(R.id.tv_order_total);
        layoutStatusTimeline = findViewById(R.id.layout_status_timeline);
        recyclerOrderItems = findViewById(R.id.recycler_order_items);

        String orderId = getIntent() != null ? getIntent().getStringExtra(EXTRA_ORDER_ID) : null;
        if (orderId == null || orderId.isEmpty()) {
            finish();
            return;
        }

        loadOrder(orderId);
    }

    private void loadOrder(String orderId) {
        RetrofitClient.getService().getOrderById(orderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bindOrder(response.body());
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                // Fallback: load all orders and find by id
                RetrofitClient.getService().getOrders().enqueue(new Callback<List<Order>>() {
                    @Override
                    public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            for (Order o : response.body()) {
                                if (orderId.equals(o.getId())) {
                                    bindOrder(o);
                                    return;
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Order>> call, Throwable t2) {}
                });
            }
        });
    }

    private void bindOrder(Order order) {
        tvOrderId.setText("Đơn hàng #" + (order.getId() != null ? order.getId() : ""));
        tvOrderDate.setText(order.getDate() != null ? order.getDate() : "");
        tvOrderAddress.setText(order.getAddress() != null ? "Địa chỉ: " + order.getAddress() : "");
        tvOrderPhone.setText(order.getPhone() != null ? "SĐT: " + order.getPhone() : "");
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvOrderTotal.setText("Tổng: " + format.format(order.getTotal()));

        buildStatusTimeline(order.getStatus());
        bindOrderItems(order.getItems());
    }

    private void buildStatusTimeline(String currentStatus) {
        layoutStatusTimeline.removeAllViews();
        boolean isCancelled = "Đã hủy".equals(currentStatus);
        int currentIndex = -1;
        if (!isCancelled) {
            for (int i = 0; i < STATUS_STEPS.length; i++) {
                if (STATUS_STEPS[i].equals(currentStatus)) {
                    currentIndex = i;
                    break;
                }
            }
        }

        for (int i = 0; i < STATUS_STEPS.length; i++) {
            View row = LayoutInflater.from(this).inflate(R.layout.item_order_detail_status, layoutStatusTimeline, false);
            View dot = row.findViewById(R.id.dot_status);
            TextView label = row.findViewById(R.id.tv_status_label);
            TextView done = row.findViewById(R.id.tv_status_done);

            label.setText(STATUS_STEPS[i]);
            boolean doneState = currentIndex >= i;
            if (doneState) {
                dot.setBackgroundResource(R.drawable.circle_dot_done);
                done.setVisibility(View.VISIBLE);
            }
            if (isCancelled && i < STATUS_STEPS.length - 1) {
                done.setVisibility(View.GONE);
            }
            layoutStatusTimeline.addView(row);
        }

        if (isCancelled) {
            View row = LayoutInflater.from(this).inflate(R.layout.item_order_detail_status, layoutStatusTimeline, false);
            View dot = row.findViewById(R.id.dot_status);
            TextView label = row.findViewById(R.id.tv_status_label);
            TextView done = row.findViewById(R.id.tv_status_done);
            dot.setBackgroundResource(R.drawable.circle_dot_done);
            label.setText("Đã hủy");
            done.setVisibility(View.VISIBLE);
            layoutStatusTimeline.addView(row);
        }
    }

    private void bindOrderItems(List<Order.OrderItem> items) {
        final List<Order.OrderItem> list = items != null ? items : new ArrayList<>();
        recyclerOrderItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerOrderItems.setAdapter(new RecyclerView.Adapter<OrderDetailActivity.OrderItemVH>() {
            @NonNull
            @Override
            public OrderDetailActivity.OrderItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_product, parent, false);
                return new OrderDetailActivity.OrderItemVH(v);
            }
            @Override
            public void onBindViewHolder(@NonNull OrderDetailActivity.OrderItemVH holder, int position) {
                Order.OrderItem item = list.get(position);
                holder.tvName.setText(item.getName());
                NumberFormat f = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                holder.tvQtyPrice.setText("x" + item.getQuantity() + " - " + f.format(item.getPrice() * item.getQuantity()));
                if (item.getImage() != null && !item.getImage().isEmpty()) {
                    Glide.with(OrderDetailActivity.this).load(item.getImage()).placeholder(android.R.drawable.ic_menu_gallery).into(holder.img);
                }
            }
            @Override
            public int getItemCount() { return list.size(); }
        });
    }

    static class OrderItemVH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvQtyPrice;
        OrderItemVH(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_order_item);
            tvName = itemView.findViewById(R.id.tv_order_item_name);
            tvQtyPrice = itemView.findViewById(R.id.tv_order_item_qty_price);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
