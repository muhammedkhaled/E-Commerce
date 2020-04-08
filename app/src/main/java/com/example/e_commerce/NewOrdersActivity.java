package com.example.e_commerce;

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

import com.example.e_commerce.admin.OrderProductsActivity;
import com.example.e_commerce.byers.ConfirmFinalOrderActivity;
import com.example.e_commerce.model.AdminOrders;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewOrdersActivity extends AppCompatActivity implements AdminOrdersAdapter.OnOrderClick {

    private RecyclerView ordersListRecycler;
    private DatabaseReference ordersRef;
    private List<AdminOrders> adminOrdersList = new ArrayList<>();
    private AdminOrdersAdapter adminOrdersAdapter = new AdminOrdersAdapter(this);

    private String type = "" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        Toolbar toolbar = findViewById(R.id.admin_order_toolbar);
        toolbar.setTitle("New Orders");
        setSupportActionBar(toolbar);

        type = getIntent().getStringExtra("type");
        adminOrdersAdapter.setType(type);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        ordersListRecycler = findViewById(R.id.orders_List_recycler);

        ordersListRecycler.setLayoutManager(new LinearLayoutManager(this));

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adminOrdersList.clear();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot idSnapShot : dataSnapshot.getChildren()){
                        String userId = idSnapShot.getKey();
                        assert userId != null;
                        ordersRef.child(userId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    AdminOrders adminOrders = snapshot.getValue(AdminOrders.class);
                                    if (type.equals("Admin")){
                                        if (adminOrders.getState().equals("not shipped")){
                                            adminOrdersList.add(adminOrders);
                                            adminOrdersAdapter.setAdminOrdersList(adminOrdersList);
                                        }
                                    }else {
                                        adminOrdersList.add(adminOrders);
                                        adminOrdersAdapter.setAdminOrdersList(adminOrdersList);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ordersListRecycler.setAdapter(adminOrdersAdapter);
    }

    @Override
    public void onBtnClick(View view, int position) {
        AdminOrders adminOrder = adminOrdersList.get(position);
//        AdminOrders adminOrder = adminOrdersAdapter.getOrderObject();
        String uId = adminOrder.getuPhone();
        String orderID = adminOrder.getOrderID();
        DatabaseReference orderIDRef = ordersRef.child(uId).child(orderID);
        switch (view.getId()){
            case R.id.show_all_products_btn:
//                adminOrdersAdapter.getOrderObject().getuPhone();
                Intent intent = new Intent(this, OrderProductsActivity.class);
                intent.putExtra("uid", uId);
                intent.putExtra("orderID", orderID);
                if (adminOrder.getState().equals("not shipped")){
                    intent.putExtra("orderState", "EditOrder");
                }else {
                    intent.putExtra("orderState", "Can'tEditOrder");
                }
                startActivity(intent);
                break;
            case R.id.ship_order_btn:
                if (type.equals("Admin")) {
                    orderIDRef.child("state").setValue("Ready to ship");
                    adminOrdersList.remove(position);
                    adminOrdersAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Order Ready to Ship", Toast.LENGTH_SHORT).show();
                }else {
                    // todo edit Order Details

                    Intent editOrderDetails = new Intent(NewOrdersActivity.this, ConfirmFinalOrderActivity.class);
                    editOrderDetails.putExtra("type", "editDetails");
                    editOrderDetails.putExtra("orderID", adminOrder.getOrderID());
                    startActivity(editOrderDetails);
                }
                break;
            case R.id.reject_order_btn:
                if (type.equals("Admin")) {
                    orderIDRef.child("state").setValue("Rejected");
                    adminOrdersList.remove(position);
                    adminOrdersAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Order Rejected", Toast.LENGTH_SHORT).show();
                }else {
                    // remove order
                    ordersRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(NewOrdersActivity.this,
                                        "Order removed successfully", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(NewOrdersActivity.this,
                                        "Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    adminOrdersList.remove(position);
                    adminOrdersAdapter.notifyDataSetChanged();
                }
                break;
        }
    }
}
