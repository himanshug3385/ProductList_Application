package com.example.ecomm.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "product_table")
public class ProductItem {

    @PrimaryKey (autoGenerate = true)
    private  int id;
    private String imageUrl;
    private String product_name;
    private String product_type;
    private double price;
    private int tax;


    public ProductItem(String imageUrl, String product_name, String product_type, double price, int tax) {
        this.imageUrl = imageUrl;
        this.product_name = product_name;
        this.product_type = product_type;
        this.price = price;
        this.tax = tax;
    }

    public ProductItem() {
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }
}
