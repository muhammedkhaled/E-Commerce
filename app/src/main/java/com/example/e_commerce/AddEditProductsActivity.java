package com.example.e_commerce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static com.example.e_commerce.prevalent.Prevalent.CATEGORY;

public class AddEditProductsActivity extends AppCompatActivity {

    //widgets
    private Button addNewProductButton, deleteBtn;
    private ImageView inputProductImage;
    private EditText inputProductName, inputProductDescription, inputProductPrice;
    private ProgressDialog loadingBar;

//    private ProgressBar progressBar;

    //Vars
    private String categoryName, description, price, pName, saveCurrentDate, saveCurrentTime;
    private static final int GALLEY_PICK = 1;
    private Uri imageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference productImageRef;
    private DatabaseReference productRef, sellersRef;
    private String selName, selAddress, selEmail, selID, selPhone, productID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_products);

        addNewProductButton = findViewById(R.id.add_new_product);
        deleteBtn = findViewById(R.id.delete_product_btn);
        inputProductImage = findViewById(R.id.select_product_image);
        inputProductName = findViewById(R.id.product_name);
        inputProductDescription = findViewById(R.id.product_description);
        inputProductPrice = findViewById(R.id.product_price);

        productImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        sellersRef = FirebaseDatabase.getInstance().getReference().child("Sellers");

        if (getIntent().hasExtra("pid")) {
            productID = getIntent().getStringExtra("pid");
            productRef = productRef.child(productID);
            deleteBtn.setVisibility(View.VISIBLE);
            displaySpecificProductInfo();
            addNewProductButton.setText("Update Product");
        } else {
            categoryName = getIntent().getExtras().get(CATEGORY).toString();
        }

        loadingBar = new ProgressDialog(this);

//        progressBar = findViewById(R.id.seller_new_products_progressbar);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteThisProduct();
            }
        });

        inputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!productID.equals("")) {
                    applyChanges();
                } else {
                    validateProductData();
                }
            }
        });
    }

    private void validateProductData() {
        description = inputProductDescription.getText().toString();
        price = inputProductPrice.getText().toString();
        pName = inputProductName.getText().toString();

        if (downloadImageUrl == null) {
            Toast.makeText(this, "Product image is mandatory...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please write product description...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Please write product Price...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pName)) {
            Toast.makeText(this, "Please write product name...", Toast.LENGTH_SHORT).show();
        } else {
            saveProductInfoTodataBase();
        }
    }

    private void storeProductInformation(final String productID) {
//        loadingBar.setTitle("Add New Product");
//        loadingBar.setMessage("Dear, please wait while we are adding the new product.");
//        loadingBar.setCanceledOnTouchOutside(false);
//        loadingBar.show();

        // todo handel when user press back btn or put app in forground prefent him from back by toast msg or cancel all work ???
//        loadUI(true);
//        imageUri.getLastPathSegment()

        final StorageReference filPath =
                productImageRef.child(productID + ".JPG");

        final UploadTask uploadTask = filPath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AddEditProductsActivity.this,
                        "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
//                loadUI(false);

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddEditProductsActivity.this,
                        "Product image uploaded successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,
                        Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task)
                            throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        downloadImageUrl = filPath.getDownloadUrl().toString();
                        return filPath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AddEditProductsActivity.this,
                                    "got product image Url successfully....",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void saveProductInfoTodataBase() {
        getSellerInfo();

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", description);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", categoryName);
        productMap.put("price", price);
        productMap.put("pname", pName);

        productMap.put("sellerName", selName);
        productMap.put("sellerAddress", selAddress);
        productMap.put("sellerPhone", selPhone);
        productMap.put("sellerEmail", selEmail);
        productMap.put("sellerID", selID);
        productMap.put("productState", "Not Approved");

        productRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(AddEditProductsActivity.this,
                                    "Product is added successfully..", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AddEditProductsActivity.this,
                                    "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
//                        loadUI(false);
                    }
                });

    }

    public void getSellerInfo(){
        selID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        sellersRef.child(selID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            selName = dataSnapshot.child("name").getValue().toString();
                            selAddress = dataSnapshot.child("address").getValue().toString();
                            selEmail = dataSnapshot.child("email").getValue().toString();
                            selPhone = dataSnapshot.child("phone").getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void openGallery() {
        // todo add option to take picture and crop it beside shoase from gallery
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLEY_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLEY_PICK && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            inputProductImage.setImageURI(imageUri);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");

            saveCurrentDate = currentDate.format(calendar.getTime());
            saveCurrentTime = currentTime.format(calendar.getTime());
            productRandomKey = saveCurrentDate + saveCurrentTime;
            if (productID != null && !productID.isEmpty()){
                storeProductInformation(productID);
            }else{
                storeProductInformation(productRandomKey);
            }

        }
    }


    private void deleteThisProduct() {
        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AddEditProductsActivity.this,
                        "Products has been deleted from data base", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void applyChanges() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentDate = currentDate.format(calendar.getTime());
        saveCurrentTime = currentTime.format(calendar.getTime());

        String pName = inputProductName.getText().toString();
        String pPrice = inputProductPrice.getText().toString();
        String pDescription = inputProductDescription.getText().toString();

        if (pName.equals("")) {
            Toast.makeText(this, "Write down Product Name", Toast.LENGTH_SHORT).show();
        } else if (pPrice.equals("")) {
            Toast.makeText(this, "Write down Product Price", Toast.LENGTH_SHORT).show();
        } else if (pDescription.equals("")) {
            Toast.makeText(this, "Write down Product Description", Toast.LENGTH_SHORT).show();
        } else {

            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productID);
            productMap.put("description", pDescription);
            productMap.put("price", pPrice);
            productMap.put("pname", pName);
            productMap.put("time", saveCurrentTime);
            productMap.put("date", saveCurrentDate);
            if (downloadImageUrl != null){
                productMap.put("image", downloadImageUrl);
            }

            productRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddEditProductsActivity.this,
                                "Changes Applied Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }
    }

    private void displaySpecificProductInfo() {
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String pName = dataSnapshot.child("pname").getValue().toString();
                    String pPrice = dataSnapshot.child("price").getValue().toString();
                    String pDescription = dataSnapshot.child("description").getValue().toString();
                    if (!dataSnapshot.child("image").getValue().equals("")){
                        String pImage = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(pImage).into(inputProductImage);
                    }else {
                        // todo show warnning to seller also
                        Toast.makeText(AddEditProductsActivity.this,
                                "Warning this product has no Image", Toast.LENGTH_SHORT).show();
                    }


                    inputProductName.setText(pName);
                    inputProductPrice.setText(pPrice);
                    inputProductDescription.setText(pDescription);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

//    public void loadUI(boolean isVisible){
//        if (isVisible){
//            progressBar.setVisibility(View.VISIBLE);
//
//            addNewProductButton.setVisibility(View.INVISIBLE);
//            inputProductImage.setVisibility(View.INVISIBLE);
//            inputProductName.setVisibility(View.INVISIBLE);
//            inputProductDescription.setVisibility(View.INVISIBLE);
//            inputProductPrice.setVisibility(View.INVISIBLE);
//
//        }else {
//            progressBar.setVisibility(View.INVISIBLE);
//
//            addNewProductButton.setVisibility(View.VISIBLE);
//            inputProductImage.setVisibility(View.VISIBLE);
//            inputProductName.setVisibility(View.VISIBLE);
//            inputProductDescription.setVisibility(View.VISIBLE);
//            inputProductPrice.setVisibility(View.VISIBLE);
//        }
//    }
}
