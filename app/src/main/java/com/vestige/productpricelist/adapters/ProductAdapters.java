package com.vestige.productpricelist.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vestige.productpricelist.R;
import com.vestige.productpricelist.activity.SingleProduct;
import com.vestige.productpricelist.models.Product;
import com.vestige.productpricelist.sqlite.FavProductDBController;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapters extends RecyclerView.Adapter<ProductAdapters.ViewHolder> {

    private Context context;
    private List<Product> productList;
    private String page;

    public ProductAdapters(Context context, List<Product> productList, String page) {
        this.context = context;
        this.productList = productList;
        this.page = page;
    }

    public void filterList(ArrayList<Product> filterlist) {
        // below line is to add our filtered
        // list in our course array list.
        productList = filterlist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        if (product!=null)
        {
            holder.Title.setText(product.getProductName()+" ("+product.getNetContent()+")");
            holder.Price.setText("MRP : ₹"+product.getMrp());
            holder.DP.setText("DP : ₹"+product.getDp());
            holder.PV.setText("PV: "+product.getPv());
            holder.PCode.setText("P. Code : "+product.getProductCode());
            Glide.with(context.getApplicationContext())
                    .load(product.getProductImage())
                    .into(holder.imageView);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, SingleProduct.class);
                intent.putExtra("title", product.getProductName());
                intent.putExtra("url", product.getProductImage());
                intent.putExtra("mrp", ""+product.getMrp());
                intent.putExtra("dp", ""+product.getDp());
                intent.putExtra("pv", ""+product.getPv());
                intent.putExtra("catid",product.getCategoryID());
                intent.putExtra("code", ""+product.getProductCode());
                intent.putExtra("qt", ""+product.getNetContent());
                intent.putExtra("id", ""+product.getId());
                intent.putExtra("page", ""+page);
                context.startActivity(intent);
            });


            FavProductDBController favProductDBController = new FavProductDBController(context);

            if (favProductDBController.checkFav(product.getId().toString()))
            {
                Glide.with(context.getApplicationContext())
                        .load(R.drawable.ic_favourite)
                        .into(holder.FavBtn);
            }
            else
            {
                Glide.with(context.getApplicationContext())
                        .load(R.drawable.ic_unfavourite)
                        .into(holder.FavBtn);
            }

            holder.FavBtn.setOnClickListener(v -> {
                if (favProductDBController.checkFav(product.getId().toString()))
                {
                    favProductDBController.deleteFav(product.getId().toString(),context);
                    Glide.with(context.getApplicationContext())
                            .load(R.drawable.ic_unfavourite)
                            .into(holder.FavBtn);
                    notifyItemChanged(position);
                }
                else
                {
                    favProductDBController.insertData(Integer.parseInt(product.getId()));
                    Glide.with(context.getApplicationContext())
                            .load(R.drawable.ic_favourite)
                            .into(holder.FavBtn);
                    notifyItemChanged(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView, FavBtn;
        private TextView Title, Price, DP, PV, PCode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.item_product_title);
            Price = itemView.findViewById(R.id.item_product_price);
            imageView = itemView.findViewById(R.id.item_product_img);
            DP = itemView.findViewById(R.id.item_product_dp);
            PV = itemView.findViewById(R.id.item_product_pv);
            PCode = itemView.findViewById(R.id.item_product_productCode);
            FavBtn = itemView.findViewById(R.id.item_product_fav_btn);
        }
    }
}
