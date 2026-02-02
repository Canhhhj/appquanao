package com.example.myapplication.api;

import com.example.myapplication.model.Category;
import com.example.myapplication.model.Product;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("/products")
    Call<List<Product>> getProducts();

    @GET("/categories")
    Call<List<Category>> getCategories();
    
    @GET("/products/{id}")
    Call<Product> getProductById(@Path("id") String id);
}
