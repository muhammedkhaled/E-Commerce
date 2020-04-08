package com.example.e_commerce.byers;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerce.R;
import com.example.e_commerce.model.Cart;
import com.example.e_commerce.prevalent.Prevalent;
import com.example.e_commerce.viewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessBtn;
    private TextView txtTotalAmount, txtMsg1;

    private int overTotalPrice = 0;
    private HashMap<String, Object> orderPIDMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_List_recycler);
        nextProcessBtn = findViewById(R.id.next_process_btn);
        txtTotalAmount = findViewById(R.id.total_price);
        txtMsg1 = findViewById(R.id.msg1);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Todo show total price this way is Wrong
                txtTotalAmount.setText("Total Price = "+ String.valueOf(overTotalPrice) + " $");

                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                // this list to save products id under order to make admin see order products
                intent.putExtra("pidMap", orderPIDMap);
                startActivity(intent);

                // todo i should not finish because whate if user neeed to change cart items
                finish();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
//        checkOrderState();
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List");
        // Todo replace all FirebaseRecycler with normal Recycler View
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef
                                .child("User view")
                                .child(Prevalent.CURRENT_ONLINE_USER.getPhone())
                                .child("Products"), Cart.class).build();


        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart,
                CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, final int i, @NonNull final Cart cart) {

                cartViewHolder.txtProductQuantity.setText("Quantity = " + cart.getQuantity());
                cartViewHolder.txtProductPrice.setText(cart.getPrice() + " $");
                cartViewHolder.txtProductName.setText(cart.getPname());

                orderPIDMap.put(cart.getPid(), cart.getQuantity());

                int oneTypeProductPrice = Integer.valueOf(cart.getPrice()) * Integer.valueOf(cart.getQuantity());

                overTotalPrice = overTotalPrice + oneTypeProductPrice;

                cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{
                                "Edit",
                                "Remove"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    Intent intent = new Intent(CartActivity.this,
                                            ProductDetailsActivity.class);
                                    intent.putExtra("pid", cart.getPid());
                                    startActivity(intent);
                                }
                                if (which == 1) {

                                    cartListRef.child("User view")
                                            .child(Prevalent.CURRENT_ONLINE_USER.getPhone())
                                            .child("Products")
                                            .child(cart.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

/*                                                        cartListRef.child("Admin view")
                                                                .child(Prevalent.CURRENT_ONLINE_USER.getPhone())
                                                                .child("Products").child(cart.getPid())
                                                                .removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(CartActivity.this,
                                                                                    "Removed from Cart List...", Toast.LENGTH_SHORT)
                                                                                    .show();
                                                                        }
                                                                    }
                                                                });*/

                                                        Toast.makeText(CartActivity.this,
                                                                "Removed from Cart List...", Toast.LENGTH_SHORT)
                                                                .show();

//                                                        Intent intent =
//                                                        new Intent(CartActivity.this, HomeActivity.class);
//                                                        startActivity(intent);

                                                    }
                                                }
                                            });
                                }
                            }
                        });

                        builder.show();
                    }
                });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();



    }

/*
    private void checkOrderState(){
        // todo whate if user need to kae another order i should handle this
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.CURRENT_ONLINE_USER.getPhone());

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();

                    if (shippingState.equals("shipped")){
                        txtTotalAmount.setText("Dear " + userName + "\n order is shipped successfully");
                        recyclerView.setVisibility(View.GONE);
                        txtMsg1.setText("Congratulations your final order has been shipped successfully, soon you will received yor order at your door step");
                        txtMsg1.setVisibility(View.VISIBLE);
                        nextProcessBtn.setVisibility(View.GONE);

                    }else if (shippingState.equals("not shipped")){
                        txtTotalAmount.setText("Shipped state :  not shipped yet");
                        recyclerView.setVisibility(View.GONE);
                        txtMsg1.setVisibility(View.VISIBLE);
                        nextProcessBtn.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
*/
}
