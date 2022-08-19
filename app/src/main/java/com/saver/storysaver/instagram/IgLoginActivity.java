package com.saver.storysaver.instagram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.saver.storysaver.R;
import com.saver.storysaver.utils.Util;

public class IgLoginActivity extends AppCompatActivity {


    WebView webView;

    String url = "https://www.instagram.com/accounts/login/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ig_login);

        initView();

        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);


    }

    private void initView() {

        webView = findViewById(R.id.webView);

    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            view.loadUrl();
            return super.shouldOverrideUrlLoading(view, request);

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);


        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            String[] cookie = (CookieManager.getInstance().getCookie(url)).split(";");
            Log.d("INSTA_LOG", "------------ all Cookies --------------" + cookie);


            SharedPreferences preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();


            for (int i = 0; i < cookie.length; i++) {

                if (cookie[i].contains("ds_user_id")) {

                    String str = cookie[i].split("=")[1];
                    Log.d("INSTA_LOG", "------------ ds_user_id --------------" + str);
                    editor.putString("ds_user_id", str);
                    editor.apply();

                } else if (cookie[i].contains("sessionid")) {

                    String str = cookie[i].split("=")[1];
                    Log.d("INSTA_LOG", "------------ sessionid --------------" + str);
                    editor.putString("sessionid", str);
                    editor.apply();

                }

            }

            if (!preferences.getString(Util.IG_U_NAME, "").isEmpty()
                    && !preferences.getString(Util.IG_SESSION_ID, "").isEmpty()) {
                editor.putBoolean(Util.IS_LOGIN, true);
                editor.apply();
                Toast.makeText(IgLoginActivity.this, "Login Sucessfully!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(IgLoginActivity.this, IgLinkActivity.class));
                finish();
            }

        }
    }
}