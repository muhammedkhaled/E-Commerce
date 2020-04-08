package com.example.e_commerce.interface_listners;

import android.view.View;

import com.example.e_commerce.model.Products;

public interface ItemClickListener {

    void onClick(View view, int position, boolean isLongClick);

    void onFavBtnClick(View view, int position, boolean isChecked);
}
