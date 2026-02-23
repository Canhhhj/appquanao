package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.utils.CartManager;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartList;
    private Runnable onCartChanged;

    public CartAdapter(List<CartItem> cartList, Runnable onCartChanged) {
        this.cartList = cartList;
        this.onCartChanged = onCartChanged;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartList.get(position);
        holder.tvName.setText(item.getProduct().getName());
        holder.tvQty.setText(String.valueOf(item.getQuantity()));

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(format.format(item.getProduct().getPrice() * item.getQuantity()));

        Glide.with(holder.itemView.getContext())
                .load(item.getProduct().getImage())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.imgProduct);

        // Nút +
        holder.btnPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
            if (onCartChanged != null) onCartChanged.run();
        });

        // Nút -
        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
            } else {
                CartManager.getInstance().removeFromCart(item);
                cartList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartList.size());
            }
            if (onCartChanged != null) onCartChanged.run();
        });

        // Nút xóa
        holder.btnRemove.setOnClickListener(v -> {
            CartManager.getInstance().removeFromCart(item);
            cartList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartList.size());
            if (onCartChanged != null) onCartChanged.run();
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQty;
        ImageView imgProduct, btnRemove;
        Button btnPlus, btnMinus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.cart_item_name);
            tvPrice = itemView.findViewById(R.id.cart_item_price);
            tvQty = itemView.findViewById(R.id.cart_item_qty);
            imgProduct = itemView.findViewById(R.id.cart_item_image);
            btnRemove = itemView.findViewById(R.id.btn_remove);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
        }
    }
}
