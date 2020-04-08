package com.example.e_commerce.sellers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.e_commerce.R;
import com.example.e_commerce.AddEditProductsActivity;

import static com.example.e_commerce.prevalent.Prevalent.CATEGORY;

public class SellerProductCategoryActivity extends AppCompatActivity {

    private ImageView tShirts, sportsTShirts, femaleDresses, sweathers;
    private ImageView glasses, hatsCaps, walletsBagsPurses, shoes;
    private ImageView headPhonesHandFree, laptops, watches, mobilePhones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saller_product_category);


    }

    public void onClick(View view) {
        Intent intent = new Intent(SellerProductCategoryActivity.this,
                AddEditProductsActivity.class);
        switch (view.getId()) {
            case R.id.t_shirts:
                intent.putExtra(CATEGORY, "tShirts");

                break;
            case R.id.sports_shirts:
                intent.putExtra(CATEGORY, "Sports tShirts");

                break;
            case R.id.female_dresses:
                intent.putExtra(CATEGORY, "Female Dresses");

                break;
            case R.id.sweathers:
                intent.putExtra(CATEGORY, "Sweathers");

                break;
            case R.id.glasses:
                intent.putExtra(CATEGORY, "Glasses");

                break;
            case R.id.hats:
                intent.putExtra(CATEGORY, "Hats Caps");

                break;
            case R.id.purses_bags_wallets:
                intent.putExtra(CATEGORY, "Wallets Bags Purses");

                break;
            case R.id.shoes:
                intent.putExtra(CATEGORY, "Shoes");

                break;
            case R.id.headphoness_handfree:
                intent.putExtra(CATEGORY, "HeadPhones HandFree");

                break;
            case R.id.laptop_pc:
                intent.putExtra(CATEGORY, "Laptops");

                break;
            case R.id.watches:
                intent.putExtra(CATEGORY, "Watches");

                break;
            case R.id.mobilphones:
                intent.putExtra(CATEGORY, "Mobile Phones");

                break;
        }
        startActivity(intent);
    }
}
