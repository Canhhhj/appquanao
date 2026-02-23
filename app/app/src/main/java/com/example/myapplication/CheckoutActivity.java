package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.Order;
import com.example.myapplication.utils.CartManager;
import com.example.myapplication.utils.UserSession;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvTotal, tvUserName, tvUserEmail;
    private Button btnConfirmOrder;
    private NumberFormat format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Xác nhận đơn hàng");
        }

        format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        tvTotal = findViewById(R.id.tv_checkout_total);
        tvUserName = findViewById(R.id.tv_checkout_name);
        tvUserEmail = findViewById(R.id.tv_checkout_email);
        btnConfirmOrder = findViewById(R.id.btn_confirm_order);

        UserSession session = UserSession.getInstance(this);
        tvUserName.setText("Người nhận: " + session.getUserName());
        tvUserEmail.setText("Email: " + session.getUserEmail());

        updateTotal();

        btnConfirmOrder.setOnClickListener(v -> placeOrder());
    }

    private void updateTotal() {
        double total = CartManager.getInstance().getFinalTotal();
        double discount = CartManager.getInstance().getDiscountAmount();

        if (discount > 0) {
            tvTotal.setText("Tổng sau giảm: " + format.format(total) +
                    " (Đã giảm " + format.format(discount) + ")");
        } else {
            tvTotal.setText("Tổng tiền: " + format.format(total));
        }
    }

    private void placeOrder() {
        UserSession session = UserSession.getInstance(this);
        List<CartItem> cartItems = CartManager.getInstance().getCartItems();

        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnConfirmOrder.setEnabled(false);
        btnConfirmOrder.setText("Đang đặt hàng...");

        Order order = new Order();
        order.setCustomer(session.getUserName());
        order.setDate(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()));
        order.setTotal(CartManager.getInstance().getFinalTotal());
        order.setStatus("Chờ xác nhận");
        order.setPayment("COD");

        List<Order.OrderItem> items = new ArrayList<>();
        for (CartItem ci : cartItems) {
            Order.OrderItem oi = new Order.OrderItem();
            oi.setName(ci.getProduct().getName());
            oi.setPrice(ci.getProduct().getPrice());
            oi.setQuantity(ci.getQuantity());
            oi.setImage(ci.getProduct().getImage());
            items.add(oi);
        }
        order.setItems(items);

        RetrofitClient.getService().createOrder(order).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> res) {
                if (res.isSuccessful()) {
                    CartManager.getInstance().clearCart();
                    Toast.makeText(CheckoutActivity.this, "🎉 Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    btnConfirmOrder.setEnabled(true);
                    btnConfirmOrder.setText("XÁC NHẬN ĐẶT HÀNG");
                    Toast.makeText(CheckoutActivity.this, "Lỗi đặt hàng (mã: " + res.code() + "), thử lại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                btnConfirmOrder.setEnabled(true);
                btnConfirmOrder.setText("XÁC NHẬN ĐẶT HÀNG");
                Toast.makeText(CheckoutActivity.this, "Mất kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
