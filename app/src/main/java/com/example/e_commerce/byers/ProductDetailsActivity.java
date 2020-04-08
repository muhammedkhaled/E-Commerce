package com.example.e_commerce.byers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.e_commerce.R;
import com.example.e_commerce.admin.OrderProductsActivity;
import com.example.e_commerce.model.Products;
import com.example.e_commerce.prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    // todo make normal btn with text instead of FAB
    private Button addToCart;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productPrice, productDescription, productName;

    private String productID = "", type ;
    private String orderID = "", currentProQyt = "", currentProPrice = "", userID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        addToCart = findViewById(R.id.add_product_to_cart);
        numberButton = findViewById(R.id.number_btn);
        productImage = findViewById(R.id.product_image_details);
        productName = findViewById(R.id.product_name_details);
        productDescription = findViewById(R.id.product_description_details);
        productPrice = findViewById(R.id.product_price_details);

        productID = getIntent().getStringExtra("pid");
        type = getIntent().getStringExtra("type");

        if (type != null && type.equals("EditOrder")){
            orderID = getIntent().getStringExtra("orderID");
            currentProQyt = getIntent().getStringExtra("pQyt");
            currentProPrice = getIntent().getStringExtra("pPrice");
            userID = getIntent().getStringExtra("uID");
            numberButton.setNumber(currentProQyt);
            // todo edite Button From Add To Cart To Update
            addToCart.setText("Update Order");
        }

        getProductDetails(productID);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type != null && type.equals("EditOrder")){
                    if (numberButton.getNumber() != currentProQyt){
                        editOrderQytAndTotal();
                    }else {
                        Toast.makeText(ProductDetailsActivity.this,
                                "Product quantity did'nt changed", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    addingToCart();
                }
            }
        });
    }

    private void editOrderQytAndTotal() {

        final int pPrice = Integer.valueOf(currentProPrice);
        int pQyt = Integer.valueOf(currentProQyt);
        final int pQPrice = pPrice * pQyt;

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders").child(userID).child(orderID);
        final String newQyt = numberButton.getNumber();

        ordersRef.child("Order Products").child(productID).setValue(newQyt)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    ordersRef.child("totalAmount").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int total = Integer.valueOf(dataSnapshot.getValue().toString());
                            total = total - pQPrice;
                            int newQytInteger = Integer.valueOf(newQyt);
                            int finalPrice = newQytInteger * pPrice;
                            total = total + finalPrice;
                            ordersRef.child("totalAmount").setValue(String.valueOf(total));
                            Toast.makeText(ProductDetailsActivity.this,
                                    "Quantity Of this products Updated", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else {
                    Toast.makeText(ProductDetailsActivity.this,
                            "Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addingToCart() {
        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");

        saveCurrentDate = currentDate.format(calForDate.getTime());
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", productName.getText().toString());
        cartMap.put("price", productPrice.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", numberButton.getNumber());
        cartMap.put("discount", "");

        cartListRef.child("User view")
                .child(Prevalent.CURRENT_ONLINE_USER.getPhone())
                .child("Products")
                .child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(ProductDetailsActivity.this,
                                    "Added to Cart List...", Toast.LENGTH_SHORT)
                                    .show();
                            finish();
                        }
                    }
                });

    }

    private void getProductDetails(String productID) {
        DatabaseReference productRef =
                FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Products product = dataSnapshot.getValue(Products.class);

                    productName.setText(product.getPname());
                    productPrice.setText(product.getPrice());
                    productDescription.setText(product.getDescription());
                    Picasso.get().load(product.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}
