package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.myapplication.model.Product;
import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

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
        String details = "Danh mục: " + product.getCategory() + "\n" +
                        "Tình trạng: " + product.getStatus() + "\n" +
                        "Size: " + product.getSize() + "\n" +
                        "Màu: " + product.getColor();
        desc.setText(details);
        
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        price.setText(format.format(product.getPrice()));

        Glide.with(this).load(product.getImage()).into(img);
        
        findViewById(R.id.btn_add_to_cart).setOnClickListener(v -> {
            com.example.myapplication.utils.CartManager.getInstance().addToCart(product, 1);
            android.widget.Toast.makeText(this, "Đã thêm vào giỏ hàng", android.widget.Toast.LENGTH_SHORT).show();
            finish(); // Go back or stay? Usually stay, but for simplicity
        });

        findViewById(R.id.btn_buy_now).setOnClickListener(v -> {
             // For now just add to cart and maybe go to cart directly (impl later if needed)
            com.example.myapplication.utils.CartManager.getInstance().addToCart(product, 1);
            android.widget.Toast.makeText(this, "Mua ngay!", android.widget.Toast.LENGTH_SHORT).show();
        });
    }
}
