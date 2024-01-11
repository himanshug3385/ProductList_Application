package com.example.ecomm;

import com.example.ecomm.Model.ProductItem;

import org.json.JSONObject;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitAPI {
    @Multipart
    @POST("api/public/add")
    Call<JSONObject> createPost(
             @Part("product_name") RequestBody product_name,
             @Part("product_type") RequestBody product_type,
             @Part("price") RequestBody price,
             @Part("tax") RequestBody tax,
             @Part MultipartBody.Part image
    );
}
