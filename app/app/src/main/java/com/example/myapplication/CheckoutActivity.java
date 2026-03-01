package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.model.CartItem;
import com.example.myapplication.model.Order;
import com.example.myapplication.model.Voucher;
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
    private EditText etAddress, etPhone;
    private RadioGroup radioPayment;
    private Button btnConfirmOrder;
    private NumberFormat format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Toolbar toolbar = findViewById(R.id.toolbar_checkout);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thanh toán");
        }

        format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        tvTotal = findViewById(R.id.tv_checkout_total);
        tvUserName = findViewById(R.id.tv_checkout_name);
        tvUserEmail = findViewById(R.id.tv_checkout_email);
        etAddress = findViewById(R.id.et_checkout_address);
        etPhone = findViewById(R.id.et_checkout_phone);
        radioPayment = findViewById(R.id.radio_payment);
        btnConfirmOrder = findViewById(R.id.btn_confirm_order);

        UserSession session = UserSession.getInstance(this);
        tvUserName.setText("Người nhận: " + session.getUserName());
        tvUserEmail.setText("Email: " + session.getUserEmail());

        updateTotal();

        Button btnSelectAddress = findViewById(R.id.btn_select_address);
        if (btnSelectAddress != null) {
            btnSelectAddress.setOnClickListener(v -> {
                Intent i = new Intent(CheckoutActivity.this, AddressActivity.class);
                startActivityForResult(i, 100);
            });
        }

        btnConfirmOrder.setOnClickListener(v -> placeOrder());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String address = data.getStringExtra(AddressActivity.EXTRA_ADDRESS);
            String phone = data.getStringExtra(AddressActivity.EXTRA_PHONE);
            if (address != null) etAddress.setText(address);
            if (phone != null) etPhone.setText(phone);
        }
    }

    private void updateTotal() {
        double total = CartManager.getInstance().getFinalTotal();
        double discount = CartManager.getInstance().getDiscountAmount();

        if (discount > 0) {
            tvTotal.setText("Tổng sau giảm: " + format.format(total) +
                    " (Đã giảm " + format.format(discount) + ")");
        } else {
            tvTotal.setText(format.format(total));
        }
    }

    private String getSelectedPayment() {
        int id = radioPayment.getCheckedRadioButtonId();
        if (id == R.id.radio_cod) return "Thanh toán khi nhận hàng (COD)";
        if (id == R.id.radio_transfer) return "Chuyển khoản ngân hàng";
        if (id == R.id.radio_ewallet) return "Ví điện tử (MoMo, ZaloPay, VNPay)";
        return "COD";
    }

    private void placeOrder() {
        String address = etAddress.getText().toString().trim();
        if (address.isEmpty()) {
            etAddress.setError("Vui lòng nhập địa chỉ giao hàng");
            etAddress.requestFocus();
            return;
        }

        String phone = etPhone.getText().toString().trim().replaceAll("\\s", "");
        if (phone.isEmpty()) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            etPhone.requestFocus();
            return;
        }
        if (phone.length() < 10 || phone.length() > 11) {
            etPhone.setError("Số điện thoại 10–11 số");
            etPhone.requestFocus();
            return;
        }

        UserSession session = UserSession.getInstance(this);
        List<CartItem> cartItems = CartManager.getInstance().getCartItems();

        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnConfirmOrder.setEnabled(false);
        btnConfirmOrder.setText("Đang đặt hàng...");

        Order order = new Order();
        order.setUserId(session.getUserId());
        order.setCustomer(session.getUserName());
        order.setDate(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()));
        order.setTotal(CartManager.getInstance().getFinalTotal());
        order.setStatus("Chờ xác nhận");
        order.setPayment(getSelectedPayment());
        order.setAddress(address);
        order.setPhone(phone);

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

        final Voucher appliedVoucher = CartManager.getInstance().getAppliedVoucher();

        RetrofitClient.getService().createOrder(order).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> res) {
                if (res.isSuccessful()) {
                    if (appliedVoucher != null && appliedVoucher.getId() != null) {
                        appliedVoucher.setUsed(appliedVoucher.getUsed() + 1);
                        RetrofitClient.getService().updateVoucher(appliedVoucher.getId(), appliedVoucher)
                                .enqueue(new Callback<Voucher>() {
                                    @Override
                                    public void onResponse(Call<Voucher> c, Response<Voucher> r) { }
                                    @Override
                                    public void onFailure(Call<Voucher> c, Throwable t) { }
                                });
                    }
                    CartManager.getInstance().clearCart();
                    Toast.makeText(CheckoutActivity.this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    btnConfirmOrder.setEnabled(true);
                    btnConfirmOrder.setText("Xác nhận đặt hàng");
                    Toast.makeText(CheckoutActivity.this, "Lỗi đặt hàng (mã: " + res.code() + "), thử lại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                btnConfirmOrder.setEnabled(true);
                btnConfirmOrder.setText("Xác nhận đặt hàng");
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
