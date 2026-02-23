package com.example.myapplication.model;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String id;
    private String customer;
    private String date;
    private double total;
    private String status;
    private String payment;
    private List<OrderItem> items;

    public Order() {}

    public static class OrderItem implements Serializable {
        private String name;
        private double price;
        private int quantity;
        private String image;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomer() { return customer; }
    public void setCustomer(String customer) { this.customer = customer; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPayment() { return payment; }
    public void setPayment(String payment) { this.payment = payment; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
