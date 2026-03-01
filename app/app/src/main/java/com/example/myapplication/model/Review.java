package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Review implements Serializable {
    private String id;
    @SerializedName("productId")
    private String productId;
    @SerializedName("userId")
    private String userId;
    private String user;
    private String product;
    private int rating;
    private String comment;
    private String date;
    private String reply;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
}
