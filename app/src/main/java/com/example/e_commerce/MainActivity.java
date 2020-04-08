package com.example.e_commerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerce.byers.HomeActivity;
import com.example.e_commerce.byers.LoginActivity;
import com.example.e_commerce.byers.RegisterActivity;
import com.example.e_commerce.model.Users;
import com.example.e_commerce.prevalent.Prevalent;
import com.example.e_commerce.sellers.SellerHomeActivity;
import com.example.e_commerce.sellers.SellerRegistrationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

import static com.example.e_commerce.prevalent.Prevalent.PARENT_DP_NAME_USERS;

public class MainActivity extends AppCompatActivity {

    private Button joinNowButton, loganButton;
//    private ProgressDialog loadingBar = null;
    private ProgressBar progressBar;
    private TextView sellerBegin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // todo add Data binding instead;
        joinNowButton = findViewById(R.id.main_join_now_button);
        loganButton = findViewById(R.id.main_login_button);
        sellerBegin = findViewById(R.id.seller_begin);

        progressBar = findViewById(R.id.main_progressbar);

//        loadingBar = new ProgressDialog(this);

        Paper.init(this);
        loganButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        sellerBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SellerRegistrationActivity.class);
                startActivity(intent);
            }
        });

        // todo make this at Splash activity to check UserType
        String userPhoneKey = Paper.book().read(Prevalent.USER_PHONE_KEY);
        String userPasswordKey = Paper.book().read(Prevalent.USER_PASSWORD_kEY);

        if (userPhoneKey != "" && userPasswordKey != "") {
            if (!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPasswordKey)) {
                allowAccess(userPhoneKey, userPasswordKey);
                Log.d("dd", "onCreate: " + userPhoneKey + " " + userPasswordKey);
//                loadingBar.setTitle("Already Logged in");
//                loadingBar.setMessage("please wait....");
//                loadingBar.setCanceledOnTouchOutside(false);
//                loadingBar.show();
                loadUI(true);
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            Intent intent = new Intent(MainActivity.this, SellerHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            loadUI(false);
        }
    }

    private void allowAccess(final String phone, final String password) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadUI(false);

                if ((dataSnapshot.child(PARENT_DP_NAME_USERS).child(phone).exists())) {

                    Users usersData = dataSnapshot.child(PARENT_DP_NAME_USERS).child(phone)
                            .getValue(Users.class);

                    if (usersData.getPhone().equals(phone)) {

                        if (usersData.getPassword().equals(password)) {
                            Toast.makeText(MainActivity.this,
                                    "Pleas Wait, you already logged in", Toast.LENGTH_SHORT).show();
//                            loadingBar.dismiss();
//                            loadUI(false);
                            Intent intent =
                                    new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.CURRENT_ONLINE_USER = usersData;
                            startActivity(intent);
                        }
                    }
                }
                loadUI(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void loadUI(boolean isVisible){
        if (isVisible){
            progressBar.setVisibility(View.VISIBLE);

            joinNowButton.setVisibility(View.INVISIBLE);
            loganButton.setVisibility(View.INVISIBLE);
            sellerBegin.setVisibility(View.INVISIBLE);
        }else {
            progressBar.setVisibility(View.INVISIBLE);

            joinNowButton.setVisibility(View.VISIBLE);
            loganButton.setVisibility(View.VISIBLE);
            sellerBegin.setVisibility(View.VISIBLE);
        }
    }
}
