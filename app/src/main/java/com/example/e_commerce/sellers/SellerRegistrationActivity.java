package com.example.e_commerce.sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SellerRegistrationActivity extends AppCompatActivity {
    private TextView alreadySeller;
    private EditText nameInput, phoneInput, emailInput, passInput, confirmPass, addressInput;
    private Button registerBtn;
    //    private ProgressDialog loadingBar = null;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);

        mAuth = FirebaseAuth.getInstance();

//        loadingBar = new ProgressDialog(this);

        progressBar = findViewById(R.id.seller_registration_progressbar);

        alreadySeller = findViewById(R.id.seller_already);
        nameInput = findViewById(R.id.seller_name);
        phoneInput = findViewById(R.id.seller_phone);
        emailInput = findViewById(R.id.seller_email);
        passInput = findViewById(R.id.seller_password);
        confirmPass = findViewById(R.id.seller_con_password);
        addressInput = findViewById(R.id.seller_address);
        registerBtn = findViewById(R.id.seller_register);

        alreadySeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerRegistrationActivity.this, SellerLoginActivity.class);
                startActivity(intent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerSeller();
            }
        });

    }

    private void registerSeller() {
        final String name = nameInput.getText().toString().trim();
        final String phone = phoneInput.getText().toString().trim();
        final String email = emailInput.getText().toString().trim();
        final String pass = passInput.getText().toString().trim();
        final String conPass = confirmPass.getText().toString().trim();
        final String address = addressInput.getText().toString().trim();


        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "please write your name", Toast.LENGTH_SHORT).show();
            nameInput.requestFocus();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "please write your phone number", Toast.LENGTH_SHORT).show();
            phoneInput.requestFocus();
        }else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "please write your email", Toast.LENGTH_SHORT).show();
        }
        // todo encrypt pass after get and save at data base as encrypted for security
        else if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "please write your password", Toast.LENGTH_SHORT).show();
        }else if (pass.length() < 6){
            Toast.makeText(this, "password can't be less than 6 digits", Toast.LENGTH_SHORT).show();
            passInput.requestFocus();
        } else if (!conPass.equals(pass)){
            Toast.makeText(this, "password didn't match", Toast.LENGTH_SHORT).show();
            confirmPass.requestFocus();
        }else if (TextUtils.isEmpty(address)){
            Toast.makeText(this, "please write your business address", Toast.LENGTH_SHORT).show();
            confirmPass.requestFocus();
        }
        else {

//            loadingBar.setTitle("Create Seller Account");
//            loadingBar.setMessage("please wait, while we are checking the credentials");
//            loadingBar.setCanceledOnTouchOutside(false);
//            loadingBar.show();
            loadUI(true);

            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        String sid = mAuth.getCurrentUser().getUid();
                        HashMap<String, Object> sellerMap = new HashMap<>();
                        sellerMap.put("sid", sid);
                        sellerMap.put("phone", phone);
                        sellerMap.put("email", email);
                        sellerMap.put("address", address);
                        sellerMap.put("name", name);

                        rootRef.child("Sellers").child(sid).updateChildren(sellerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
//                                    loadingBar.dismiss();

                                    Toast.makeText(SellerRegistrationActivity.this,
                                            "Congratulation " + name + "you became now a seller", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(SellerRegistrationActivity.
                                            this, SellerHomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                loadUI(false);
                            }
                        });
                    }

                }
            });

        }

    }


    public void loadUI(boolean isVisible) {
        if (isVisible) {
            progressBar.setVisibility(View.VISIBLE);

            alreadySeller.setVisibility(View.INVISIBLE);
            nameInput.setVisibility(View.INVISIBLE);
            phoneInput.setVisibility(View.INVISIBLE);
            emailInput.setVisibility(View.INVISIBLE);
            passInput.setVisibility(View.INVISIBLE);
            confirmPass.setVisibility(View.INVISIBLE);
            addressInput.setVisibility(View.INVISIBLE);
            registerBtn.setVisibility(View.INVISIBLE);

        } else {
            progressBar.setVisibility(View.INVISIBLE);

            alreadySeller.setVisibility(View.VISIBLE);
            nameInput.setVisibility(View.VISIBLE);
            phoneInput.setVisibility(View.VISIBLE);
            emailInput.setVisibility(View.VISIBLE);
            passInput.setVisibility(View.VISIBLE);
            confirmPass.setVisibility(View.VISIBLE);
            addressInput.setVisibility(View.VISIBLE);
            registerBtn.setVisibility(View.VISIBLE);
        }
    }
}
