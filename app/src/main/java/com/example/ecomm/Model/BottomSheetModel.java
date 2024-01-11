package com.example.ecomm.Model;



import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ecomm.GetFilePath;
import com.example.ecomm.R;
import com.example.ecomm.RetrofitAPI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BottomSheetModel extends BottomSheetDialogFragment {
    String[] courses = { "Service","Product","model","vs","unit","veggies","os" };
   String selected="Service";
   CircleImageView img;
    int SELECT_PICTURE = 200;
    Uri myuri;
    public static BottomSheetModel newInstance() {
        return new BottomSheetModel();
    }
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_product_item_layout, container, false);
         Spinner type= view.findViewById(R.id.product_type);
         img=view.findViewById(R.id.product_image2);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selected= (String) parentView.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        ArrayAdapter ad = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, courses);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(ad);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText name= view.findViewById(R.id.product_name);
        EditText price= view.findViewById(R.id.product_price);
        EditText tax= view.findViewById(R.id.product_tax);
        Spinner type = view.findViewById(R.id.product_type);
        ProgressBar progressBar=view.findViewById(R.id.progress);
        Button add_product_btn= view.findViewById(R.id.add_product);
        img=view.findViewById(R.id.product_image2);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
            }
        });
//        String curtype= type.getSelectedView().getTransitionName();
        add_product_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                postData(name.getText().toString(),selected,Double.parseDouble(price.getText().toString()),Integer.parseInt(tax.getText().toString()));
//               Toast.makeText(getContext(),name.getText().toString()+selected,Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void postData(String productName, String productType, double productPrice, int productTax) {
//        Log.d("ramji", "postData: "+file);
//        Toast.makeText(getContext(),myuri,Toast.LENGTH_LONG).show();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://app.getswipe.in/").addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        String path= GetFilePath.getRealPath(getContext(),myuri);
        File file= new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse(getContext().getContentResolver().getType(myuri)), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("files[]", file.getName(), requestFile);
        RequestBody pr_name = RequestBody.create(MediaType.parse("multipart/form-data"),productName);
        RequestBody pr_type = RequestBody.create(MediaType.parse("multipart/form-data"),productType);
        RequestBody pr_tax = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(productTax));
        RequestBody pr_price = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(productPrice));
        Call<JSONObject> call = retrofitAPI.createPost(pr_name,pr_type,pr_price,pr_tax,body);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(@NonNull Call<JSONObject> call, Response<JSONObject> response) {
                Toast.makeText(getContext(), "Data added to API", Toast.LENGTH_SHORT).show();
                dismiss();
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    img.setImageURI(selectedImageUri);
                    myuri=selectedImageUri;
                }
            }
        }
    }



}