package com.example.myapplication.api;

import com.example.myapplication.model.Category;
import com.example.myapplication.model.Order;
import com.example.myapplication.model.Product;
import com.example.myapplication.model.User;
import com.example.myapplication.model.Voucher;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.PUT;

public interface ApiService {
    @GET("/products")
    Call<List<Product>> getProducts();

    @GET("/categories")
    Call<List<Category>> getCategories();

    @GET("/products/{id}")
    Call<Product> getProductById(@Path("id") String id);

    // User APIs - kết nối với admin
    @GET("/users")
    Call<List<User>> getUsers();

    @POST("/users")
    Call<User> createUser(@Body User user);

    // Order APIs - kết nối với admin
    @GET("/orders")
    Call<List<Order>> getOrders();

    @POST("/orders")
    Call<Order> createOrder(@Body Order order);

    // Voucher APIs - dùng chung với admin
    @GET("/vouchers")
    Call<List<Voucher>> getVouchers();

    @PUT("/vouchers/{id}")
    Call<Voucher> updateVoucher(@Path("id") String id, @Body Voucher voucher);
}
