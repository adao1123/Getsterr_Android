package getsterr.imf.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import getsterr.imf.R;

/**
 * Created by adao1 on 1/4/2017.
 */

public class SearchPreviewFragment extends Fragment implements View.OnClickListener{
    private WebView webView;
    private ProgressBar progressBar;
    private FrameLayout clickablePreview;
    private String url;
    private OnPreviewClickListener onPreviewClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview,container,false);
        webView = (WebView)view.findViewById(R.id.WebView);
        progressBar = (ProgressBar)view.findViewById(R.id.webView_progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        clickablePreview = (FrameLayout)view.findViewById(R.id.clickable_preview_screen);
        url = getArguments().getString("url");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        clickablePreview.setOnClickListener(this);
        setWebView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onPreviewClickListener = (OnPreviewClickListener)getActivity();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.clickable_preview_screen:
                onPreviewClickListener.onPreviewClicked(url);
                break;
        }
    }

    public interface OnPreviewClickListener{
        void onPreviewClicked(String url);
    }

    public void setWebView(){
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
            progressBar.setVisibility(View.GONE);
        }
    }

}
