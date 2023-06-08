package com.vestige.productpricelist.activity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.vestige.productpricelist.R;
import com.vestige.productpricelist.api.ApiInterface;
import com.vestige.productpricelist.api.Constants;
import com.vestige.productpricelist.models.Privacy;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebActivity extends AppCompatActivity {

    private TextView title;
    private ImageView backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        String data = getIntent().getStringExtra("data");

        title = findViewById(R.id.webview_toolbar_title);
        backbtn = findViewById(R.id.webview_toolbar_back);

        WebView webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        WebViewClient webViewClient = new WebViewClient();
        webView.setWebViewClient(webViewClient);

        ApiInterface.getApiRequestInterface().getAllText(Constants.API_KEY)
                .enqueue(new Callback<List<Privacy>>() {
                    @Override
                    public void onResponse(Call<List<Privacy>> call, Response<List<Privacy>> response) {
                        if (response.isSuccessful())
                        {
                            Privacy privacy = response.body().get(0);
                            if (data.equals("disclaimer"))
                            {
                                title.setText("Disclaimer");
                                webView.loadData(""+privacy.getDisclaimer(),
                                        "text/html", "UTF-8");
                            }
                            else if (data.equals("privacy_policy"))
                            {
                                title.setText("Privacy Policy");
                                webView.loadData(""+privacy.getPrivacyPolicy(),
                                        "text/html", "UTF-8");
                            }
                            else if (data.equals("terms_conditions"))
                            {
                                title.setText("Terms & Conditions");
                                webView.loadData(""+privacy.getTermsConditions(),
                                        "text/html", "UTF-8");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Privacy>> call, Throwable t) {

                    }
                });

        backbtn.setOnClickListener(v ->
        {
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}