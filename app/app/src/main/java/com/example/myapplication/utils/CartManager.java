package com.example.myapplication.utils;

import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.Product;
import com.example.myapplication.model.Voucher;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;
    private Voucher appliedVoucher;

    private CartManager() {
        cartItems = new ArrayList<>();
        appliedVoucher = null;
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(Product product, int quantity) {
        // Check if exists
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        cartItems.add(new CartItem(product, quantity));
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }
    
    public void removeFromCart(CartItem item) {
        cartItems.remove(item);
    }
    
    public void clearCart() {
        cartItems.clear();
        appliedVoucher = null;
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }

    public void applyVoucher(Voucher voucher) {
        this.appliedVoucher = voucher;
    }

    public void clearVoucher() {
        this.appliedVoucher = null;
    }

    public Voucher getAppliedVoucher() {
        return appliedVoucher;
    }

    public double getDiscountAmount() {
        if (appliedVoucher == null) return 0;
        if (appliedVoucher.getDiscount() == null) return 0;

        String discountStr = appliedVoucher.getDiscount().trim();
        if (discountStr.endsWith("%")) {
            try {
                String percentStr = discountStr.replace("%", "").trim();
                double percent = Double.parseDouble(percentStr);
                return getTotalPrice() * percent / 100.0;
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        // Các loại voucher khác (Freeship, ...) hiện chưa áp dụng giảm tiền
        return 0;
    }

    public double getFinalTotal() {
        double total = getTotalPrice();
        double discount = getDiscountAmount();
        double finalTotal = total - discount;
        return finalTotal < 0 ? 0 : finalTotal;
    }
}
