package com.vestige.productpricelist.activity;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.vestige.productpricelist.R;
import com.vestige.productpricelist.adapters.SinglePageAdapters;
import com.vestige.productpricelist.api.ApiInterface;
import com.vestige.productpricelist.api.Constants;
import com.vestige.productpricelist.models.Product;
import com.vestige.productpricelist.sqlite.FavModels;
import com.vestige.productpricelist.sqlite.FavProductDBController;
import com.vestige.productpricelist.utils.ConnectionReceiver;
import com.vestige.productpricelist.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleProduct extends AppCompatActivity implements ConnectionReceiver.ReceiverListener{

    private ViewPager2 viewPager2;
    private List<Product> productList;
    private List<Product> products;
    private SinglePageAdapters singlePageAdapters;
    private String page;
    private int item_limit;
    public static TextView ToolbarTitle;
    private ImageView BackImg;
    private int Click = 0;
    private String adType, adStatus, adMobBannerID, FacebookBannerID, catID, interstitialAdmob, interstitialFacebook, nativeAdmob;
    private LinearLayout admobBanner;
    private com.facebook.ads.AdView adView;
    private LinearLayout facebookBanner, MainLayout;
    private ProgressBar progressBar;
    private int id;
    boolean isLoading = false;
    private int fromItem = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product);

        productList = new ArrayList<>();
        products = new ArrayList<>();

        String title = getIntent().getStringExtra("title");
        id = Integer.parseInt(getIntent().getStringExtra("id"));
        page = getIntent().getStringExtra("page");
        catID = getIntent().getStringExtra("catid");
        item_limit = Integer.parseInt(Tools.getString(this,"adsLimit"));

        adType = Tools.getString(this,"AdsType");
        adStatus = Tools.getString(this,"AdsStatus");
        //adCounts = Tools.getString(this,"AdsCount");
        adMobBannerID = Tools.getString(this,"bannerAdmob");
        FacebookBannerID = Tools.getString(this,"bannerFacebook");
        interstitialAdmob = Tools.getString(this,"interstitialAdmob");
        interstitialFacebook = Tools.getString(this,"interstitialFacebook");
        nativeAdmob = Tools.getString(this,"nativeAdmob");
        Click = Tools.getInt(this,"click");
        //countInt = Integer.parseInt(adCounts);

        viewPager2 = findViewById(R.id.single_product_viewpager);
        ToolbarTitle = findViewById(R.id.single_product_toolbar_title);
        BackImg = findViewById(R.id.single_product_toolbar_back);
        admobBanner = findViewById(R.id.single_product_adView);
        facebookBanner = findViewById(R.id.single_product_facebook_banner);
        MainLayout = findViewById(R.id.single_product_layout);
        progressBar = findViewById(R.id.progressbar);


        if (title!=null)
            ToolbarTitle.setText(title);

        if (adStatus.equals("1"))
        {
            if (adType.equals("1"))
            {
                loadAdMobBanner();
            }
            else if(adType.equals("2"))
            {
                LoadFacebookBanner();
            }
        }

        LoadData();

        BackImg.setOnClickListener(v -> onBackPressed());

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if ((position + 1) % item_limit == 0)
                    ToolbarTitle.setText("Ads");
                else
                {
                    int pos = position - Math.round(position / item_limit);
                    ToolbarTitle.setText(productList.get(pos).getProductName());
                }
            }
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (!isLoading) {
                    if (position == singlePageAdapters.getItemCount() - 1 && productList.size()>0) {
                        fromItem = fromItem + 25;
                        LoadMoreData(fromItem);
                        isLoading = true;
                    }
                }


            }
        });

    }

    private void LoadData() {

        isLoading = false;
        fromItem = 0;

        if (page.equals("fav"))
        {
            ApiInterface.getApiRequestInterface().getProductsByID(Constants.API_KEY, String.valueOf(id))
                    .enqueue(new Callback<List<Product>>() {
                        @Override
                        public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                            if (response.isSuccessful())
                            {
                                productList.add(response.body().get(0));
                                ApiInterface.getApiRequestInterface().getProductsByCat(Constants.API_KEY, "0",catID)
                                        .enqueue(new Callback<List<Product>>() {
                                            @Override
                                            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                                                if (response.isSuccessful())
                                                {
                                                    viewPager2.setVisibility(View.VISIBLE);
                                                    progressBar.setVisibility(View.GONE);
                                                    products = response.body();
                                                    FavProductDBController favProductDBController = new FavProductDBController(SingleProduct.this);
                                                    List<FavModels> favModelsList = favProductDBController.getAllData();
                                                    for (FavModels favModels : favModelsList)
                                                    {
                                                        for (Product product : products)
                                                        {
                                                            if (Integer.parseInt(product.getId())==favModels.getSlno())
                                                            {
                                                                if (Integer.parseInt(product.getId()) != id) {
                                                                    productList.add(product);
                                                                }
                                                            }
                                                        }
                                                    }

                                                    singlePageAdapters = new SinglePageAdapters(SingleProduct.this,productList,item_limit);
                                                    viewPager2.setAdapter(singlePageAdapters);
                                                    singlePageAdapters.notifyDataSetChanged();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<List<Product>> call, Throwable t) {

                                            }
                                        });
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Product>> call, Throwable t) {

                        }
                    });
        }
        else
        {
            ApiInterface.getApiRequestInterface().getProductsByID(Constants.API_KEY, String.valueOf(id))
                    .enqueue(new Callback<List<Product>>() {
                        @Override
                        public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                            if (response.isSuccessful())
                            {
                                productList.add(response.body().get(0));
                                ApiInterface.getApiRequestInterface().getProductsByCat(Constants.API_KEY,"0",catID)
                                        .enqueue(new Callback<List<Product>>() {
                                            @Override
                                            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                                                if (response.isSuccessful())
                                                {
                                                    viewPager2.setVisibility(View.VISIBLE);
                                                    progressBar.setVisibility(View.GONE);
                                                    products = response.body();

                                                    for (int i = 0; i<products.size(); i++) {
                                                        if (Integer.parseInt(products.get(i).getId()) != id) {
                                                            productList.add(products.get(i));
                                                        }
                                                    }
                                                    singlePageAdapters = new SinglePageAdapters(SingleProduct.this,productList,item_limit);
                                                    viewPager2.setAdapter(singlePageAdapters);
                                                    singlePageAdapters.notifyDataSetChanged();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<List<Product>> call, Throwable t) {
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Product>> call, Throwable t) {

                        }
                    });
        }
    }

    private void LoadMoreData(int fromItem) {
        if (page.equals("fav"))
        {
            ApiInterface.getApiRequestInterface().getProductsByCat(Constants.API_KEY, String.valueOf(fromItem),catID)
                    .enqueue(new Callback<List<Product>>() {
                        @Override
                        public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                            if (response.isSuccessful())
                            {
                                viewPager2.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                products = response.body();
                                FavProductDBController favProductDBController = new FavProductDBController(SingleProduct.this);
                                List<FavModels> favModelsList = favProductDBController.getAllData();
                                for (FavModels favModels : favModelsList)
                                {
                                    for (Product product : products)
                                    {
                                        if (Integer.parseInt(product.getId())==favModels.getSlno())
                                        {
                                            if (Integer.parseInt(product.getId()) != id) {
                                                productList.add(product);
                                            }
                                        }
                                    }
                                }

                                if (products.size() > 0)
                                {
                                    singlePageAdapters = new SinglePageAdapters(SingleProduct.this,productList,item_limit);
                                    viewPager2.setAdapter(singlePageAdapters);
                                    singlePageAdapters.notifyDataSetChanged();
                                }
                                isLoading = false;
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Product>> call, Throwable t) {

                        }
                    });
        }
        else
        {
            ApiInterface.getApiRequestInterface().getProductsByCat(Constants.API_KEY, String.valueOf(fromItem),catID)
                    .enqueue(new Callback<List<Product>>() {
                        @Override
                        public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                            if (response.isSuccessful())
                            {
                                viewPager2.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                products = response.body();


                                for (int i = 0; i<products.size(); i++) {
                                    if (Integer.parseInt(products.get(i).getId()) != id) {
                                        productList.add(products.get(i));
                                    }
                                }
                                if (products.size() > 0) {
                                    singlePageAdapters = new SinglePageAdapters(SingleProduct.this,productList,item_limit);
                                    viewPager2.setAdapter(singlePageAdapters);
                                    singlePageAdapters.notifyDataSetChanged();
                                }
                                isLoading = false;
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Product>> call, Throwable t) {
                        }
                    });
        }
    }

    private void LoadFacebookBanner() {
        admobBanner.setVisibility(View.GONE);
        facebookBanner.setVisibility(View.VISIBLE);
        AudienceNetworkAds.initialize(this);
        adView = new com.facebook.ads.AdView(this, FacebookBannerID, AdSize.BANNER_HEIGHT_50);
        facebookBanner.addView(adView);
        com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                loadAdMobBanner();
            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
    }

    private void loadAdMobBanner() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        admobBanner.setVisibility(View.VISIBLE);
        facebookBanner.setVisibility(View.GONE);
        AdView mAdView = new AdView(this);
        mAdView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
        mAdView.setAdUnitId(adMobBannerID);
        AdRequest adRequest = new AdRequest.Builder().build();
        if(mAdView.getAdSize() != null || mAdView.getAdUnitId() != null)
            mAdView.loadAd(adRequest);

        admobBanner.addView(mAdView);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                LoadFacebookBanner();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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