package com.example.e_commerce.byers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.e_commerce.AddEditProductsActivity;
import com.example.e_commerce.ProductsAdapter;
import com.example.e_commerce.R;
import com.example.e_commerce.interface_listners.ItemClickListener;
import com.example.e_commerce.model.Products;
import com.example.e_commerce.prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class WishListActivity extends AppCompatActivity implements ItemClickListener {
    private DatabaseReference wishListRef;
    private DatabaseReference cartListRef;

    private RecyclerView recyclerView;
    private List<Products> productsList = new ArrayList<>();
    private ProductsAdapter productsAdapter = new ProductsAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        recyclerView = findViewById(R.id.recycler_wish_list);

        wishListRef = FirebaseDatabase.getInstance().getReference()
                .child("Wish List")
                .child(Prevalent.CURRENT_ONLINE_USER.getPhone())
                .child("Products");

        cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        Toolbar toolbar = findViewById(R.id.wish_list_toolbar);
        toolbar.setTitle("Wish List");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        productsAdapter.setType("wishList");
        recyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        wishListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Products product = snapshot.getValue(Products.class);

                    Log.d("wish", "onDataChange: " + product.getPid());

                    productsList.add(product);
                    productsAdapter.setProductsList(productsList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        recyclerView.setAdapter(productsAdapter);
    }


    @Override
    public void onClick(View view, int position, boolean isLongClick) {
        switch (view.getId()) {
            case R.id.add_to_cart:
                Products product = productsList.get(position);

                String productID = product.getPid();
                String productName = product.getPname();
                String productPrice = product.getPrice();
                addingToCart(productID, productName, productPrice);

                break;
            default:
                Intent intent = new Intent(WishListActivity.this,
                        ProductDetailsActivity.class);
                intent.putExtra("pid", productsList.get(position).getPid());
                startActivity(intent);
                break;
        }

    }


    private void addingToCart(final String productID, String productName, String productPrice) {
        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");

        saveCurrentDate = currentDate.format(calForDate.getTime());
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", productName);
        cartMap.put("price", productPrice);
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", "1");
        cartMap.put("discount", "");

        // todo this flag to set favorite btn to checked if user add to cart
//        cartMap.put("productState:", "addedToCart");

        cartListRef.child("User view")
                .child(Prevalent.CURRENT_ONLINE_USER.getPhone())
                .child("Products")
                .child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // no need to admin view admin will access the order directly from order Ref
/*                            cartListRef.child("Admin view")
                                    .child(Prevalent.CURRENT_ONLINE_USER.getPhone())
                                    .child("Products").child(productID)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                            }
                                        }
                                    });*/

                            Toast.makeText(WishListActivity.this,
                                    "Added to Cart List...", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });

    }


    @Override
    public void onFavBtnClick(View view, int position, boolean isChecked) {
        if (!isChecked) {
            removeFromWishList(productsList.get(position).getPid());
            productsList.remove(position);
            productsAdapter.notifyItemRemoved(position);
            if (productsList.isEmpty()) {
                finish();
            }
        }
    }

    private void removeFromWishList(String productID) {
        wishListRef
                .child(productID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(WishListActivity.this,
                                    "Removed from wish list...", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

}
