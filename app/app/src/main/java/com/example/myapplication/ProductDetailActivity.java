package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myapplication.model.Product;
import com.example.myapplication.adapter.ReviewAdapter;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.model.Review;
import com.example.myapplication.utils.CartManager;
import com.example.myapplication.utils.FavoritesManager;
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

public class ProductDetailActivity extends AppCompatActivity {

    private Product product;
    private List<Review> reviews = new ArrayList<>();
    private RecyclerView recyclerReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết sản phẩm");
        }

        product = (Product) getIntent().getSerializableExtra("product");
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
            startActivity(new Intent(this, CheckoutActivity.class));
        });

        recyclerReviews = findViewById(R.id.recycler_reviews);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerReviews.setNestedScrollingEnabled(false);
        loadReviews();

        LinearLayout layoutAddReview = findViewById(R.id.layout_add_review);
        UserSession session = UserSession.getInstance(this);
        if (!session.isLoggedIn()) {
            layoutAddReview.setVisibility(View.GONE);
        } else {
            Button btnSubmit = findViewById(R.id.btn_submit_review);
            RadioGroup ratingGroup = findViewById(R.id.rating_group);
            EditText etComment = findViewById(R.id.et_review_comment);
            btnSubmit.setOnClickListener(v -> {
                int rating = 5;
                int id = ratingGroup.getCheckedRadioButtonId();
                if (id == R.id.star1) rating = 1;
                else if (id == R.id.star2) rating = 2;
                else if (id == R.id.star3) rating = 3;
                else if (id == R.id.star4) rating = 4;
                else if (id == R.id.star5) rating = 5;
                String comment = etComment.getText().toString().trim();
                if (comment.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập nhận xét", Toast.LENGTH_SHORT).show();
                    return;
                }
                submitReview(rating, comment, etComment);
            });
        }
    }

    private void loadReviews() {
        String productId = product.getId();
        if (productId == null) return;
        RetrofitClient.getService().getReviews(productId).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                reviews.clear();
                if (response.isSuccessful() && response.body() != null) {
                    reviews.addAll(response.body());
                }
                recyclerReviews.setAdapter(new ReviewAdapter(reviews));
            }
            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                recyclerReviews.setAdapter(new ReviewAdapter(reviews));
            }
        });
    }

    private void submitReview(int rating, String comment, EditText etComment) {
        Review r = new Review();
        r.setProductId(product.getId());
        r.setUserId(UserSession.getInstance(this).getUserId());
        r.setUser(UserSession.getInstance(this).getUserName());
        r.setProduct(product.getName());
        r.setRating(rating);
        r.setComment(comment);
        r.setDate(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()));
        RetrofitClient.getService().createReview(r).enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductDetailActivity.this, "Đã gửi đánh giá!", Toast.LENGTH_SHORT).show();
                    etComment.setText("");
                    loadReviews();
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Gửi thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFavoriteIcon(ImageView btnFavorite) {
        if (product == null || product.getId() == null) return;
        boolean fav = FavoritesManager.getInstance(this).isFavorite(product.getId());
        btnFavorite.setImageResource(fav ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
