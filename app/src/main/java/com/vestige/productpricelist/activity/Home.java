package com.vestige.productpricelist.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.vestige.productpricelist.R;
import com.vestige.productpricelist.adapters.CategoryAdapters;
import com.vestige.productpricelist.api.ApiInterface;
import com.vestige.productpricelist.api.Constants;
import com.vestige.productpricelist.models.Category;
import com.vestige.productpricelist.models.Sliders;
import com.vestige.productpricelist.utils.ConnectionReceiver;
import com.vestige.productpricelist.utils.PermissionUtil;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity implements PermissionUtil.PermissionsCallBack, ConnectionReceiver.ReceiverListener {

    public static LinearLayout HomeMainLayout;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private List<Category> categoryList;
    private CategoryAdapters categoryAdapters;
    private ImageSlider imageSlider;
    private List<SlideModel> imageSliderList;

    public static DrawerLayout MainLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    private ImageView DrawerClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imageSliderList = new ArrayList<SlideModel>();
        categoryList = new ArrayList<>();

        imageSlider = findViewById(R.id.home_image_slider);
        HomeMainLayout = findViewById(R.id.home_main_layout);
        MainLayout = findViewById(R.id.home_drawer_layout);
        recyclerView = findViewById(R.id.home_recyclerview);
        navigationView = findViewById(R.id.home_nav_View);
        DrawerClick = findViewById(R.id.home_drawer_click);

        toggle = new ActionBarDrawerToggle(Home.this, MainLayout, R.string.open, R.string.close);
        MainLayout.addDrawerListener(toggle);
        toggle.syncState();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Drawar click event
        // Drawer item Click event ------
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();
                if (itemId == R.id.menu_fav) {
                    startActivity(new Intent(Home.this, FavActivity.class));
                    return true;
                } else if (itemId == R.id.rate_us) {
                    String appPackageName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                    return true;
                } else if (itemId == R.id.disclaimer) {
                    Intent disIntent = new Intent(Home.this, WebActivity.class);
                    disIntent.putExtra("data", "disclaimer");
                    startActivity(disIntent);
                    return true;
                } else if (itemId == R.id.privacy_policy) {
                    Intent priIntent = new Intent(Home.this, WebActivity.class);
                    priIntent.putExtra("data", "privacy_policy");
                    startActivity(priIntent);
                    return true;
                } else if (itemId == R.id.terms_conditions) {
                    Intent termIntent = new Intent(Home.this, WebActivity.class);
                    termIntent.putExtra("data", "terms_conditions");
                    startActivity(termIntent);
                    return true;
                } else if (itemId == R.id.contact_us) {
                    Intent contactIntent = new Intent(Intent.ACTION_SENDTO);
                    contactIntent.setData(Uri.parse("mailto:app@jayrcm.com"));
                    contactIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");
                    startActivity(Intent.createChooser(contactIntent, "Send Email"));
                    return true;
                }
                return false;
            }
        });

        DrawerClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Code Here
                MainLayout.openDrawer(GravityCompat.START);
            }
        });

        requestPermissions();

        gridLayoutManager = new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);
        categoryAdapters = new CategoryAdapters(this,categoryList);
        recyclerView.setAdapter(categoryAdapters);


        ApiInterface.getApiRequestInterface().getCategory(Constants.API_KEY,"0")
                .enqueue(new Callback<List<Category>>() {
                    @Override
                    public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                        if (response.isSuccessful())
                        {
                            categoryList.addAll(response.body());
                            categoryAdapters.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Category>> call, Throwable t) {
                        Toast.makeText(Home.this, ""+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


       /* imageSliderList.add(new SlideModel("https://cdn.pixabay.com/photo/2015/12/01/20/28/road-1072821_960_720.jpg",ScaleTypes.FIT));
        imageSliderList.add(new SlideModel("https://cdn.pixabay.com/photo/2015/12/01/20/28/road-1072821_960_720.jpg",ScaleTypes.FIT));
        imageSliderList.add(new SlideModel("https://cdn.pixabay.com/photo/2015/12/01/20/28/road-1072821_960_720.jpg",ScaleTypes.FIT));
        imageSlider.setImageList(imageSliderList);*/

        ApiInterface.getApiRequestInterface().getSliders(Constants.API_KEY).enqueue(new Callback<List<Sliders>>() {
            @Override
            public void onResponse(Call<List<Sliders>> call, Response<List<Sliders>> response) {
                if (response.isSuccessful())
                {
                    List<Sliders> slides = response.body();

                    for (Sliders slide : slides)
                    {
                        imageSliderList.add(new SlideModel(slide.getImage(),ScaleTypes.CENTER_CROP));
                    }
                    imageSlider.setImageList(imageSliderList);
                    imageSlider.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onItemSelected(int i) {
                            String url = slides.get(i).getUrl();
                            if (url!=null)
                            {
                                Intent Slideintent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(Slideintent);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Sliders>> call, Throwable t) {

            }
        });


    }

    @Override
    public void onBackPressed() {

        if (MainLayout.isDrawerOpen(GravityCompat.START))
        {
            MainLayout.closeDrawer(GravityCompat.START);
        }
        showExitDialog();

        /*if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }*/

        /*this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 4000);*/
    }

    private void showExitDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.custom_sheet_dialog);
        Button rateusBtn = dialog.findViewById(R.id.modal_sheet_rate_us);
        Button shareBtn = dialog.findViewById(R.id.modal_sheet_share);
        Button quitBtn = dialog.findViewById(R.id.modal_sheet_quit);

        quitBtn.setOnClickListener(v -> {
            finishAffinity();
        });

        rateusBtn.setOnClickListener(v -> {
            String appPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });

        shareBtn.setOnClickListener(v -> {
            String appPackageName = getPackageName();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this app: https://play.google.com/store/apps/details?id=" + appPackageName);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share using"));
        });

        if (dialog.isShowing())
            dialog.dismiss();
        else
            dialog.show();
    }

    public void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtil.checkAndRequestPermissions(this,
                    Manifest.permission.POST_NOTIFICATIONS)) {
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, grantResults, this);
    }

    @Override
    public void permissionsGranted() {
    }

    @Override
    public void permissionsDenied() {
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        showSnackBar(isConnected,false);
    }

    private void checkConnection(boolean recreate) {

        // initialize intent filter
        IntentFilter intentFilter = new IntentFilter();

        // add action
        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");

        // register receiver
        registerReceiver(new ConnectionReceiver(), intentFilter);

        // Initialize listener
        ConnectionReceiver.Listener = this;

        // Initialize connectivity manager
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Initialize network info
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        // get connection status
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        // display snack bar
        showSnackBar(isConnected, recreate);
    }

    private void showSnackBar(boolean isConnected, boolean recreate) {
        if (isConnected)
        {
            if (recreate)
                recreate();
        }
        else
        {
            Snackbar snackbar = Snackbar.make(MainLayout,"",Snackbar.LENGTH_INDEFINITE);
            View customView = getLayoutInflater().inflate(R.layout.custom_snackbar,null);
            snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
            Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
            snackbarLayout.setPadding(0,0,0,0);

            TextView titleText = customView.findViewById(R.id.custom_snackbar_text);
            Button Btn = customView.findViewById(R.id.custom_snackbar_btn);

            titleText.setText("Not Connected to Internet");
            Btn.setText("Retry");

            Btn.setOnClickListener(v ->
            {
                snackbar.dismiss();
                checkConnection(true);
            });

            snackbarLayout.addView(customView,0);
            snackbar.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // call method
        checkConnection(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // call method
        checkConnection(false);
    }
}