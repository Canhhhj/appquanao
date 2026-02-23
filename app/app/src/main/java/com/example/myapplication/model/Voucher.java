package com.example.myapplication.model;

import java.io.Serializable;

public class Voucher implements Serializable {
    private String id;
    private String code;
    private String discount;
    private int limit;
    private int used;
    private String status;
    private String expiry;

    public Voucher() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDiscount() { return discount; }
    public void setDiscount(String discount) { this.discount = discount; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }

    public int getUsed() { return used; }
    public void setUsed(int used) { this.used = used; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getExpiry() { return expiry; }
    public void setExpiry(String expiry) { this.expiry = expiry; }
}

