package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.Address;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressVH> {

    private final List<Address> list;
    private final OnSelectListener listener;

    public interface OnSelectListener {
        void onSelect(Address address);
    }

    public AddressAdapter(List<Address> list, OnSelectListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressVH holder, int position) {
        Address a = list.get(position);
        holder.tvLabel.setText(a.getLabel() != null ? a.getLabel() : "Địa chỉ");
        holder.tvFull.setText(a.getFullAddress() != null ? a.getFullAddress() : "");
        holder.tvPhone.setText(a.getPhone() != null ? "SĐT: " + a.getPhone() : "");
        holder.btnSelect.setOnClickListener(v -> {
            if (listener != null) listener.onSelect(a);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class AddressVH extends RecyclerView.ViewHolder {
        TextView tvLabel, tvFull, tvPhone;
        Button btnSelect;

        AddressVH(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tv_address_label);
            tvFull = itemView.findViewById(R.id.tv_address_full);
            tvPhone = itemView.findViewById(R.id.tv_address_phone);
            btnSelect = itemView.findViewById(R.id.btn_select_address_item);
        }
    }
}
