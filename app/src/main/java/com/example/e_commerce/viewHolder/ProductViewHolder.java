package com.example.e_commerce.viewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerce.R;
import com.example.e_commerce.interface_listners.ItemClickListener;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductDescription, txtProductPrice, txtProductState, productQyt;
    public Button addToCart;
    public ImageView imageView;
    public ItemClickListener mItemClickListener;
    public ImageButton editQyt;
//    public MaterialFavoriteButton favoriteButton;
    public CheckBox favoriteButton;
    private int mItemPosition;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.product_image);
        txtProductName = itemView.findViewById(R.id.product_name);
        txtProductDescription = itemView.findViewById(R.id.product_description);
        txtProductPrice = itemView.findViewById(R.id.product_price);
        txtProductState = itemView.findViewById(R.id.product_state);
        favoriteButton = itemView.findViewById(R.id.favorite_btn);
        addToCart = itemView.findViewById(R.id.add_to_cart);
        editQyt = itemView.findViewById(R.id.edit_qyt_btn);
        productQyt = itemView.findViewById(R.id.qyt_text);

        itemView.setOnClickListener(this);
        addToCart.setOnClickListener(this);
        editQyt.setOnClickListener(this);

        favoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = getAdapterPosition();
                if (mItemClickListener != null && position != RecyclerView.NO_POSITION) {
                    mItemClickListener.onFavBtnClick(buttonView, getAdapterPosition(), isChecked);
                }
            }
        });


    }

    public void setItemClickListener(ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public void onClick(View v) {
        int position = getAdapterPosition();
        if (mItemClickListener != null && position != RecyclerView.NO_POSITION) {
            mItemClickListener.onClick(v, position, false);
        }
    }
}
