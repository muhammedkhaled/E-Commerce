package com.example.e_commerce.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.e_commerce.ProductsAdapter;
import com.example.e_commerce.R;
import com.example.e_commerce.byers.ProductDetailsActivity;
import com.example.e_commerce.interface_listners.ItemClickListener;
import com.example.e_commerce.model.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderProductsActivity extends AppCompatActivity implements ItemClickListener {
    private static final String TAG = "AdminUserProductsActivi";

    private DatabaseReference ordersRef;
    private DatabaseReference productsRef;

    private RecyclerView productsListRecycler;
    private RecyclerView.LayoutManager layoutManager;
    private String userID = "";
    private String orderID = "";
    private String type = "";
    private List<Products> productsList = new ArrayList<>();
    private ProductsAdapter productsAdapter = new ProductsAdapter(this);
    private HashMap<String, Object> qytMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_products);
        type = "OrderInfoAdmin";

        Toolbar toolbar = findViewById(R.id.order_product_toolbar);
        toolbar.setTitle("Order products");
        setSupportActionBar(toolbar);

        userID = getIntent().getStringExtra("uid");
        orderID = getIntent().getStringExtra("orderID");
        type = getIntent().getStringExtra("orderState");

        productsAdapter.setType(type);

        productsListRecycler = findViewById(R.id.products_list);
        productsListRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productsListRecycler.setLayoutManager(layoutManager);

//        productsAdapter.setType("OrderInfoAdmin");

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(userID).child(orderID);
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        ordersRef.child("Order Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productsList.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    String currentProQyt = snapshot.getValue().toString();
                    final String pid = snapshot.getKey();
                    qytMap.put(pid, currentProQyt);

                    Log.d(TAG, "onClick: "+ currentProQyt);
                    Log.d(TAG, "onDataChange: " + pid);

                    productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                            for (DataSnapshot snapshot1: dataSnapshot1.getChildren()){
                                Products products = snapshot1.getValue(Products.class);

                                if (products.getPid().equals(pid)){
                                    productsList.add(products);
                                    // set adapter here
                                    productsAdapter.setProductsList(productsList);
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        productsListRecycler.setAdapter(productsAdapter);
        productsAdapter.setpQytMap(qytMap);
    }


    @Override
    public void onClick(View view, final int position, boolean isLongClick) {

        Products product = productsList.get(position);
        String currentQyt = (String) qytMap.get(product.getPid());


        switch (view.getId()){
            case R.id.add_to_cart:

                int pPrice = Integer.valueOf(product.getPrice());
                int pQyt = Integer.valueOf(currentQyt);
                final int pQPrice = pPrice*pQyt;

                // todo should admin have abilty to delete order products ??

                ordersRef.child("Order Products").child(productsList.get(position).getPid())
                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            ordersRef.child("totalAmount").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int total = Integer.valueOf(dataSnapshot.getValue().toString());
                                    total = total - pQPrice;
                                    ordersRef.child("totalAmount").setValue(String.valueOf(total));
                                    if (total == 0){
                                        ordersRef.child("state").setValue("Order Canceled");
                                        Toast.makeText(OrderProductsActivity.this, "Order Removed", Toast.LENGTH_SHORT).show();
                                        OrderProductsActivity.this.finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                });

                break;

            case R.id.edit_qyt_btn:

                Intent intent = new Intent(OrderProductsActivity.this,
                        ProductDetailsActivity.class);
                intent.putExtra("pid", product.getPid());
                intent.putExtra("type", "EditOrder");
                intent.putExtra("orderID", orderID);
                intent.putExtra("uID", userID);
                intent.putExtra("pQyt", currentQyt);
                intent.putExtra("pPrice", product.getPrice());
                startActivity(intent);

                break;

        }

    }

    @Override
    public void onFavBtnClick(View view, int position, boolean isChecked) {

    }
}
