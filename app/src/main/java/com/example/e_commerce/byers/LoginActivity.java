package com.example.e_commerce.byers;

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
import com.example.e_commerce.model.Users;
import com.example.e_commerce.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    // Widgets
    private EditText inputPhoneNumber, inputPassword;
    private Button loginButton;
    //    private ProgressDialog loadingBar = null;
    private ProgressBar progressBar;

    private CheckBox chkBoxRememberMe;
    private TextView adminLink, notAdminLink, forgetPassword;


    // Vars
    private String parentDpName = "Users";

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.login_progressbar);

        loginButton = findViewById(R.id.login_button);
        inputPassword = findViewById(R.id.login_password_input);
        inputPhoneNumber = findViewById(R.id.login_phone_number_input);
        chkBoxRememberMe = findViewById(R.id.remember_me_chk);
        adminLink = findViewById(R.id.admin_panel_link);
        notAdminLink = findViewById(R.id.not_admin_panel_link);
        forgetPassword = findViewById(R.id.forgot_password_link);

        Paper.init(this);

//        loadingBar = new ProgressDialog(this);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText("Login Admin");
                chkBoxRememberMe.setVisibility(View.INVISIBLE);
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);
                parentDpName = "Admins";
            }
        });

        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText("Login");
                chkBoxRememberMe.setVisibility(View.VISIBLE);
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.INVISIBLE);
                parentDpName = "Users";
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check", "login");
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String phone = inputPhoneNumber.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "please write your phone number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "please write your password", Toast.LENGTH_SHORT).show();
        } else {

//            loadingBar.setTitle("Login Account");
//            loadingBar.setMessage("please wait, while we are checking the credentials");
//            loadingBar.setCanceledOnTouchOutside(false);
//            loadingBar.show();
            loadUI(true);


            allowAccessToAccount(phone, password);

        }
    }

    private void allowAccessToAccount(final String phone, final String password) {

        if (chkBoxRememberMe.isChecked()) {
            Paper.book().write(Prevalent.USER_PHONE_KEY, phone);
            Paper.book().write(Prevalent.USER_PASSWORD_kEY, password);
        }

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child(parentDpName).child(phone).exists())) {
                    Users usersData = dataSnapshot.child(parentDpName).child(phone)
                            .getValue(Users.class);

                    if (usersData.getPhone().equals(phone)) {
                        if (usersData.getPassword().equals(password)) {

                            if (parentDpName.equals("Admins")) {

                                Toast.makeText(LoginActivity.this,
                                        "Admin Logged in successfully...", Toast.LENGTH_SHORT).show();
//                                loadingBar.dismiss();
                                loadUI(false);

//                                Intent intent =
//                                        new Intent(LoginActivity.this
//                                                , AdminHomeActivity.class);
                                Intent intent =
                                        new Intent(LoginActivity.this
                                                , HomeActivity.class);
                                intent.putExtra("Admin", "Admin");
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this,
                                        "Logged in successfully...", Toast.LENGTH_SHORT).show();
//                                loadingBar.dismiss();
                                loadUI(false);

                                Intent intent =
                                        new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.CURRENT_ONLINE_USER = usersData;
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
//                            loadingBar.dismiss();
                            loadUI(false);
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Account with This " + phone + " number doesn't exist", Toast.LENGTH_SHORT).show();
//                    loadingBar.dismiss();
                    loadUI(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void loadUI(boolean isVisible) {
        if (isVisible) {
            progressBar.setVisibility(View.VISIBLE);

            loginButton.setVisibility(View.INVISIBLE);
            inputPassword.setVisibility(View.INVISIBLE);
            inputPhoneNumber.setVisibility(View.INVISIBLE);
            chkBoxRememberMe.setVisibility(View.INVISIBLE);
            adminLink.setVisibility(View.INVISIBLE);
            forgetPassword.setVisibility(View.INVISIBLE);

        } else {
            progressBar.setVisibility(View.INVISIBLE);

            loginButton.setVisibility(View.VISIBLE);
            inputPassword.setVisibility(View.VISIBLE);
            inputPhoneNumber.setVisibility(View.VISIBLE);
            chkBoxRememberMe.setVisibility(View.VISIBLE);
            adminLink.setVisibility(View.VISIBLE);
            forgetPassword.setVisibility(View.VISIBLE);
        }
    }

}
