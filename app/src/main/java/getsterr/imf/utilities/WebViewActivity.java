package getsterr.imf.utilities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import getsterr.imf.R;

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = WebViewActivity.class.getSimpleName();
    Toolbar dashBoardToolbar;
    private WebView webView;
    private ProgressBar progressBar;
//    LinearLayout menuHamburgerLayout;
    String url;
    LinearLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        initViews();
        initActionBar();
        url = getBundleUrl();
        setWebView(url);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.menu_hamburger_iv:
//                if (menuHamburgerLayout.getVisibility()==View.GONE) menuHamburgerLayout.setVisibility(View.VISIBLE);
//                else menuHamburgerLayout.setVisibility(View.GONE);
//                break;
            case R.id.menu_logo_iv:
                startShareAppIntent();
                break;
            case R.id.toolbar_icon:
                this.finish();
                break;
//            case R.id.menu_share_iv:
//                startShareIntent(url);
//                break;
//            case R.id.menu_login_tv:
//                Intent loginIntent = new Intent(this, LoginActivity.class);
//                startActivity(loginIntent);
//                break;
//            case R.id.menu_setup_tv:
//                Intent setUpIntent = new Intent(this, MainActivity.class);
//                startActivity(setUpIntent);
//                break;
            case R.id.menu_settings_tv:
//                openSetting();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        parentLayout.removeAllViews(); //the viewgroup the webview is attached to
        webView.loadUrl("about:blank");
        webView.stopLoading();
        webView.setWebChromeClient(null);
        webView.setWebViewClient(null);
        webView.destroy();
        webView = null;
    }

    private void initViews(){
        webView = (WebView)findViewById(R.id.WebView);
        progressBar = (ProgressBar)findViewById(R.id.webView_progressBar);
        dashBoardToolbar = (Toolbar) findViewById(R.id.dashboard_toolbar);
        parentLayout = (LinearLayout) findViewById(R.id.activity_web_view);
    }

    private String getBundleUrl(){
        return getIntent().getExtras().getString(Constants.URL_INTENTKEY, "http://www.getsterr.com");
    }

    private String getBundleTitle(){
        return getIntent().getExtras().getString(Constants.TITLE_INTENTKEY, "Search");
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
            progressBar.getIndeterminateDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void initActionBar() {
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.dashboard_toolbar);
        setSupportActionBar(mainToolbar);
        ActionBar mainActionBar = getSupportActionBar();
        mainActionBar.setDisplayShowHomeEnabled(false);
        mainActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = LayoutInflater.from(this);
        View actionBarView = inflater.inflate(R.layout.actionbar_layout, null);
        TextView toolbarTitleTv = (TextView) actionBarView.findViewById(R.id.menu_title_tv);
//        menuHamburgerLayout = (LinearLayout)findViewById(R.id.menu_hamburger_container);
//        TextView menuLoginTv = (TextView)findViewById(R.id.menu_login_tv);
//        TextView menuSetupTv = (TextView)findViewById(R.id.menu_setup_tv);
//        TextView menuSettingsTv = (TextView)findViewById(R.id.menu_settings_tv);
//        menuLoginTv.setOnClickListener(this);
//        menuSetupTv.setOnClickListener(this);
//        menuSettingsTv.setOnClickListener(this);
        toolbarTitleTv.setText(getBundleTitle());
        mainActionBar.setCustomView(actionBarView);
        mainActionBar.setDisplayShowCustomEnabled(true);
        ImageView loginButton = (ImageView) actionBarView.findViewById(R.id.menu_hamburger_iv);
        loginButton.setVisibility(View.GONE);
        loginButton.setOnClickListener(this);
        ImageView shareAppButton = (ImageView) actionBarView.findViewById(R.id.menu_logo_iv);
        shareAppButton.setOnClickListener(this);
        RelativeLayout toolbarIcon = (RelativeLayout) findViewById(R.id.toolbar_icon);
        toolbarIcon.setOnClickListener(this);
//        ImageView shareButton = (ImageView)actionBarView.findViewById(R.id.menu_share_iv);
//        shareButton.setOnClickListener(this);
//        if (!getBundleTitle().equals("Search")) shareButton.setVisibility(View.GONE);
    }

    private void startShareIntent(String url) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Check out this Getsterr search!");
        i.putExtra(Intent.EXTRA_TEXT, "I found this on Getsterr - " + url);
        startActivity(Intent.createChooser(i, "Share via"));
    }

    private void startShareAppIntent() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Hey! Get Getsterr!");
        i.putExtra(Intent.EXTRA_TEXT, "Hey! Get Getsterr! \nhttps://play.google.com/store/apps/details?id=getsterr.imf&ah=02E7Fv-xKVeD-_tcwnyRMeEnxfc");
        startActivity(Intent.createChooser(i, "Share via"));
    }

}
