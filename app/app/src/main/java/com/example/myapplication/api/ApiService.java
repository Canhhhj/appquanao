package com.example.myapplication.api;

import com.example.myapplication.model.Address;
import com.example.myapplication.model.Category;
import com.example.myapplication.model.Notification;
import com.example.myapplication.model.Order;
import com.example.myapplication.model.Product;
import com.example.myapplication.model.Review;
import com.example.myapplication.model.User;
import com.example.myapplication.model.Voucher;
import com.example.myapplication.model.Banner;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/products")
    Call<List<Product>> getProducts();

    @GET("/products")
    Call<List<Product>> getProductsBySearch(@Query("name_like") String query);

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

    @GET("/orders/{id}")
    Call<Order> getOrderById(@Path("id") String id);

    @POST("/orders")
    Call<Order> createOrder(@Body Order order);

    // Voucher APIs - dùng chung với admin
    @GET("/vouchers")
    Call<List<Voucher>> getVouchers();

    @PUT("/vouchers/{id}")
    Call<Voucher> updateVoucher(@Path("id") String id, @Body Voucher voucher);

    // Banner APIs - dùng chung với admin
    @GET("/banners")
    Call<List<Banner>> getBanners();

    // Reviews - xem theo sản phẩm, thêm đánh giá
    @GET("/reviews")
    Call<List<Review>> getReviews(@Query("productId") String productId);

    @POST("/reviews")
    Call<Review> createReview(@Body Review review);

    // Sổ địa chỉ
    @GET("/addresses")
    Call<List<Address>> getAddressesByUser(@Query("userId") String userId);

    @POST("/addresses")
    Call<Address> createAddress(@Body Address address);

    @PUT("/addresses/{id}")
    Call<Address> updateAddress(@Path("id") String id, @Body Address address);

    @DELETE("/addresses/{id}")
    Call<Void> deleteAddress(@Path("id") String id);

    // Thông báo
    @GET("/notifications")
    Call<List<Notification>> getNotificationsByUser(@Query("userId") String userId);

    @PUT("/notifications/{id}")
    Call<Notification> updateNotification(@Path("id") String id, @Body Notification notification);
}
