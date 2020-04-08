package com.example.e_commerce.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.e_commerce.R;
import com.example.e_commerce.byers.HomeActivity;
import com.example.e_commerce.byers.ProductDetailsActivity;
import com.example.e_commerce.model.Products;
import com.example.e_commerce.viewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CheckNewProductsActivity extends AppCompatActivity {
    private RecyclerView selNewProductsRecycler;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unApprovedProductsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_new_products);

        unApprovedProductsRef = FirebaseDatabase.getInstance().getReference()
                .child("Products");

        selNewProductsRecycler = findViewById(R.id.seller_new_products_list);
        selNewProductsRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        selNewProductsRecycler.setLayoutManager(layoutManager);


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(unApprovedProductsRef.orderByChild("productState")
                                .equalTo("Not Approved"), Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int i
                            , @NonNull final Products model) {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText(model.getPrice() + " $");
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.addToCart.setVisibility(View.GONE);
                        holder.favoriteButton.setVisibility(View.GONE);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final String productId = model.getPid();
                                CharSequence[] options = new CharSequence[]{
                                        "Approve",
                                        "Reject"
                                };
                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(CheckNewProductsActivity.this);
                                builder.setTitle("Do you want to Approve this Product, Are you sure");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            changeProductState(productId, "Approved");
                                        } else {
                                            changeProductState(productId, "Rejected");
                                        }
                                    }
                                });

                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };

        selNewProductsRecycler.setAdapter(adapter);
        adapter.startListening();
    }

    private void changeProductState(String productId, String state) {
        unApprovedProductsRef.child(productId).child("productState").setValue(state)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CheckNewProductsActivity.this,
                                    "that Item has been Approved, now available at store", Toast.LENGTH_SHORT).show();

                            // todo push notification to seller ID tel him product state and for more information call *****
                            // todo push notification to normal user  " there is a new product " OPTIONAL for marketing

                        } else {
                            Toast.makeText(CheckNewProductsActivity.this,
                                    "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();

                            // todo push notification to seller ID tel him product state and for more information call *****
                        }
                    }
                });
    }
}
