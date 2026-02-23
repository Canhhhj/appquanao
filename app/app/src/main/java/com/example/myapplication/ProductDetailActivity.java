package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.myapplication.model.Product;
import com.example.myapplication.utils.CartManager;
import com.example.myapplication.utils.UserSession;
import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết sản phẩm");
        }

        Product product = (Product) getIntent().getSerializableExtra("product");
        if (product == null) {
            finish();
            return;
        }

        ImageView img = findViewById(R.id.detail_image);
        TextView name = findViewById(R.id.detail_name);
        TextView price = findViewById(R.id.detail_price);
        TextView desc = findViewById(R.id.detail_desc);

        name.setText(product.getName());

        StringBuilder details = new StringBuilder();
        if (product.getCategory() != null) details.append("Danh mục: ").append(product.getCategory()).append("\n");
        if (product.getStatus() != null) details.append("Tình trạng: ").append(product.getStatus()).append("\n");
        if (product.getSize() != null) details.append("Size: ").append(product.getSize()).append("\n");
        if (product.getColor() != null) details.append("Màu: ").append(product.getColor());
        desc.setText(details.toString().trim());

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        price.setText(format.format(product.getPrice()));

        Glide.with(this)
                .load(product.getImage())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(img);

        Button btnAddToCart = findViewById(R.id.btn_add_to_cart);
        Button btnBuyNow = findViewById(R.id.btn_buy_now);

        btnAddToCart.setOnClickListener(v -> {
            CartManager.getInstance().addToCart(product, 1);
            Toast.makeText(this, "✅ Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            // Do NOT finish() - stay on detail page
        });

        btnBuyNow.setOnClickListener(v -> {
            if (!UserSession.getInstance(this).isLoggedIn()) {
                Toast.makeText(this, "Vui lòng đăng nhập để mua hàng", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }
            CartManager.getInstance().addToCart(product, 1);
            // Go directly to checkout
            startActivity(new Intent(this, CheckoutActivity.class));
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
