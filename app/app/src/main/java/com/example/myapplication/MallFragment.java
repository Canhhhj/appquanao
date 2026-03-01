package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.MallCategoryAdapter;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.model.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MallFragment extends Fragment {

    private RecyclerView recyclerCategories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mall, container, false);

        recyclerCategories = view.findViewById(R.id.recycler_categories_mall);
        recyclerCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));

        view.findViewById(R.id.tv_mall_see_all).setOnClickListener(v -> {
            if (getContext() == null) return;
            startActivity(new Intent(getContext(), ProductListActivity.class));
        });

        loadCategories();

        return view;
    }

    private void loadCategories() {
        RetrofitClient.getService().getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MallCategoryAdapter adapter = new MallCategoryAdapter(response.body(), category -> {
                        if (getContext() == null) return;
                        Intent intent = new Intent(getContext(), ProductListActivity.class);
                        intent.putExtra("categoryName", category.getName());
                        startActivity(intent);
                    });
                    recyclerCategories.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(getContext(), "Không tải được danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
