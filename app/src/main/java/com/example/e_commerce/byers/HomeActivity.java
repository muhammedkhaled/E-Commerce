package com.example.e_commerce.byers;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerce.MainActivity;
import com.example.e_commerce.ProductsAdapter;
import com.example.e_commerce.R;
import com.example.e_commerce.SliderAdapter;
import com.example.e_commerce.NewOrdersActivity;
import com.example.e_commerce.admin.CheckNewProductsActivity;
import com.example.e_commerce.interface_listners.ItemClickListener;
import com.example.e_commerce.model.Products;
import com.example.e_commerce.prevalent.Prevalent;
import com.example.e_commerce.AddEditProductsActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ItemClickListener {

    private DatabaseReference productRef;
    private DatabaseReference cartListRef;
    private DatabaseReference wishListRef;

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private StaggeredGridLayoutManager staggeredGrid;
    private RecyclerView recyclerView;
    private SliderView sliderView;

    private String type = "";
    private List<Products> productsList = new ArrayList<>();
    private ProductsAdapter productsAdapter = new ProductsAdapter(this);
    private List<String> sliderImagesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        wishListRef = FirebaseDatabase.getInstance().getReference().child("Wish List");

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            type = getIntent().getExtras().get("Admin").toString();
        }

        Paper.init(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);

        if (!type.equals("Admin")) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!type.equals("Admin")) {
                    Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                    startActivity(intent);
                }

            }
        });

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();

        if (type.equals("Admin")) {
            Menu menu = navigationView.getMenu();
            MenuItem nav_cart = menu.findItem(R.id.nav_cart);
            MenuItem nav_wishList = menu.findItem(R.id.nav_wish_list);
            MenuItem nav_orders = menu.findItem(R.id.nav_orders);
            MenuItem nav_sitings = menu.findItem(R.id.nav_sitings);

            nav_wishList.setVisible(false);
            nav_sitings.setVisible(false);

            nav_cart.setTitle("Approve New Products");
            nav_orders.setTitle("Approve New Orders");
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);

        if (!type.equals("Admin")) {
            userNameTextView.setText(Prevalent.CURRENT_ONLINE_USER.getName());
            Picasso.get()
                    .load(Prevalent.CURRENT_ONLINE_USER.getImage())
                    .placeholder(R.drawable.profile)
                    .into(profileImageView);
        } else if (type.equals("Admin")) {
            productsAdapter.setType("Admin");
        }

        final SliderAdapter sliderAdapter = new SliderAdapter(this);
        sliderImagesList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        staggeredGrid = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGrid);
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Products products = snapshot.getValue(Products.class);
                    if (dataSnapshot.child(products.getPid()).child("productState").getValue().equals("Approved")) {
                        productsList.add(products);
                        if (sliderImagesList.size() < 7) {
                            sliderImagesList.add(products.getImage());
                            // fill slide adapter with existing product images or static images
                            sliderAdapter.renewItems(sliderImagesList);
                        }
                        productsAdapter.setProductsList(productsList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        recyclerView.setAdapter(productsAdapter);

        sliderView = findViewById(R.id.imageSlider);
//        integers.add(R.drawable.applogo);
//        integers.add(R.drawable.login);
//        integers.add(R.drawable.register);
        sliderView.setSliderAdapter(sliderAdapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.THIN_WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(2); //set scroll delay in seconds :
        sliderView.startAutoCycle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productsAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_wish_list:
                if (!type.equals("Admin")) {
                    Intent intent = new Intent(HomeActivity.this, WishListActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.nav_cart:
                if (!type.equals("Admin")) {
                    Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(HomeActivity.this,
                            CheckNewProductsActivity.class);
                    intent.putExtra("Admin", "Admin");
                    startActivity(intent);
                }
                break;

            case R.id.nav_orders:
                Intent newOrdersIntent = new Intent(HomeActivity.this, NewOrdersActivity.class);
                if (!type.equals("Admin")) {
                    newOrdersIntent.putExtra("type", "User");
                } else {
                    newOrdersIntent.putExtra("type", "Admin");
                }
                startActivity(newOrdersIntent);

                break;
            case R.id.nav_sitings:
                if (!type.equals("Admin")) {
                    Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.nav_logout:
                Paper.book().destroy();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
        }

/*        if (id == R.id.nav_categories) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view, int position, boolean isLongClick) {
        switch (view.getId()) {
            case R.id.add_to_cart:
                Products product = productsList.get(position);

                String productID = product.getPid();
                String productName = product.getPname();
                String productPrice = product.getPrice();
                addingToCart(productID, productName, productPrice);

                break;
            default:
                if (type.equals("Admin")) {
                    Intent intent = new Intent(HomeActivity.this,
                            AddEditProductsActivity.class);
                    intent.putExtra("pid", productsList.get(position).getPid());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HomeActivity.this,
                            ProductDetailsActivity.class);
                    intent.putExtra("pid", productsList.get(position).getPid());
                    startActivity(intent);
                }
                break;
        }

    }

    @Override
    public void onFavBtnClick(View view, int position, boolean isChecked) {
        Products product = productsList.get(position);

        if (isChecked) {
            addingToWishList(product);
        } else {
            removeFromWishList(product.getPid());
        }
    }

    private void removeFromWishList(String productID) {
        wishListRef.child(Prevalent.CURRENT_ONLINE_USER.getPhone())
                .child("Products")
                .child(productID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(HomeActivity.this,
                                    "Removed from wish list...", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    private void addingToWishList(Products product) {
        String productID = product.getPid();
        String productName = product.getPname();
        String productPrice = product.getPrice();
        String productImage = product.getImage();
        String productDesc = product.getDescription();

        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");

        saveCurrentDate = currentDate.format(calForDate.getTime());
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", productName);
        cartMap.put("price", productPrice);
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("image", productImage);
        cartMap.put("description", productDesc);

        // todo this flag to set favorite btn to checked if user add to cart

        wishListRef.child(Prevalent.CURRENT_ONLINE_USER.getPhone())
                .child("Products")
                .child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(HomeActivity.this,
                                    "Added to wish list...", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });

    }

    private void addingToCart(final String productID, String productName, String productPrice) {
        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");

        saveCurrentDate = currentDate.format(calForDate.getTime());
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", productName);
        cartMap.put("price", productPrice);
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", "1");
        cartMap.put("discount", "");

        // todo this flag to set favorite btn to checked if user add to cart
//        cartMap.put("productState:", "addedToCart");

        cartListRef.child("User view")
                .child(Prevalent.CURRENT_ONLINE_USER.getPhone())
                .child("Products")
                .child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            // no need to admin view admin will access the order directly from order Ref
/*                            cartListRef.child("Admin view")
                                    .child(Prevalent.CURRENT_ONLINE_USER.getPhone())
                                    .child("Products").child(productID)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                            }
                                        }
                                    });*/

                            Toast.makeText(HomeActivity.this,
                                    "Added to Cart List...", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });

    }

}
