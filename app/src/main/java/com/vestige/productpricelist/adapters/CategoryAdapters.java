package com.vestige.productpricelist.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vestige.productpricelist.R;
import com.vestige.productpricelist.activity.ProductActivity;
import com.vestige.productpricelist.models.Category;

import java.util.List;

public class CategoryAdapters extends RecyclerView.Adapter<CategoryAdapters.ViewHolder> {

    private Context context;
    private List<Category> categoryList;

    public CategoryAdapters(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cat,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapters.ViewHolder holder, int position) {
        Category category = categoryList.get(position);

        if (categoryList!=null)
        {
            Glide.with(context.getApplicationContext())
                    .load(category.getIcon())
                    .into(holder.imageView);

            holder.titleText.setText(""+category.getCategoryName());

            holder.CardClick.setOnClickListener(view -> {
                Intent intent = new Intent(context, ProductActivity.class);
                intent.putExtra("catname",category.getCategoryName());
                intent.putExtra("catid",category.getId());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView titleText;
        private CardView CardClick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.item_cat_img);
            titleText = itemView.findViewById(R.id.item_cat_title);
            CardClick = itemView.findViewById(R.id.item_cat_cardView);
        }
    }
}
