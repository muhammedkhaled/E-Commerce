package com.example.e_commerce;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerce.interface_listners.ItemClickListener;
import com.example.e_commerce.model.Products;
import com.example.e_commerce.viewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductViewHolder> implements Filterable {

    private List<Products> productsList;
    private List<Products> productsListFull;
    private ItemClickListener mItemClickListener;
    private int mItemPosition;
    private String type = "";
    private HashMap<String, Object> pQytMap = null;

    public void setpQytMap(HashMap<String, Object> pQytMap) {
        this.pQytMap = pQytMap;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setProductsList(List<Products> productsList) {
        this.productsList = productsList;
        productsListFull = new ArrayList<>(productsList);
        notifyDataSetChanged();
    }

    public ProductsAdapter(ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
        ProductViewHolder productViewHolder = new ProductViewHolder(view);
        productViewHolder.setItemClickListener(mItemClickListener);
        return productViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Products model = productsList.get(position);
        holder.txtProductName.setText(model.getPname());
        holder.txtProductDescription.setText(model.getDescription());
        holder.txtProductPrice.setText(model.getPrice() + " $");
        if (!model.getImage().isEmpty()) {
            Picasso.get().load(model.getImage()).into(holder.imageView);
        }

        if (type.equals("Admin")){
            holder.favoriteButton.setVisibility(View.GONE);
            holder.addToCart.setVisibility(View.GONE);
        }
        else if (type.equals("Seller")){
            holder.txtProductState.setVisibility(View.VISIBLE);
            holder.addToCart.setVisibility(View.GONE);
            holder.favoriteButton.setVisibility(View.GONE);
            holder.txtProductState.setText(model.getProductState());
        }
        else if (type.equals("wishList")){
//            holder.favoriteButton.setVisibility(View.GONE);
            holder.favoriteButton.setChecked(true);
        }
        else if (type.equals("OrderInfoAdmin")){
            holder.favoriteButton.setVisibility(View.GONE);
            holder.addToCart.setText("Remove From Cart");
            // set adtocart background color to Red

        }
        else if (type.equals("EditOrder")){
            holder.favoriteButton.setVisibility(View.GONE);
            holder.editQyt.setVisibility(View.VISIBLE);
            holder.productQyt.setVisibility(View.VISIBLE);
            String qyt = (String) pQytMap.get(model.getPid());
            holder.productQyt.setText(qyt);
            holder.addToCart.setText("Remove From Cart");

        } else if (type.equals("Can'tEditOrder")) {
            holder.favoriteButton.setVisibility(View.GONE);
            holder.editQyt.setVisibility(View.GONE);
            holder.productQyt.setVisibility(View.VISIBLE);
            holder.addToCart.setVisibility(View.GONE);
            String qyt = (String) pQytMap.get(model.getPid());
            holder.productQyt.setText(qyt);
        } else {
            holder.favoriteButton.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        if (productsList == null) {
            return 0;
        } else {
            return productsList.size();
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter =  new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Products> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(productsListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Products item : productsListFull) {
                    if (item.getPname().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            productsList.clear();
            productsList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


}
