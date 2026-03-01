package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.model.Banner;
import com.example.myapplication.model.Voucher;
import com.example.myapplication.utils.CartManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveFragment extends Fragment {

    private ImageView imgHighlight;
    private TextView tvHighlightTitle;
    private TextView tvEmptyLive;
    private RecyclerView recyclerPromotions;
    private final List<Voucher> voucherList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live, container, false);

        imgHighlight = view.findViewById(R.id.img_highlight);
        tvHighlightTitle = view.findViewById(R.id.tv_highlight_title);
        tvEmptyLive = view.findViewById(R.id.tv_empty_live);
        recyclerPromotions = view.findViewById(R.id.recycler_promotions);
        recyclerPromotions.setLayoutManager(new LinearLayoutManager(getContext()));

        loadHighlightBanner();
        loadPromotions();

        return view;
    }

    private void loadHighlightBanner() {
        RetrofitClient.getService().getBanners().enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Banner b = response.body().get(0);
                    tvHighlightTitle.setText(b.getTitle());

                    com.bumptech.glide.Glide.with(requireContext())
                            .load(b.getImageUrl())
                            .centerCrop()
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .into(imgHighlight);
                }
            }

            @Override
            public void onFailure(Call<List<Banner>> call, Throwable t) {
                // ignore
            }
        });
    }

    private void loadPromotions() {
        RetrofitClient.getService().getVouchers().enqueue(new Callback<List<Voucher>>() {
            @Override
            public void onResponse(Call<List<Voucher>> call, Response<List<Voucher>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    voucherList.clear();
                    voucherList.addAll(response.body());
                    recyclerPromotions.setAdapter(new LiveVoucherAdapter(voucherList, LiveFragment.this::onVoucherSelected));
                    if (tvEmptyLive != null) tvEmptyLive.setVisibility(View.GONE);
                    recyclerPromotions.setVisibility(View.VISIBLE);
                } else {
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<List<Voucher>> call, Throwable t) {
                Toast.makeText(getContext(), "Không tải được khuyến mãi", Toast.LENGTH_SHORT).show();
                showEmptyState();
            }
        });
    }

    private void showEmptyState() {
        if (tvEmptyLive != null) {
            tvEmptyLive.setVisibility(View.VISIBLE);
        }
        if (recyclerPromotions != null) {
            recyclerPromotions.setVisibility(View.GONE);
        }
    }

    private void onVoucherSelected(Voucher voucher) {
        if (getContext() == null || voucher == null) return;
        CartManager.getInstance().applyVoucher(voucher);
        Toast.makeText(getContext(),
                "Đã áp dụng voucher " + voucher.getCode() + ". Vào giỏ hàng/checkout để xem giảm giá.",
                Toast.LENGTH_SHORT).show();
    }

    // Adapter hiển thị voucher trong tab Khám Phá + click áp dụng
    private static class LiveVoucherAdapter extends RecyclerView.Adapter<LiveVoucherAdapter.ViewHolder> {

        interface OnVoucherClickListener {
            void onVoucherClick(Voucher voucher);
        }

        private final List<Voucher> data;
        private final OnVoucherClickListener listener;

        LiveVoucherAdapter(List<Voucher> data, OnVoucherClickListener listener) {
            this.data = data;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_live_voucher, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Voucher v = data.get(position);
            holder.tvTitle.setText(v.getCode() + " - " + v.getDiscount());
            holder.tvDesc.setText("Giới hạn: " + v.getLimit() + " • Đã dùng: " + v.getUsed());

            holder.itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onVoucherClick(v);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDesc;
            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_live_voucher_title);
                tvDesc = itemView.findViewById(R.id.tv_live_voucher_desc);
            }
        }
    }
}
