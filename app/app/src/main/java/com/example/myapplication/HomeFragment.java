package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.myapplication.adapter.CategoryAdapter;
import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.model.Category;
import com.example.myapplication.model.Product;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerCategories, recyclerProducts;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText etSearch;
    private List<Product> allProducts = new ArrayList<>();
    private ProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerCategories = view.findViewById(R.id.recycler_categories);
        recyclerProducts = view.findViewById(R.id.recycler_products);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        etSearch = view.findViewById(R.id.et_search);

        recyclerCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Search filter logic
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterProducts(s.toString());
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        loadData();
        swipeRefreshLayout.setOnRefreshListener(this::loadData);

        return view;
    }

    private void filterProducts(String query) {
        if (query.isEmpty()) {
            productAdapter = new ProductAdapter(getContext(), allProducts);
        } else {
            List<Product> filtered = new ArrayList<>();
            for (Product p : allProducts) {
                if (p.getName().toLowerCase().contains(query.toLowerCase())
                        || p.getCategory().toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(p);
                }
            }
            productAdapter = new ProductAdapter(getContext(), filtered);
        }
        recyclerProducts.setAdapter(productAdapter);
    }

    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);

        // Load Categories
        RetrofitClient.getService().getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recyclerCategories.setAdapter(new CategoryAdapter(response.body()));
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {}
        });

        // Load Products
        RetrofitClient.getService().getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    allProducts = response.body();
                    productAdapter = new ProductAdapter(getContext(), allProducts);
                    recyclerProducts.setAdapter(productAdapter);
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Không thể tải sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
