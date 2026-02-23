package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Voucher;

import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {

    public interface OnVoucherClickListener {
        void onVoucherClick(Voucher voucher);
    }

    private final List<Voucher> voucherList;
    private final OnVoucherClickListener listener;

    public VoucherAdapter(List<Voucher> voucherList, OnVoucherClickListener listener) {
        this.voucherList = voucherList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher v = voucherList.get(position);
        holder.tvCode.setText(v.getCode());
        holder.tvDiscount.setText(v.getDiscount());
        holder.tvExpiry.setText("Hạn: " + v.getExpiry());
        holder.tvUsage.setText("Đã dùng " + v.getUsed() + "/" + v.getLimit());

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onVoucherClick(v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    static class VoucherViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvDiscount, tvExpiry, tvUsage;

        VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tv_voucher_code);
            tvDiscount = itemView.findViewById(R.id.tv_voucher_discount);
            tvExpiry = itemView.findViewById(R.id.tv_voucher_expiry);
            tvUsage = itemView.findViewById(R.id.tv_voucher_usage);
        }
    }
}

