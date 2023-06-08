package com.vestige.productpricelist.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.vestige.productpricelist.adapters.ProductAdapters;
import com.vestige.productpricelist.adapters.SearchAdapters;
import com.vestige.productpricelist.api.ApiInterface;
import com.vestige.productpricelist.api.Constants;
import com.vestige.productpricelist.models.Product;
import com.vestige.productpricelist.sqlite.SearchModels;
import com.vestige.productpricelist.sqlite.SearchProductDBController;
import com.vestige.productpricelist.utils.ConnectionReceiver;
import com.vestige.productpricelist.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductActivity extends AppCompatActivity implements ConnectionReceiver.ReceiverListener {

    public static List<Product> productList;
    public static ProductAdapters productAdapters;
    public static RecyclerView recyclerView, searchRecyclerview;
    private LinearLayout MainLayout;
    private int Click = 0,countInt;
    private String adType, adStatus, interstitialAdsCount, adMobBannerID, FacebookBannerID, interstitialAdmob, interstitialFacebook, nativeAdmob, catID, catTitle;
    private LinearLayout admobBanner;
    private com.facebook.ads.AdView adView;
    private LinearLayout facebookBanner;
    private ProgressBar progressBar;
    private TextView ToolbarTitle;
    private LinearLayout SearchLayout, ToolbarLayout;
    private ImageView SearchBack, SearchBtn, SearchClear;
    public static EditText SearchInput;
    private SearchAdapters searchAdapters;
    private List<SearchModels> searchModelsList;
    private SearchProductDBController searchProductDBController;
    private final long DELAY = 2000;
    private boolean running = false;
    private boolean notify;
    boolean isLoading = false;
    private int fromItem = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        notify = getIntent().getBooleanExtra("notify",false);

        catID = getIntent().getStringExtra("catid");
        catTitle = getIntent().getStringExtra("catname");

        recyclerView = findViewById(R.id.product_recyclerview);
        MainLayout = findViewById(R.id.product_main_layout);
        admobBanner = findViewById(R.id.product_adView);
        facebookBanner = findViewById(R.id.product_facebook_banner);
        progressBar = findViewById(R.id.progressbar);
        searchRecyclerview = findViewById(R.id.product_search_history_recyclerview);

        ToolbarTitle = findViewById(R.id.product_toolbar_title);
        SearchLayout = findViewById(R.id.product_search_layout);
        ToolbarLayout = findViewById(R.id.product_toolbar_layout);
        SearchBack = findViewById(R.id.product_search_back);
        SearchInput = findViewById(R.id.product_search_edit);
        SearchBtn = findViewById(R.id.product_search_btn);
        SearchClear = findViewById(R.id.product_search_clear);

        ToolbarTitle.setText(catTitle);


        SearchBtn.setOnClickListener(v -> {
            loadSearchHistoryData();
            ToolbarLayout.setVisibility(View.GONE);
            SearchLayout.setVisibility(View.VISIBLE);
            showSoftKeyboard(SearchInput);
        });

        SearchBack.setOnClickListener(v -> {
            ToolbarLayout.setVisibility(View.VISIBLE);
            SearchLayout.setVisibility(View.GONE);
            SearchInput.setText(null);
            hideSoftKeyboard(SearchInput, this);
        });

        SearchClear.setOnClickListener(v ->
        {
            SearchInput.setText(null);
            filter("");
            loadSearchHistoryData();
        });


        SearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
                searchRecyclerview.setVisibility(View.GONE);

                if (!running)
                {
                    running = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!s.toString().isEmpty())
                                searchProductDBController.insertData(s.toString());

                            running = false;
                        }
                    },DELAY);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        productList = new ArrayList<>();
        searchModelsList = new ArrayList<>();

        adType = Tools.getString(this,"AdsType");
        adStatus = Tools.getString(this,"AdsStatus");
        interstitialAdsCount = Tools.getString(this,"interstitialAdsCount");
        adMobBannerID = Tools.getString(this,"bannerAdmob");
        FacebookBannerID = Tools.getString(this,"bannerFacebook");
        interstitialAdmob = Tools.getString(this,"interstitialAdmob");
        interstitialFacebook = Tools.getString(this,"interstitialFacebook");
        nativeAdmob = Tools.getString(this,"nativeAdmob");
        Click = Tools.getInt(this,"click");
        countInt = Integer.parseInt(interstitialAdsCount);

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

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        productAdapters = new ProductAdapters(this,productList, "product");
        recyclerView.setAdapter(productAdapters);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        searchRecyclerview.setLayoutManager(linearLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productList.size() - 1 && productList.size()>0) {
                        //bottom of list!
                        fromItem = fromItem + 25;
                        LoadMoreData(fromItem);
                        isLoading = true;
                    }
                }
            }
        });
    }

    public void showSoftKeyboard(View view){
        if(view.requestFocus()){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void hideSoftKeyboard(View view, Context context){
        InputMethodManager imm =(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void loadSearchHistoryData() {
        if (searchModelsList.size()>0)
            searchModelsList.clear();

        searchProductDBController = new SearchProductDBController(this);
        searchModelsList = searchProductDBController.getAllData();
        if (searchModelsList.size()>0)
        {
            searchAdapters = new SearchAdapters(searchModelsList,this,"product");
            searchRecyclerview.setAdapter(searchAdapters);
            searchAdapters.notifyDataSetChanged();
            searchRecyclerview.setVisibility(View.VISIBLE);
        }
        else
            searchRecyclerview.setVisibility(View.GONE);
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

    private void LoadData() {

        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        if (catID!=null)
        {
            ApiInterface.getApiRequestInterface().getProductsByCat(Constants.API_KEY, "0",catID)
                    .enqueue(new Callback<List<Product>>() {
                        @Override
                        public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                            if (response.isSuccessful())
                            {

                                recyclerView.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                List<Product> products = response.body();
                                productList.addAll(products);
                                productAdapters.notifyDataSetChanged();
                                if (productList.size()<=0)
                                    Tools.showSnackbar(ProductActivity.this,MainLayout,"No Products Found...","Okay");
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Product>> call, Throwable t) {
                        }
                    });
        }
    }

    private void LoadMoreData(int fromItem) {
        if (catID!=null)
        {
            ApiInterface.getApiRequestInterface().getProductsByCat(Constants.API_KEY, String.valueOf(fromItem),catID)
                    .enqueue(new Callback<List<Product>>() {
                        @Override
                        public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                            if (response.isSuccessful())
                            {
                                recyclerView.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                List<Product> products = response.body();
                                productList.addAll(products);
                                productAdapters.notifyDataSetChanged();
                                isLoading = false;
                                if (productList.size()<=0)
                                    Tools.showSnackbar(ProductActivity.this,MainLayout,"No Products Found...","Okay");
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Product>> call, Throwable t) {
                        }
                    });
        }
    }

    public static void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<Product> filteredlist = new ArrayList<Product>();

        // running a for loop to compare elements.
        for (Product item : productList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getProductName().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            productAdapters.filterList(filteredlist);
        }
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (SearchLayout.getVisibility()==View.VISIBLE)
        {
            ToolbarLayout.setVisibility(View.VISIBLE);
            SearchLayout.setVisibility(View.GONE);
            SearchInput.setText(null);
            hideSoftKeyboard(SearchInput,this);
        }
        else
        {
            if (adStatus.equals("1"))
            {
                if (adType.equals("1"))
                {
                    Click = Tools.getInt(this,"click");

                    if (Click==0){
                        Tools.showInterstitialAdfromAdmob(this);
                        Tools.loadInterstitialAdfromAdmob(this);
                        Tools.loadInterstitialAdromFacebook(this);
                    }
                    else if (Click==countInt)
                    {
                        Tools.showInterstitialAdfromAdmob(this);
                        Tools.loadInterstitialAdfromAdmob(this);
                        Tools.loadInterstitialAdromFacebook(this);
                    }
                    else
                    {
                        int value = Tools.getInt(this,"click");
                        Tools.saveInt(this,"click",1+value);
                    }
                }
                else if(adType.equals("2"))
                {
                    Click = Tools.getInt(this,"click");

                    if (Click==0){
                        Tools.showInterstitialAdfromFacebook(this);
                        Tools.loadInterstitialAdfromAdmob(this);
                        Tools.loadInterstitialAdromFacebook(this);
                    }
                    else if (Click==countInt)
                    {
                        Tools.showInterstitialAdfromAdmob(this);
                        Tools.loadInterstitialAdfromAdmob(this);
                        Tools.loadInterstitialAdromFacebook(this);
                    }
                    else
                    {
                        int value = Tools.getInt(this,"click");
                        Tools.saveInt(this,"click",1+value);
                    }
                }
            }
            if (notify)
                startActivity(new Intent(ProductActivity.this, Home.class));
            else
                super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adStatus.equals("1"))
        {
            Tools.loadInterstitialAdfromAdmob(this);
            Tools.loadInterstitialAdromFacebook(this);
        }
        checkConnection(false);

        if (productList.size()>0)
            productList.clear();

        isLoading = false;
        fromItem = 0;

        LoadData();
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
    protected void onPause() {
        super.onPause();
        // call method
        checkConnection(false);
    }

    public static void SelectSearch(String searchText, Context context)
    {
        SearchInput.setText(searchText);
        searchRecyclerview.setVisibility(View.GONE);
        hideSoftKeyboard(SearchInput, context);
        filter(searchText);
    }
}