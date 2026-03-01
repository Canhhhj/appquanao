package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.model.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity {

    public static final String EXTRA_SEARCH_QUERY = "search_query";

    private RecyclerView recyclerView;
    private TextView tvTitle, tvEmpty;
    private ProgressBar progressBar;
    private final List<Product> products = new ArrayList<>();
    private ProductAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        Toolbar toolbar = findViewById(R.id.toolbar_product_list);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        recyclerView = findViewById(R.id.recycler_products_list);
        tvTitle = findViewById(R.id.tv_product_list_title);
        tvEmpty = findViewById(R.id.tv_empty_products);
        progressBar = findViewById(R.id.progress_products);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductAdapter(this, products);
        recyclerView.setAdapter(adapter);

        String searchQuery = getIntent() != null ? getIntent().getStringExtra(EXTRA_SEARCH_QUERY) : null;
        String categoryName = getIntent() != null ? getIntent().getStringExtra("categoryName") : null;

        if (searchQuery != null && !searchQuery.isEmpty()) {
            tvTitle.setText("Kết quả: \"" + searchQuery + "\"");
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Tìm kiếm");
            loadProductsBySearch(searchQuery);
        } else {
            if (categoryName == null || categoryName.trim().isEmpty()) {
                tvTitle.setText("Tất cả sản phẩm");
                if (getSupportActionBar() != null) getSupportActionBar().setTitle("Sản phẩm");
            } else {
                tvTitle.setText("Danh mục: " + categoryName);
                if (getSupportActionBar() != null) getSupportActionBar().setTitle(categoryName);
            }
            loadProducts(categoryName);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadProducts(@Nullable String categoryName) {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        RetrofitClient.getService().getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                progressBar.setVisibility(View.GONE);
                products.clear();

                if (response.isSuccessful() && response.body() != null) {
                    String catLower = categoryName == null ? "" : categoryName.toLowerCase();
                    for (Product p : response.body()) {
                        if (p == null) continue;
                        if (catLower.isEmpty()) {
                            products.add(p);
                        } else {
                            String pc = p.getCategory() == null ? "" : p.getCategory().toLowerCase();
                            if (pc.contains(catLower) || catLower.contains(pc)) {
                                products.add(p);
                            }
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                updateEmptyState();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                updateEmptyState();
            }
        });
    }

    private void loadProductsBySearch(String query) {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        RetrofitClient.getService().getProductsBySearch(query).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                progressBar.setVisibility(View.GONE);
                products.clear();
                if (response.isSuccessful() && response.body() != null) {
                    products.addAll(response.body());
                }
                adapter.notifyDataSetChanged();
                updateEmptyState();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                updateEmptyState();
            }
        });
    }

    private void updateEmptyState() {
        if (products.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}

