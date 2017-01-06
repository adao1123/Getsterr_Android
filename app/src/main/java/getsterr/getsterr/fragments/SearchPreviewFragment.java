package getsterr.getsterr.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import getsterr.getsterr.R;
import getsterr.getsterr.utilities.WebViewActivity;

/**
 * Created by adao1 on 1/4/2017.
 */

public class SearchPreviewFragment extends Fragment {
    private WebView webView;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        webView = (WebView)view.findViewById(R.id.WebView);
        progressBar = (ProgressBar)view.findViewById(R.id.webView_progressBar);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        setWebView();
    }

    public void setWebView(String url){
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
