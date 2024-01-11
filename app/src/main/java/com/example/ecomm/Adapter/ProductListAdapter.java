package com.example.ecomm.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecomm.Model.ProductItem;
import com.example.ecomm.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private Context context;
    private List<ProductItem> productItemArrayList;
    public ProductListAdapter(Context context, List<ProductItem> productItemArrayList) {
        this.context = context;
        this.productItemArrayList = productItemArrayList;
    }

    @NonNull
    @Override
    public ProductListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.product_item_layout, parent, false));

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductListAdapter.ViewHolder holder, int position) {
        ProductItem productItem = productItemArrayList.get(position);
        String name= productItem.getProduct_name();
        holder.product_name.setText(name);
        holder.product_type.setText(productItem.getProduct_type());
        holder.product_price.setText("$"+productItem.getPrice());
        holder.product_tax.setText(productItem.getTax()+"%");
        if(!productItem.getImageUrl().isEmpty()) {
            Picasso.get().load(productItem.getImageUrl()).into(holder.product_img);
        }
    }
    public void filterList(ArrayList<ProductItem> filterlist) {
        productItemArrayList = filterlist;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        if(productItemArrayList.isEmpty())
            return 0;
        return productItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView product_name;
        private final TextView product_type;
        private  final  TextView product_price;
        private final TextView product_tax;
        private final ImageView product_img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product_name);
            product_img = itemView.findViewById(R.id.product_image);
            product_price = itemView.findViewById(R.id.product_price);
            product_tax = itemView.findViewById(R.id.product_tax);
            product_type = itemView.findViewById(R.id.product_type);

        }
    }
}
