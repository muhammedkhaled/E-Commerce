package com.example.e_commerce.byers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.e_commerce.R;
import com.example.e_commerce.model.AdminOrders;
import com.example.e_commerce.prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, cityEditText, addressEditText;
    private Button confirmOrderBtn;

    private String totalAmount = null;
    private String type = "", orderID = "";

    private HashMap<String, Object> orderPID;

    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        confirmOrderBtn = findViewById(R.id.confirm_final_order_btn);
        nameEditText = findViewById(R.id.shipment_name);
        phoneEditText = findViewById(R.id.shipment_phone_number);
        cityEditText = findViewById(R.id.shipment_city);
        addressEditText = findViewById(R.id.shipment_address);

        orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.CURRENT_ONLINE_USER.getPhone());

        type = getIntent().getStringExtra("type");
        orderID = getIntent().getStringExtra("orderID");

        if (type != null && type.equals("editDetails")){
            confirmOrderBtn.setText("Update Order Info");
            // set data to edit text fields
            getOrderDetails();
        }
        // no need to it ??
        totalAmount = getIntent().getStringExtra("Total Price");
        orderPID = (HashMap<String, Object>) getIntent().getSerializableExtra("pidMap");

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
    }

    private void getOrderDetails() {
        orderRef.child(orderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AdminOrders adminOrders = dataSnapshot.getValue(AdminOrders.class);

                nameEditText.setText(adminOrders.getrName());
                phoneEditText.setText(adminOrders.getrPhone());
                addressEditText.setText(adminOrders.getAddress());
                cityEditText.setText(adminOrders.getCity());
                totalAmount = adminOrders.getTotalAmount();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        orderRef.child(orderID).child("Order Products").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                orderPID = new HashMap<>();
//                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
//                    String currentProQyt = snapshot.getValue().toString();
//                    String pid = snapshot.getKey();
//                    orderPID.put(pid, currentProQyt);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }

    private void check() {
        // order place details
        if (TextUtils.isEmpty(nameEditText.getText().toString())) {
            Toast.makeText(this, "Please Provide Your Full Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phoneEditText.getText().toString())) {
            Toast.makeText(this, "Please Provide Your Phone Number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(addressEditText.getText().toString())) {
            Toast.makeText(this, "Please Provide Your Address", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(cityEditText.getText().toString())) {
            Toast.makeText(this, "Please Provide Your City Name", Toast.LENGTH_SHORT).show();
        } else {
            if (type != null && type.equals("editDetails")){
                updateOrderInfo();
            }else {
                confirmOrder();
            }
        }
    }

    private void updateOrderInfo() {
        final String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm a");

        saveCurrentDate = currentDate.format(calForDate.getTime());
        saveCurrentTime = currentTime.format(calForDate.getTime());

        orderRef = orderRef.child(orderID);

        HashMap<String, Object> ordersMap = new HashMap<>();
//        ordersMap.put("totalAmount", totalAmount);
        ordersMap.put("rName", nameEditText.getText().toString());
        ordersMap.put("rPhone", phoneEditText.getText().toString());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("address", addressEditText.getText().toString());
        ordersMap.put("city", cityEditText.getText().toString());
        ordersMap.put("uPhone", Prevalent.CURRENT_ONLINE_USER.getPhone());
        ordersMap.put("uName", Prevalent.CURRENT_ONLINE_USER.getName());
//        ordersMap.put("orderID", orderID);

        // once the admin abroved the order with customer by phone need to change to shipped or confirmed
//        ordersMap.put("state", "not shipped");

        orderRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

//                    DatabaseReference orderProductRef =
//                            orderRef.child("Order Products");
//
//                    orderProductRef.updateChildren(orderPID);
                    Toast.makeText(ConfirmFinalOrderActivity.this, "Order Info updated", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }

    private void confirmOrder() {
        final String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm a");

        saveCurrentDate = currentDate.format(calForDate.getTime());
        saveCurrentTime = currentTime.format(calForDate.getTime());

        orderRef = orderRef.child(saveCurrentDate + saveCurrentTime);

        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("totalAmount", totalAmount);
        ordersMap.put("rName", nameEditText.getText().toString());
        ordersMap.put("rPhone", phoneEditText.getText().toString());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("address", addressEditText.getText().toString());
        ordersMap.put("city", cityEditText.getText().toString());
        ordersMap.put("uPhone", Prevalent.CURRENT_ONLINE_USER.getPhone());
        ordersMap.put("uName", Prevalent.CURRENT_ONLINE_USER.getName());
        ordersMap.put("orderID", saveCurrentDate + saveCurrentTime);

        // once the admin abroved the order with customer by phone need to change to shipped or confirmed
        ordersMap.put("state", "not shipped");

        orderRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    DatabaseReference orderProductRef =
                            orderRef.child("Order Products");

                    orderProductRef.updateChildren(orderPID);


                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User view")
                            .child(Prevalent.CURRENT_ONLINE_USER.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(ConfirmFinalOrderActivity.this,
                                        "Your Final Order has been placed successfully", Toast.LENGTH_SHORT).show();
                                Intent intent =
                                        new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });

    }
}
