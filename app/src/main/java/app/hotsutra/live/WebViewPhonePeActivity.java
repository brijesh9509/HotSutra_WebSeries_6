package app.hotsutra.live;

import static app.hotsutra.live.network.RetrofitClient.API_URL_EXTENSION;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import app.hotsutra.live.utils.AESHelper;
import app.hotsutra.live.utils.MyAppClass;

public class WebViewPhonePeActivity extends AppCompatActivity {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = findViewById(R.id.webView);

        String s = getIntent().getStringExtra("url");

        webView.setWebChromeClient(new WebChromeClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //String currentUrl = url;
                Log.e("currentUrl", ": " + url);
                view.loadUrl(url);

                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.e("onPageFinished", ": " + url);
                /*if (url.equalsIgnoreCase(AESHelper.decrypt(MyAppClass.HASH_KEY, AppConfig.API_SERVER_URL) + API_URL_EXTENSION + "phonepe_transaction_callback_response")) {
                    finish();
                }*/
                super.onPageFinished(view, url);
            }
        });

        webView.loadUrl(s);

    }

}
