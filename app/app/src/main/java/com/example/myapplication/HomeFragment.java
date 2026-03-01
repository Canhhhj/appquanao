package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.example.myapplication.model.Banner;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerCategories, recyclerProducts;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText etSearch;
    private ImageView bannerImage;
    private List<Product> allProducts = new ArrayList<>();
    private List<Banner> banners = new ArrayList<>();
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private Category selectedCategory = null;
    private String currentQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerCategories = view.findViewById(R.id.recycler_categories);
        recyclerProducts = view.findViewById(R.id.recycler_products);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        etSearch = view.findViewById(R.id.et_search);
        bannerImage = view.findViewById(R.id.banner_image);
        TextView tvSeeAll = view.findViewById(R.id.tv_see_all_products);

        recyclerCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Search: lọc local + mở màn tìm kiếm theo API khi nhấn nút hoặc action Search
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentQuery = s.toString();
                    applyFilters();
                }
                @Override public void afterTextChanged(Editable s) {}
            });
            etSearch.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                    openSearchScreen(etSearch.getText().toString().trim());
                    return true;
                }
                return false;
            });
        }
        View btnSearchOpen = view.findViewById(R.id.btn_search_open);
        if (btnSearchOpen != null) {
            btnSearchOpen.setOnClickListener(v -> openSearchScreen(etSearch != null ? etSearch.getText().toString().trim() : ""));
        }

        if (tvSeeAll != null) {
            tvSeeAll.setOnClickListener(v -> {
                if (getContext() == null) return;
                android.content.Intent intent =
                        new android.content.Intent(getContext(), ProductListActivity.class);
                // Không truyền category => hiện tất cả sản phẩm
                startActivity(intent);
            });
        }

        loadData();
        swipeRefreshLayout.setOnRefreshListener(this::loadData);

        return view;
    }

    private void applyFilters() {
        if (getContext() == null) return;
        List<Product> filtered = new ArrayList<>();
        String queryLower = currentQuery.toLowerCase();

        for (Product p : allProducts) {
            boolean matchesSearch =
                    queryLower.isEmpty() ||
                    p.getName().toLowerCase().contains(queryLower) ||
                    p.getCategory().toLowerCase().contains(queryLower);

            boolean matchesCategory =
                    selectedCategory == null || matchesCategory(p, selectedCategory);

            if (matchesSearch && matchesCategory) {
                filtered.add(p);
            }
        }

        productAdapter = new ProductAdapter(getContext(), filtered);
        recyclerProducts.setAdapter(productAdapter);
    }

    private void openSearchScreen(String query) {
        if (getContext() == null) return;
        android.content.Intent intent = new android.content.Intent(getContext(), ProductListActivity.class);
        intent.putExtra(ProductListActivity.EXTRA_SEARCH_QUERY, query);
        startActivity(intent);
    }

    private boolean matchesCategory(Product product, Category category) {
        if (product == null || category == null) return false;

        String pc = product.getCategory() == null ? "" : product.getCategory().toLowerCase();
        String cc = category.getName() == null ? "" : category.getName().toLowerCase();

        if (pc.isEmpty() || cc.isEmpty()) return false;

        // Ghép linh hoạt: \"Giày\" khớp \"Giày Thể Thao\", \"Quần áo\" khớp \"Quần Áo Nam/Nữ\"
        return pc.contains(cc) || cc.contains(pc);
    }

    private void updateBannerImage() {
        if (getContext() == null || bannerImage == null) return;
        if (banners.isEmpty()) {
            bannerImage.setImageResource(android.R.drawable.ic_menu_gallery);
            return;
        }
        int index = (int) (System.currentTimeMillis() % banners.size());
        Banner b = banners.get(index);
        String url = b.getImageUrl();

        com.bumptech.glide.Glide.with(getContext())
                .load(url)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .centerCrop()
                .into(bannerImage);
    }

    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);

        // Load Categories
        RetrofitClient.getService().getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> apiCategories = response.body();
                    List<Category> categories = new ArrayList<>();

                    // Thêm danh mục "Tất Cả" ở đầu
                    Category all = new Category();
                    all.setId("ALL");
                    all.setName("Tất Cả");
                    categories.add(all);
                    categories.addAll(apiCategories);

                    // Mặc định chọn Tất Cả
                    selectedCategory = all;

                    categoryAdapter = new CategoryAdapter(categories, category -> {
                        if ("ALL".equalsIgnoreCase(category.getId())) {
                            selectedCategory = null;
                        } else {
                            selectedCategory = category;
                        }
                        applyFilters();
                    });
                    recyclerCategories.setAdapter(categoryAdapter);
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {}
        });

        // Load Products
        RetrofitClient.getService().getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allProducts = response.body();
                    applyFilters();
                }
                // Sau khi có sản phẩm, tiếp tục tải banner
                RetrofitClient.getService().getBanners().enqueue(new Callback<List<Banner>>() {
                    @Override
                    public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response2) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (response2.isSuccessful() && response2.body() != null) {
                            banners = response2.body();
                            updateBannerImage();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Banner>> call, Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Không thể tải sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
