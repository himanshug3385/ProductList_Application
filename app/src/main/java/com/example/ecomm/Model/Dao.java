package com.example.ecomm.Model;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@androidx.room.Dao
public interface Dao {
    @Insert
    void insert(ProductItem model);
    @Update
    void update(ProductItem model);
    @Delete
    void delete(ProductItem model);
    @Query("DELETE FROM product_table")
    void deleteAllProducts();
    @Query("SELECT * FROM product_table ORDER BY product_name ASC")
    List<ProductItem> getAllProducts();
}
