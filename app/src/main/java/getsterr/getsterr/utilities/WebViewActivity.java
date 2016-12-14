package getsterr.getsterr.utilities;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import getsterr.getsterr.R;

public class WebViewActivity extends AppCompatActivity {

    private static final String TAG = WebViewActivity.class.getSimpleName();

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        initViews();

        String url = getBundleUrl();
        setWebView(url);
    }

    private void initViews(){
        webView = (WebView)findViewById(R.id.WebView);
        progressBar = (ProgressBar)findViewById(R.id.webView_progressBar);
    }

    private String getBundleUrl(){
        return getIntent().getExtras().getString(Constants.URL_INTENTKEY, "http://www.getsterr.com");
    }

    private void setWebView(String url){
        CustomWebViewClient customWebViewClient = new CustomWebViewClient();
        webView.setWebViewClient(customWebViewClient);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        customWebViewClient.onLoadResource(webView, url);
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            Log.d("WebViewFragment", "onLoadResource: web page is loading");
        }

//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
////            return super.shouldOverrideUrlLoading(view, url);
//            view.loadUrl(url);
//            return false;
//        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}
