package com.example.ecomm;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

//public class ProductListingScreen extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_product_listing_screen);
//    }
//}
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecomm.Adapter.ProductListAdapter;
import com.example.ecomm.Model.BottomSheetModel;
import com.example.ecomm.Model.ConnectionReceiver;
import com.example.ecomm.Model.ProductItem;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductListingScreen extends AppCompatActivity implements ConnectionReceiver.ReceiverListener {

    int count = 0;
    String url = "https://app.getswipe.in/api/public/get";
    private ArrayList<ProductItem> productItemArrayList;
    private RecyclerView recyclerView;
    private ProductListAdapter productListAdapter;
    private ProgressBar loadingPB;
    SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView nestedSV;
    ProductDB db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_listing_screen);
        swipeRefreshLayout=findViewById(R.id.refresh);
        productItemArrayList = new ArrayList<>();
         db=ProductDB.getInstance(this);
        recyclerView = findViewById(R.id.recyclerview);
        loadingPB = findViewById(R.id.idPBLoading);
        nestedSV = findViewById(R.id.idNestedSV);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                productItemArrayList.clear();
                getData();
                productListAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        GridLayoutManager manager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(manager);
        nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    count++;
                    loadingPB.setVisibility(View.VISIBLE);
                    if (count < 20) {
                        getData();
                    }
                }
            }
        });

        if(!checkConnection()){
            List<ProductItem>dbarr= (List<ProductItem>)db.Dao().getAllProducts();
            productListAdapter = new ProductListAdapter(ProductListingScreen.this, dbarr);
            recyclerView.setAdapter(productListAdapter);
        }
        else{
            getData();
        }
    }

    private void getData() {
        db.Dao().deleteAllProducts();
        RequestQueue queue = Volley.newRequestQueue(ProductListingScreen.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                recyclerView.setVisibility(View.VISIBLE);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject responseObj = response.getJSONObject(i);
                        String product_type = responseObj.getString("product_type");
                        String product_name = responseObj.getString("product_name");
                        double price = responseObj.getDouble("price");
                        String product_image = responseObj.getString("image");
                        int tax= responseObj.getInt("tax");
                        ProductItem tp=new ProductItem(product_image,product_name,product_type,price,tax);
                        productItemArrayList.add(tp);
                        db.Dao().insert(tp);
                        productListAdapter = new ProductListAdapter(ProductListingScreen.this, productItemArrayList);
                        recyclerView.setAdapter(productListAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(ProductListingScreen.this, "Fail to get the data..", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonArrayRequest);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.search_products_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.actionSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();
        MenuItem btnx=  menu.findItem(R.id.addproduct);
        btnx.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                BottomSheetModel addPhotoBottomDialogFragment =
                        BottomSheetModel.newInstance();
                addPhotoBottomDialogFragment.show(getSupportFragmentManager(),
                        "add_photo_dialog_fragment");
                 getData();
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
        return true;
    }
    private void filter(String text) {
        ArrayList<ProductItem> filteredlist = new ArrayList<ProductItem>();
        for (ProductItem item : productItemArrayList) {
            if (item.getProduct_name().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            productListAdapter.filterList(filteredlist);
        }
    }
    private boolean checkConnection() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");
        registerReceiver(new ConnectionReceiver(), intentFilter);
        ConnectionReceiver.Listener = this;
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        return isConnected;
    }

    private void showSnackBar(boolean isConnected) {
        String message;
        if (isConnected) {
            message = "Connected to Internet";
        } else {
            message = "Not Connected to Internet";
        }
        Snackbar snackbar = Snackbar.make(findViewById(R.id.idNestedSV), message, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        snackbar.show();
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        showSnackBar(checkConnection());
    }

    @Override
    protected void onResume() {
        super.onResume();
        showSnackBar(checkConnection());


    }

    @Override
    protected void onPause() {
        super.onPause();
        showSnackBar(checkConnection());
    }
}

