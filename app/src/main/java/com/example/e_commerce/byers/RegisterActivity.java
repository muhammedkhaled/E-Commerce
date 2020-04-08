package com.example.e_commerce.byers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.e_commerce.MainActivity;
import com.example.e_commerce.R;
import com.example.e_commerce.prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccountButton;
    private EditText inputName, inputPhoneNumber, inputPassword, confirmPassword;
//    private ProgressDialog loadingBar = null;
    private ProgressBar progressBar;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createAccountButton = findViewById(R.id.register_button);
        inputName = findViewById(R.id.user_name_input);
        inputPhoneNumber = findViewById(R.id.register_phone_number_input);
        inputPassword = findViewById(R.id.register_password_input);
        confirmPassword = findViewById(R.id.register_con_password_input);
        progressBar = findViewById(R.id.register_progressbar);

//        loadingBar = new ProgressDialog(this);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });


    }

    private void createAccount() {
        String name = inputName.getText().toString().trim();
        String phone = inputPhoneNumber.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "please write your name", Toast.LENGTH_SHORT).show();
            inputName.requestFocus();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "please write your phone number", Toast.LENGTH_SHORT).show();
            inputPhoneNumber.requestFocus();
        }
        // todo encrypt pass after get and save at data base as encrypted for security
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "please write your password", Toast.LENGTH_SHORT).show();
        }else if (password.length() < 6){
            Toast.makeText(this, "password can't be less than 6 digits", Toast.LENGTH_SHORT).show();
            inputPassword.requestFocus();
        } else if (!confirmPass.equals(password)){
            Toast.makeText(this, "password didn't match", Toast.LENGTH_SHORT).show();
            confirmPassword.requestFocus();
        } else {

//            loadingBar.setTitle("create Account");
//            loadingBar.setMessage("please wait, while we are checking the credentials");
//            loadingBar.setCanceledOnTouchOutside(false);
//            loadingBar.show();

            progressBar.setVisibility(View.VISIBLE);
            loadUi(true);

            validatePhoneNumber(name, phone, password);
        }
    }

    private void validatePhoneNumber(final String name, final String phone, final String password) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child(Prevalent.PARENT_DP_NAME_USERS).child(phone).exists())) {
                    // todo verify this phone by sending message to user
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone", phone);
                    userDataMap.put("password", password);
                    userDataMap.put("name", name);

//                    Users userData = new Users();
//                    userData.setName(name);
//                    userData.setPhone(phone);
//                    userData.setPassword(password);

                    rootRef.child(Prevalent.PARENT_DP_NAME_USERS).child(phone).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this,
                                                "Congratulations, your account has been created", Toast.LENGTH_SHORT).show();
//                                        loadingBar.dismiss();
                                        Intent intent =
                                                new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
//                                        loadingBar.dismiss();
                                        Log.d(TAG, "onComplete Error: " + task.getException().toString());
                                        Toast.makeText(RegisterActivity.this,
                                                "Network Error: please try again", Toast.LENGTH_SHORT).show();
                                    }
                                    progressBar.setVisibility(View.INVISIBLE);
                                    loadUi(false);
                                }
                            });

                } else {
                    Toast.makeText(RegisterActivity.this,
                            "This " + phone + " already exist", Toast.LENGTH_SHORT).show();
//                    loadingBar.dismiss();
                    progressBar.setVisibility(View.INVISIBLE);
                    loadUi(false);
                    Toast.makeText(RegisterActivity.this,
                            "Please try again using ant her phone number", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void loadUi(boolean isVisible){
        if (isVisible) {
            createAccountButton.setVisibility(View.INVISIBLE);
            inputPhoneNumber.setVisibility(View.INVISIBLE);
            inputName.setVisibility(View.INVISIBLE);
            inputPassword.setVisibility(View.INVISIBLE);
            confirmPassword.setVisibility(View.INVISIBLE);
        }else {
            createAccountButton.setVisibility(View.VISIBLE);
            inputPhoneNumber.setVisibility(View.VISIBLE);
            inputName.setVisibility(View.VISIBLE);
            inputPassword.setVisibility(View.VISIBLE);
            confirmPassword.setVisibility(View.VISIBLE);
        }
    }
}
