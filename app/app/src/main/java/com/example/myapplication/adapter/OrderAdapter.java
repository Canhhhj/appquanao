package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Order;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<Order> orderList;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvId.setText(order.getId() != null ? order.getId() : "-");
        holder.tvDate.setText(order.getDate() != null ? order.getDate() : "");
        holder.tvStatus.setText(order.getStatus() != null ? order.getStatus() : "");

        if (holder.tvPhone != null) {
            if (order.getPhone() != null && !order.getPhone().isEmpty()) {
                holder.tvPhone.setVisibility(View.VISIBLE);
                holder.tvPhone.setText("SĐT: " + order.getPhone());
            } else {
                holder.tvPhone.setVisibility(View.GONE);
            }
        }
        if (holder.tvAddress != null) {
            if (order.getAddress() != null && !order.getAddress().isEmpty()) {
                holder.tvAddress.setVisibility(View.VISIBLE);
                holder.tvAddress.setText("Địa chỉ: " + order.getAddress());
            } else {
                holder.tvAddress.setVisibility(View.GONE);
            }
        }

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvTotal.setText(format.format(order.getTotal()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOrderClick(order);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvDate, tvStatus, tvTotal, tvAddress, tvPhone;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_order_id);
            tvDate = itemView.findViewById(R.id.tv_order_date);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
            tvTotal = itemView.findViewById(R.id.tv_order_total);
            tvAddress = itemView.findViewById(R.id.tv_order_address);
            tvPhone = itemView.findViewById(R.id.tv_order_phone);
        }
    }
}

