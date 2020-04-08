package com.example.e_commerce.sellers;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.e_commerce.MainActivity;
import com.example.e_commerce.ProductsAdapter;
import com.example.e_commerce.R;
import com.example.e_commerce.AddEditProductsActivity;
import com.example.e_commerce.interface_listners.ItemClickListener;
import com.example.e_commerce.model.Products;
import com.example.e_commerce.viewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SellerHomeActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener, ItemClickListener {

    private RecyclerView selNewProductsRecycler;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference productRef;
    private FirebaseAuth mAuth;

    private List<Products> productsList = new ArrayList<>();
    private ProductsAdapter productsAdapter = new ProductsAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_add, R.id.navigation_logout)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);

        Toolbar toolbar = findViewById(R.id.seller_home_toolbar);
        toolbar.setTitle("My Live Products");
        setSupportActionBar(toolbar);

        navView.setOnNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();

        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        final String currentSellID = mAuth.getCurrentUser().getUid();

        selNewProductsRecycler = findViewById(R.id.seller_unverified_products_list);
        selNewProductsRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        selNewProductsRecycler.setLayoutManager(layoutManager);
        productsAdapter.setType("Seller");

        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Products products = snapshot.getValue(Products.class);
                    if (dataSnapshot.child(products.getPid()).child("sellerID").getValue().equals(currentSellID)) {
                        productsList.add(products);
                        productsAdapter.setProductsList(productsList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        selNewProductsRecycler.setAdapter(productsAdapter);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:


                return true;
            case R.id.navigation_add:

                Intent intentCat = new Intent(SellerHomeActivity.this,
                        SellerProductCategoryActivity.class);
                startActivity(intentCat);

                return true;
            case R.id.navigation_logout:
                final FirebaseAuth mAuth;
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();

                Intent intent = new Intent(SellerHomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;

        }


        return false;
    }

    @Override
    public void onClick(View view, int position, boolean isLongClick) {
        Intent intent = new Intent(SellerHomeActivity.this,
                AddEditProductsActivity.class);
        intent.putExtra("pid", productsList.get(position).getPid());
        startActivity(intent);
    }

    @Override
    public void onFavBtnClick(View view, int position, boolean isChecked) {

    }
}
