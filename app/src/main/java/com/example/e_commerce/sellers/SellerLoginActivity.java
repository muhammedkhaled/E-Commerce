package com.example.e_commerce.sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class SellerLoginActivity extends AppCompatActivity {
    private EditText emailInput, passInput;
    private Button loginBtn;
//    private ProgressDialog loadingBar = null;
    private ProgressBar progressBar;
    private static final String TAG = "SellerLoginActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

        mAuth = FirebaseAuth.getInstance();

//        loadingBar = new ProgressDialog(this);

        progressBar = findViewById(R.id.seller_login_progressbar);

        emailInput = findViewById(R.id.seller_login_email);
        passInput = findViewById(R.id.seller_login_password);
        loginBtn = findViewById(R.id.seller_login_Button);

         loginBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 loginSeller();
             }
         });

    }

    private void loginSeller() {
        final String email = emailInput.getText().toString().trim();
        final String pass = passInput.getText().toString().trim();

        if (!email.equals("") && !pass.equals("") ){

            // todo confirm password field ?? add to xml and if ok create
//            loadingBar.setTitle("Login Account");
//            loadingBar.setMessage("please wait, while we are checking the credentials");
//            loadingBar.setCanceledOnTouchOutside(false);
//            loadingBar.show();

            loadUI(true);

            mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(SellerLoginActivity.this,
                                "Welcome", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SellerLoginActivity.
                                this, SellerHomeActivity.class);
                        startActivity(intent);
                        finish();

                    }else {
                        Toast.makeText(SellerLoginActivity.this,
                                "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                    loadUI(false);
                }
            });
        }
    }

    public void loadUI(boolean isVisible){
        if (isVisible){
            progressBar.setVisibility(View.VISIBLE);

            emailInput.setVisibility(View.INVISIBLE);
            passInput.setVisibility(View.INVISIBLE);
            loginBtn.setVisibility(View.INVISIBLE);

        }else {
            progressBar.setVisibility(View.INVISIBLE);

            emailInput.setVisibility(View.VISIBLE);
            passInput.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.VISIBLE);

        }
    }
}
