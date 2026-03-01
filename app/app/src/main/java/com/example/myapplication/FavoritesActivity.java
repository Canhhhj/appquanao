package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.model.Product;
import com.example.myapplication.utils.FavoritesManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private TextView tvEmpty;
    private final List<Product> products = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Yêu thích");
        }

        recycler = findViewById(R.id.recycler_favorites);
        tvEmpty = findViewById(R.id.tv_empty_favorites);
        recycler.setLayoutManager(new GridLayoutManager(this, 2));

        loadFavorites();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        Set<String> ids = FavoritesManager.getInstance(this).getFavoriteIds();
        if (ids.isEmpty()) {
            products.clear();
            recycler.setAdapter(new ProductAdapter(this, products));
            tvEmpty.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
            return;
        }

        RetrofitClient.getService().getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                products.clear();
                if (response.isSuccessful() && response.body() != null) {
                    for (Product p : response.body()) {
                        if (p != null && p.getId() != null && ids.contains(p.getId())) {
                            products.add(p);
                        }
                    }
                }
                recycler.setAdapter(new ProductAdapter(FavoritesActivity.this, products));
                if (products.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    recycler.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    recycler.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                tvEmpty.setVisibility(View.VISIBLE);
                recycler.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
