package getsterr.getsterr.activities.dashboard;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKPin;
import com.pinterest.android.pdk.PDKResponse;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import getsterr.getsterr.R;
import getsterr.getsterr.activities.login.LoginActivity;
import getsterr.getsterr.activities.main.MainActivity;
import getsterr.getsterr.activities.youtube.YoutubeDisplayActivity;
import getsterr.getsterr.fragments.SearchPreviewFragment;
import getsterr.getsterr.models.bing.BingImageResult;
import getsterr.getsterr.models.bing.BingResult;
import getsterr.getsterr.models.bing.BingResultCard;
import getsterr.getsterr.models.SocialMediaCard;
import getsterr.getsterr.models.bing.BingVideoResult;
import getsterr.getsterr.models.bing.Value;
import getsterr.getsterr.models.facebook.FacebookFeedObject;
import getsterr.getsterr.models.instagram.InstagramResponseObj;
import getsterr.getsterr.models.instagram.InstagramUserResponse;
import getsterr.getsterr.models.youtube.YoutubeObject;
import getsterr.getsterr.providers.BingAPISearchService;
import getsterr.getsterr.utilities.ApiServiceManager;
import getsterr.getsterr.utilities.Constants;
import getsterr.getsterr.utilities.WebViewActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DashBoardActivity extends AppCompatActivity implements View.OnClickListener, DashBoardRVAdapter.CardClickListener,
        DashBoardRVAdapter.YoutubeCardClickListener, DashBoardRVAdapter.CardLongClickListener, DashBoardRVAdapter.LastResultShownListener,
        SearchPreviewFragment.OnPreviewClickListener, View.OnTouchListener {

    private static final String TAG = DashBoardActivity.class.getSimpleName();

    RecyclerView dashBoardRecyclerView;

    ImageButton twitterButton, pinterestButton, linkedinButton, youtubeButton, instagramButton, facebookButton;
    Toolbar dashBoardToolbar;
    ActionBar dashBoardActionBar;
    Map<String, Boolean> checkedMap;
    String query;
    FrameLayout previewContainer;
    EditText dashSearchEditText;
    TextView webSearchButton;
    TextView imageSearchButton;
    TextView videoSearchButton;
    boolean isKeyboardOpen;
    private boolean isSettingInit = false;
    private boolean isPreviewOpen = false;
    private boolean isPreviewEnabled = true; //save to preferences
    float startX;
    FrameLayout previewContainerFrame;
    LinearLayout searchOptionBar;
    DashBoardRVAdapter dashBoardRVAdapter;
    List<List<Object>> newsFeedObjectLists = new ArrayList<>();
    ArrayList<Object> socialMediaItemList = new ArrayList<>();
    List<Object> searchItemList = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshContainer;
    LinearLayout menuHamburgerLayout;
    Dialog settingDialog;
    private static final int viewSize = 66;    private static final int boundary = 35;
    private float dx;
    private int width;
    private String selectedUrl;
    private PDKResponse myPinsResponse;
    private boolean loading = false;
    private static final String PIN_FIELDS = "id,link,creator,image,counts,note,created_at,board,metadata";
    private static final String FB_PERMISSIONS = "id,name,link,icon,message,created_time,description,picture,story,permalink_url,from, full_picture";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        logKeyHash();
        initViews();
        initActionBar();
        setSocialMediaButtonListeners();
        displayRv(socialMediaItemList);
        handleSearchOptions();
        // Setup search bar
        storeCheckedButtons();
        setSearchEditTextListener();

        // Get items from social media
        displayNewsFeed();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        swipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newsFeedObjectLists.clear();
                displayNewsFeed();
                swipeRefreshContainer.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_hamburger_iv:
                if (menuHamburgerLayout.getVisibility()==View.GONE) menuHamburgerLayout.setVisibility(View.VISIBLE);
                else menuHamburgerLayout.setVisibility(View.GONE);
                break;
            case R.id.menu_logo_iv:
                newsFeedObjectLists.clear();
                displayNewsFeed();
                swipeRefreshContainer.setEnabled(true);
                searchOptionBar.setVisibility(View.GONE);
                break;
            case R.id.menu_login_tv:
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                break;
            case R.id.menu_setup_tv:
                Intent setUpIntent = new Intent(this, MainActivity.class);
                startActivity(setUpIntent);
                break;
            case R.id.menu_settings_tv:
                openSetting();
                break;
            case R.id.preview_container_cover:
                previewContainerFrame.setVisibility(View.GONE);
                isPreviewOpen = false;
                Intent intent = new Intent(DashBoardActivity.this, WebViewActivity.class);
                intent.putExtra(Constants.URL_INTENTKEY, selectedUrl);
                startActivity(intent);
                break;
            case R.id.search_option_web:
                webSearchButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                webSearchButton.setTypeface(null,Typeface.BOLD);
                webSearchButton.setPaintFlags(webSearchButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                imageSearchButton.setTextColor(getResources().getColor(R.color.lightgray));
                imageSearchButton.setTypeface(Typeface.DEFAULT);
                imageSearchButton.setPaintFlags(imageSearchButton.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                videoSearchButton.setTextColor(getResources().getColor(R.color.lightgray));
                videoSearchButton.setTypeface(Typeface.DEFAULT);
                videoSearchButton.setPaintFlags(videoSearchButton.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                searchItemList.clear();
                makeBingApiCall(dashSearchEditText.getText().toString(), 0);
                break;
            case R.id.search_option_image:
                imageSearchButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                imageSearchButton.setTypeface(null,Typeface.BOLD);
                imageSearchButton.setPaintFlags(webSearchButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                webSearchButton.setTextColor(getResources().getColor(R.color.lightgray));
                webSearchButton.setTypeface(Typeface.DEFAULT);
                webSearchButton.setPaintFlags(webSearchButton.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                videoSearchButton.setTextColor(getResources().getColor(R.color.lightgray));
                videoSearchButton.setTypeface(Typeface.DEFAULT);
                videoSearchButton.setPaintFlags(videoSearchButton.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                makeBingImageApiCall(dashSearchEditText.getText().toString(), 0);
                break;
            case R.id.search_option_video:
                videoSearchButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                videoSearchButton.setTypeface(null,Typeface.BOLD);
                videoSearchButton.setPaintFlags(webSearchButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                imageSearchButton.setTextColor(getResources().getColor(R.color.lightgray));
                imageSearchButton.setTypeface(Typeface.DEFAULT);
                imageSearchButton.setPaintFlags(imageSearchButton.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                webSearchButton.setTextColor(getResources().getColor(R.color.lightgray));
                webSearchButton.setTypeface(Typeface.DEFAULT);
                webSearchButton.setPaintFlags(webSearchButton.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                makeBingVideoApiCall(dashSearchEditText.getText().toString(), 0);
                break;
            case R.id.settings_preview_switch:
                isPreviewEnabled = !isPreviewEnabled;
                Log.i(TAG, "onClick: is Preview Enabled " + isPreviewEnabled);
                break;
            case R.id.settings_save_button:
                settingDialog.cancel();
                break;
            case R.id.dash_facebook_button:
                Intent facebookIntent = new Intent(DashBoardActivity.this, WebViewActivity.class);
                facebookIntent.putExtra(Constants.URL_INTENTKEY, Constants.FACEBOOK_HOME_URL);
                startActivity(facebookIntent);
                break;
            case R.id.dash_twitter_button:
                Intent twitterIntent = new Intent(DashBoardActivity.this, WebViewActivity.class);
                twitterIntent.putExtra(Constants.URL_INTENTKEY, Constants.TWITTER_HOME_URL);
                startActivity(twitterIntent);
                break;
            case R.id.dash_instagram_button:
                Intent instagramIntent = new Intent(DashBoardActivity.this, WebViewActivity.class);
                instagramIntent.putExtra(Constants.URL_INTENTKEY, Constants.INSTAGRAM_HOME_URL);
                startActivity(instagramIntent);
                break;
            case R.id.dash_youtube_button:
                Intent youtubeIntent = new Intent(DashBoardActivity.this, WebViewActivity.class);
                youtubeIntent.putExtra(Constants.URL_INTENTKEY, Constants.YOUTUBE_HOME_URL);
                startActivity(youtubeIntent);
                break;
            case R.id.dash_linkedin_button:
                Intent linkedInIntent = new Intent(DashBoardActivity.this, WebViewActivity.class);
                linkedInIntent.putExtra(Constants.URL_INTENTKEY, Constants.LINKEDIN_HOME_URL);
                startActivity(linkedInIntent);
                break;
            case R.id.dash_pinterest_button:
                Intent pinterestIntent = new Intent(DashBoardActivity.this, WebViewActivity.class);
                pinterestIntent.putExtra(Constants.URL_INTENTKEY, Constants.PINTEREST_HOME_URL);
                startActivity(pinterestIntent);
                break;
        }
    }

    /**
     * When a card is clicked use url passed from DashBoardRVAdapter to open url in WebView Activity
     *
     * @param cardUrl
     */
    @Override
    public void onCardClick(String cardUrl) {
        selectedUrl = cardUrl;
        if (isPreviewEnabled) {
            if (previewContainer.getVisibility()==View.GONE) previewContainer.setVisibility(View.VISIBLE);
            showSearchPreview(cardUrl);
        }
        else {
            previewContainer.setVisibility(View.GONE);
            Intent intent = new Intent(DashBoardActivity.this, WebViewActivity.class);
            intent.putExtra(Constants.URL_INTENTKEY, cardUrl);
            startActivity(intent);
        }
//        handleFlickableDialog(cardUrl);
    }

    @Override
    public void onPreviewClicked(String url) {
        previewContainer.setVisibility(View.GONE);
        Intent intent = new Intent(DashBoardActivity.this, WebViewActivity.class);
        intent.putExtra(Constants.URL_INTENTKEY, url);
        startActivity(intent);
    }

    @Override
    public void onCardLongClick(String url) {
        startShareIntent(url);
    }

    @Override
    public void onYoutubeCardClick(String videoId) {
        Intent youtubeIntent = new Intent(this, YoutubeDisplayActivity.class);
        youtubeIntent.putExtra(YoutubeDisplayActivity.YOUTUBE_DISPLAY_KEY, videoId);
        startActivity(youtubeIntent);
    }

    @Override
    public void onLastResultShown(int offset, char searchType) {
        switch (searchType) {
            case 'w':
                makeBingApiCall(query, offset);
                break;
            case 'i':
                makeBingImageApiCall(query, offset);
                break;
            case 'v':
                makeBingVideoApiCall(query, offset);
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "onTouch: action down");
                dx = view.getX() - motionEvent.getRawX();
                startX =view.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "onTouch: action move");
//                if (motionEvent.getRawX()+dx >= width-convertDpToPx(boundary+viewSize)) return true; //sets boundaries
//                if (motionEvent.getRawX()+dx <= convertDpToPx(boundary)) return true; //sets boundaries
//                view.animate().x(motionEvent.getRawX()+dx).setDuration(0).start();
                previewContainerFrame.animate().x(motionEvent.getRawX()+dx).setDuration(0).start();
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onTouch: button release");
                if (motionEvent.getX()>getScreenWidth()*3/4) Log.i(TAG, "onTouch: past 3/4 screen");
                previewContainerFrame.animate().x(startX).setDuration(0).start();
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isPreviewOpen) {
            previewContainerFrame.setVisibility(View.GONE);
            isPreviewOpen = false;
        }
        else super.onBackPressed();
    }

    /**
     * Coverts the input dp and returns px
     * @param dp
     * @return
     */
    private float convertDpToPx(int dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getBaseContext().getResources().getDisplayMetrics());
    }

    private void initViews() {
        dashBoardRecyclerView = (RecyclerView) findViewById(R.id.dashbard_recyclerView);
        dashBoardToolbar = (Toolbar) findViewById(R.id.dashboard_toolbar);
        previewContainer = (FrameLayout) findViewById(R.id.preview_container);
        facebookButton = (ImageButton) findViewById(R.id.dash_facebook_button);
        twitterButton = (ImageButton) findViewById(R.id.dash_twitter_button);
        pinterestButton = (ImageButton) findViewById(R.id.dash_pinterest_button);
        linkedinButton = (ImageButton) findViewById(R.id.dash_linkedin_button);
        youtubeButton = (ImageButton) findViewById(R.id.dash_youtube_button);
        instagramButton = (ImageButton) findViewById(R.id.dash_instagram_button);
        dashSearchEditText = (EditText) findViewById(R.id.dash_search_editText);
        webSearchButton = (TextView)findViewById(R.id.search_option_web);
        imageSearchButton = (TextView)findViewById(R.id.search_option_image);
        videoSearchButton = (TextView)findViewById(R.id.search_option_video);
        searchOptionBar = (LinearLayout) findViewById(R.id.search_options_bar);
        swipeRefreshContainer = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_container);
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
        menuHamburgerLayout = (LinearLayout)findViewById(R.id.menu_hamburger_container);
        TextView menuLoginTv = (TextView)findViewById(R.id.menu_login_tv);
        TextView menuSetupTv = (TextView)findViewById(R.id.menu_setup_tv);
        TextView menuSettingsTv = (TextView)findViewById(R.id.menu_settings_tv);
        menuLoginTv.setOnClickListener(this);
        menuSetupTv.setOnClickListener(this);
        menuSettingsTv.setOnClickListener(this);
        toolbarTitleTv.setText("Dashboard");
        mainActionBar.setCustomView(actionBarView);
        mainActionBar.setDisplayShowCustomEnabled(true);
        ImageView loginButton = (ImageView) actionBarView.findViewById(R.id.menu_hamburger_iv);
        loginButton.setOnClickListener(this);
        ImageView iconButton = (ImageView) actionBarView.findViewById(R.id.menu_logo_iv);
        iconButton.setOnClickListener(this);
    }

    private void setSocialMediaButtonListeners(){
        facebookButton.setOnClickListener(this);
        instagramButton.setOnClickListener(this);
        twitterButton.setOnClickListener(this);
        pinterestButton.setOnClickListener(this);
        youtubeButton.setOnClickListener(this);
        linkedinButton.setOnClickListener(this);
    }

    private void displayRv(List<Object> list) {
        dashBoardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dashBoardRVAdapter = new DashBoardRVAdapter(list, DashBoardActivity.this, this, this, this);
        dashBoardRecyclerView.setAdapter(dashBoardRVAdapter);
    }

    private void updateSearchRv() {
        dashBoardRVAdapter.notifyItemRangeInserted(searchItemList.size() - 25, 25);
    }

    private void displayNewsFeed() {
        makeCheckedApiCalls();
        displayRv(combineNewsFeedLists());
    }

    private void updateNewsFeed() {
        displayRv(combineNewsFeedLists());
    }

    private void makeCheckedApiCalls() {
        if (checkedMap.get(Constants.INSTAGRAM_CHECK_INTENTKEY)) handleInstagramApi();
        if (checkedMap.get(Constants.FACEBOOK_CHECK_INTENTKEY)) handleFacebookApi();
        if (checkedMap.get(Constants.LINKEDIN_CHECK_INTENTKEY)) handleLinkedInApi();
        if (checkedMap.get(Constants.PINTEREST_CHECK_INTENTKEY)) handlePinterestApi();
        if (checkedMap.get(Constants.TWITTER_CHECK_INTENTKEY)) handleTwitterApi();
    }

    private List<Object> combineNewsFeedLists() {
        List<Object> combinedList = new ArrayList<>();
        int min = 100; //max num
        for (List<Object> list : newsFeedObjectLists) if (list.size() < min) min = list.size();
        for (int index = 0; index < min; index++) {
            for (List<Object> list : newsFeedObjectLists) combinedList.add(list.get(index));
        }
        return combinedList;
    }

    private void combineSearchLists(List<Object> youtubeList, List<Object> bingList) {
        List<Object> searchList = new ArrayList<>();
        int minSize;
        if (youtubeList.size() <= bingList.size()) minSize = youtubeList.size();
        else minSize = bingList.size();
        for (int index = 0; index < minSize; index++) {
            searchList.add( bingList.get(index));
            searchList.add( youtubeList.get(index));
        }
        displayRv(searchList);
    }

    private void handleSearchOptions(){
        webSearchButton.setOnClickListener(this);
        videoSearchButton.setOnClickListener(this);
        imageSearchButton.setOnClickListener(this);
        webSearchButton.setPaintFlags(webSearchButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    /**
     * Perform Bing API Search when search icon is clicked
     */
    private void setSearchEditTextListener() {
//        dashSearchEditText.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                final int DRAWABLE_LEFT = 0;
//                final int DRAWABLE_TOP = 1;
//                final int DRAWABLE_RIGHT = 2;
//                final int DRAWABLE_BOTTOM = 3;
//
//                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    if(motionEvent.getRawX() >= (dashSearchEditText.getRight() - dashSearchEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
//                        // your action here
//                        Log.d(TAG, "onTouch: SEARCH BUTTON CLIKED");
//                        makeBingApiCall(dashSearchEditText.getText().toString());
////                        makeYoutubeApiCall(dashSearchEditText.getText().toString());
//
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
        dashSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO) {
                    Log.i(TAG, "onKey: enter clicked");
                    makeBingApiCall(dashSearchEditText.getText().toString(), 0);
                    searchOptionBar.setVisibility(View.VISIBLE);
                    swipeRefreshContainer.setEnabled(false);
//                    makeYoutubeApiCall(dashSearchEditText.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    // ----------------- YOUTUBE ------------------ //

    private void makeYoutubeApiCall(String query, final List<Object> bingList) {
        Call<YoutubeObject> call = ApiServiceManager.createYoutubeApiService().getYoutubeSearch(query);
        call.enqueue(new Callback<YoutubeObject>() {
            @Override
            public void onResponse(Call<YoutubeObject> call, Response<YoutubeObject> response) {
                YoutubeObject youtubeObject = response.body();
                Log.i(TAG, "onResponse: " + youtubeObject.getItems()[0].getId().getVideoId());
                List<Object> youtubeList = new ArrayList<Object>();
                for (YoutubeObject.Resource resource : response.body().getItems())
                    youtubeList.add(resource);
//                displayRv(dataList);
                combineSearchLists(youtubeList, bingList);
            }

            @Override
            public void onFailure(Call<YoutubeObject> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * Test data
     *
     * @return
     */
    private ArrayList<Object> getSampleArrayList() {
        ArrayList<Object> items = new ArrayList<>();

        items.add(new BingResultCard("Bing Search Result Title", "HH:MM:SS", "Bing Description Snippet", "http://www.yahoo.com"));
        items.add(new BingResultCard("Bing Search Result Title", "HH:MM:SS", "Bing Description Snippet", "http://www.yahoo.com"));
        items.add(new SocialMediaCard("Social Media Info", ContextCompat.getColor(DashBoardActivity.this, R.color.linkedin), R.drawable.ic_linkedin, "HH:MM:SS", "http://www.linkedin.com"));
        items.add(new SocialMediaCard("Social Media Info", ContextCompat.getColor(DashBoardActivity.this, R.color.facebook), R.drawable.ic_twitter, "HH:MM:SS", "http://www.twitter.com"));

        return items;
    }

    /**
     * Use RxJava to make a bing api call
     */
    private void makeBingApiCall(final String enteredQuery, final int offset) {
//        String input = dashSearchEditText.getText().toString();
        if (enteredQuery.equals("")) return;
        query = enteredQuery;
        if (offset == 0) hideKeyboard();
        BingAPISearchService.BingSearchRx bingSearch = BingAPISearchService.createRx(Constants.BING_API_SEARCH_URL);
        Observable<BingResult> observable = bingSearch.getBingAPIResult(query, 25, offset, "en-us", "Moderate", Constants.BING_SUBSCRIPTION_KEY);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BingResult>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(BingResult bingResults) {
                        Log.d(TAG, "onNext: BING RESULTS RETURNED");
                        Log.d(TAG, "onNext: " + bingResults.toString());
                        Log.d(TAG, "onNext: " + bingResults.getWebPages().getWebSearchUrl());

//                        ArrayList<Object> items = new ArrayList<Object>();

                        for (Value val : bingResults.getWebPages().getValue()) {
                            searchItemList.add(val);
                        }
                        if (offset > 0) updateSearchRv();
//                        else if (checkedMap.get(Constants.YOUTUBE_CHECK_INTENTKEY))
//                            makeYoutubeApiCall(query, searchItemList);
                        else displayRv(searchItemList);
                    }
                });
    }

    /**
     * Use RxJava to make a bing image api call
     */
    private void makeBingImageApiCall(final String enteredQuery, final int offset) {
//        String input = dashSearchEditText.getText().toString();
        if (enteredQuery.equals("")) return;
        query = enteredQuery;
        if (offset == 0) hideKeyboard();
        BingAPISearchService.BingImageRx bingSearch = BingAPISearchService.createImageRx();
        Observable<BingImageResult> observable = bingSearch.getBingAPIResult(query, 25, offset, "en-us", "Moderate", Constants.BING_SUBSCRIPTION_KEY);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BingImageResult>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(BingImageResult bingResults) {
                        Log.d(TAG, "onNext: BING RESULTS RETURNED");
                        Log.d(TAG, "onNext: " + bingResults.toString());
                        Log.d(TAG, "onNext: " + bingResults.getWebSearchUrl());

//                        ArrayList<Object> items = new ArrayList<Object>();
                        if (offset == 0) searchItemList.clear();
                        for (BingImageResult.BingImageObj val : bingResults.getValue()) {
                            searchItemList.add(val);
                        }
                        if (offset > 0) updateSearchRv();
//                        else if (checkedMap.get(Constants.YOUTUBE_CHECK_INTENTKEY))
//                            makeYoutubeApiCall(query, searchItemList);
                        else displayRv(searchItemList);
                    }
                });
    }

    /**
     * Use RxJava to make a bing video api call
     */
    private void makeBingVideoApiCall(final String enteredQuery, final int offset) {
//        String input = dashSearchEditText.getText().toString();
        if (enteredQuery.equals("")) return;
        query = enteredQuery;
        if (offset == 0) hideKeyboard();
        BingAPISearchService.BingVideoRx bingSearch = BingAPISearchService.createVideoRx();
        Observable<BingVideoResult> observable = bingSearch.getBingAPIResult(query, 25, offset, "en-us", "Moderate", Constants.BING_SUBSCRIPTION_KEY);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BingVideoResult>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(BingVideoResult bingResults) {
                        Log.d(TAG, "onNext: BING RESULTS RETURNED");
                        Log.d(TAG, "onNext: " + bingResults.toString());
                        Log.d(TAG, "onNext: " + bingResults.getWebSearchUrl());

//                        ArrayList<Object> items = new ArrayList<Object>();
                        if (offset == 0) searchItemList.clear();
                        for (BingVideoResult.BingVideoObj val : bingResults.getValue()) {
                            searchItemList.add(val);
                        }
                        if (offset > 0) updateSearchRv();
                        else if (checkedMap.get(Constants.YOUTUBE_CHECK_INTENTKEY))
                            makeYoutubeApiCall(query, searchItemList);
                        else displayRv(searchItemList);
                    }
                });
    }

    // ----------------- FACEBOOK ------------------ //

    private void handleFacebookApi() {
        getFbFeed();
    }

    /**
     * Get users' feed and combine them with the list of social media objects
     */
    private void getFbFeed() {
        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/feed",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Gson gson = new Gson();
                        String fbFeedJson = response.getJSONObject().toString();
                        FacebookFeedObject fbFeed = gson.fromJson(fbFeedJson, FacebookFeedObject.class);
                        Log.d(TAG, "onCompleted: FeedJson " + fbFeedJson);
                        Log.d(TAG, "onCompleted: " + fbFeed.getData().toString());
                        if (!fbFeed.getData().toString().isEmpty()) {
                            String postId = fbFeed.getData().get(0).getId();
                            Log.d(TAG, "onCompleted: ID<><><><" + postId);
                            List<Object> fbFeedList = new ArrayList<Object>();
                            for (FacebookFeedObject.FbData data : fbFeed.getData())
                                fbFeedList.add(data);
                            newsFeedObjectLists.add(fbFeedList);
                            updateNewsFeed();
                        }
                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", FB_PERMISSIONS);
        request.setParameters(parameters);
        request.executeAsync();
    }

    // -------------- PINTERST --------------------//

    private void handlePinterestApi() {
        fetchPins();
    }

    /**
     * Get users' pins and combine them with the list of social media objects
     */
    private PDKCallback getPinCallback() {
        PDKCallback myPinsCallback = new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response) {
                loading = false;
                myPinsResponse = response;
                Log.d(TAG, "onSuccess: Pinterest Response " + response);
                List<Object> objectList = new ArrayList<>();
                for (PDKPin pin : response.getPinList()) objectList.add(pin);
                newsFeedObjectLists.add(objectList);
                updateNewsFeed();
//                socialMediaItemList.addAll((ArrayList)response.getPinList());
//                dashBoardRVAdapter.setItems(socialMediaItemList);
//                dashBoardRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(PDKException exception) {
                loading = false;
                Log.d(TAG, "onFailure: Pinterest " + exception.getDetailMessage());
            }
        };
        loading = true;
        return myPinsCallback;
    }

    private void fetchPins() {
        PDKClient.getInstance().getMyLikes(PIN_FIELDS, getPinCallback());
    }

    // -------------- INSTAGRAM --------------------//

    private void handleInstagramApi() {
        makeApiCallforUserInfo();
    }

    private void makeApiCallforUserRecent(String userId) {
        Call<InstagramResponseObj> response = ApiServiceManager.createInstagramApiService()
                .getInstagramUserRecent(userId, getInstaAuthFromIntent());
        response.enqueue(new Callback<InstagramResponseObj>() {
            @Override
            public void onResponse(Call<InstagramResponseObj> call, Response<InstagramResponseObj> response) {
                List<Object> instagramList = new ArrayList<Object>();
                for (InstagramResponseObj.InstagramData data : response.body().getInstagramData()) {
                    instagramList.add(data);
                }
                newsFeedObjectLists.add(instagramList);
                updateNewsFeed();
//                displayRv(instagramList);
            }

            @Override
            public void onFailure(Call<InstagramResponseObj> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void makeApiCallforUserInfo() {
        Call<InstagramUserResponse> call = ApiServiceManager.createInstagramApiService()
                .getInstagramUserInfo(getInstaAuthFromIntent());
        call.enqueue(new Callback<InstagramUserResponse>() {
            @Override
            public void onResponse(Call<InstagramUserResponse> call, Response<InstagramUserResponse> response) {
                makeApiCallforUserRecent(response.body().getData().getId());
            }

            @Override
            public void onFailure(Call<InstagramUserResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private String getInstaAuthFromIntent() {
        return getIntent().getStringExtra(Constants.INSTAGRAM_OAUTH_INTENTKEY);
    }

    private String getInstaCodeFromIntent() {
        return getIntent().getStringExtra(Constants.INSTAGRAM_CODE_INTENTKEY);
    }

    // -------------- LINKEDIN --------------------//

    private void handleLinkedInApi() {
        makeLinkedInApiRequest(getLinkedInApiHelper());
    }

    private APIHelper getLinkedInApiHelper() {
        return APIHelper.getInstance(getApplicationContext());
    }

    private void makeLinkedInApiRequest(APIHelper apiHelper) {
        apiHelper.getRequest(this, Constants.LINKEDIN_BASIC_URL, getLinkedInApiListener(Constants.LINKEDIN_BASIC_URL));
        apiHelper.getRequest(this, Constants.LINKEDIN_DETAIL_URL, getLinkedInApiListener(Constants.LINKEDIN_DETAIL_URL));
    }

    private ApiListener getLinkedInApiListener(final String url) {
        return new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                Log.i(TAG, "onApiSuccess: LinkedIn ");
                try {
                    if (url.equals(Constants.LINKEDIN_BASIC_URL))
                        setLinkedInData(apiResponse.getResponseDataAsJson());
                    if (url.equals(Constants.LINKEDIN_DETAIL_URL))
                        setLinkedInImage(apiResponse.getResponseDataAsJson());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onApiError(LIApiError LIApiError) {
                LIApiError.printStackTrace();
            }
        };
    }

    private void setLinkedInData(JSONObject json) {
        try {
            Log.i(TAG, "setLinkedInData: " + json.get("firstName").toString());
            Log.i(TAG, "setLinkedInData: " + json.get("headline").toString());
//            Object object = json.get("siteStandardProfileRequest");
            Log.i(TAG, "setLinkedInData: " + json.get("siteStandardProfileRequest").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLinkedInImage(JSONObject json) {
        try {
            Log.i(TAG, "setLinkedInData: " + json.get("pictureUrl").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------------- TWITTER ------------------ //

    private void handleTwitterApi() {
        StatusesService statusService = Twitter.getInstance().getApiClient().getStatusesService();
        statusService.homeTimeline(null, null, null, null, null, null, null).enqueue(new Callback<List<Tweet>>() {
            @Override
            public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
                final List<Object> tweetList = new ArrayList<>();
                for (Tweet tweet : response.body()) {
                    tweetList.add(tweet);
                }
                newsFeedObjectLists.add(tweetList);
                updateNewsFeed();
            }

            @Override
            public void onFailure(Call<List<Tweet>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * Get boolean values of selected filters and set the visibility of images
     */
    private void storeCheckedButtons() {
        checkedMap = new HashMap<>();
        checkedMap.put(Constants.YOUTUBE_CHECK_INTENTKEY, getIntent().getBooleanExtra(Constants.YOUTUBE_CHECK_INTENTKEY, false));
        checkedMap.put(Constants.PINTEREST_CHECK_INTENTKEY, getIntent().getBooleanExtra(Constants.PINTEREST_CHECK_INTENTKEY, false));
        checkedMap.put(Constants.FACEBOOK_CHECK_INTENTKEY, getIntent().getBooleanExtra(Constants.FACEBOOK_CHECK_INTENTKEY, false));
        checkedMap.put(Constants.LINKEDIN_CHECK_INTENTKEY, getIntent().getBooleanExtra(Constants.LINKEDIN_CHECK_INTENTKEY, false));
        checkedMap.put(Constants.INSTAGRAM_CHECK_INTENTKEY, getIntent().getBooleanExtra(Constants.INSTAGRAM_CHECK_INTENTKEY, false));
        checkedMap.put(Constants.TWITTER_CHECK_INTENTKEY, getIntent().getBooleanExtra(Constants.TWITTER_CHECK_INTENTKEY, false));

        if (checkedMap.get(Constants.YOUTUBE_CHECK_INTENTKEY))
            youtubeButton.setBackgroundResource(R.drawable.circle_youtube_color);
        else youtubeButton.setBackgroundResource(R.drawable.circle_youtube_grey);
        if (checkedMap.get(Constants.PINTEREST_CHECK_INTENTKEY))
            pinterestButton.setBackgroundResource(R.drawable.circle_pinterest_color);
        else pinterestButton.setBackgroundResource(R.drawable.circle_pinterest_grey);
        if (checkedMap.get(Constants.FACEBOOK_CHECK_INTENTKEY))
            facebookButton.setBackgroundResource(R.drawable.circle_facebook_color);
        else facebookButton.setBackgroundResource(R.drawable.circle_facebook_grey);
        if (checkedMap.get(Constants.LINKEDIN_CHECK_INTENTKEY))
            linkedinButton.setBackgroundResource(R.drawable.circle_linkedin_color);
        else linkedinButton.setBackgroundResource(R.drawable.circle_linkedin_grey);
        if (checkedMap.get(Constants.INSTAGRAM_CHECK_INTENTKEY))
            instagramButton.setBackgroundResource(R.drawable.circle_instagram_color);
        else instagramButton.setBackgroundResource(R.drawable.circle_instagram_grey);
        if (checkedMap.get(Constants.TWITTER_CHECK_INTENTKEY))
            twitterButton.setBackgroundResource(R.drawable.circle_twitter_color);
        else twitterButton.setBackgroundResource(R.drawable.circle_twitter_grey);
    }

    private void startShareIntent(String url) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Check out this search!");
        i.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(i, "Share via"));
    }

    private void showSearchPreview(String url) {
        isPreviewOpen = true;
        previewContainerFrame = (FrameLayout)findViewById(R.id.preview_container_frame);
        FrameLayout previewContainerCover = (FrameLayout)findViewById(R.id.preview_container_cover);
        previewContainerCover.setOnClickListener(this);
        previewContainerFrame.setVisibility(View.VISIBLE);
//        previewContainerCover.setOnTouchListener(this);
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        SearchPreviewFragment previewFragment = new SearchPreviewFragment();
        previewFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.preview_container, previewFragment);
        fragmentTransaction.commit();

    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

    }

    private boolean isKeyboardOpen() {
        final LinearLayout rootLayout = (LinearLayout) findViewById(R.id.activity_dash_board);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                rootLayout.getWindowVisibleDisplayFrame(rect);
                int screenHeight = rootLayout.getRootView().getHeight();
                int keypadHeight = screenHeight - rect.bottom;
                Log.i(TAG, "onGlobalLayout: keypad height " + keypadHeight);
                if (keypadHeight > screenHeight * 0.15) isKeyboardOpen = true;
                else isKeyboardOpen = false;
            }
        });
        return isKeyboardOpen;
    }

    /**
     * Gets the size of the user's screen, returns int of the screen width
     * @return
     */
    private int getScreenWidth(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private void logKeyHash() {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("getsterr.getsterr", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

    //--------------------Settings Dialog--------------------//

    private void initDialog(){
        settingDialog = new Dialog(this);
        settingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        settingDialog.setContentView(R.layout.dialog_setting);
        Switch settingsPreviewSwitch = (Switch) settingDialog.findViewById(R.id.settings_preview_switch);
        settingsPreviewSwitch.setOnClickListener(this);
        Button settingSaveButton = (Button)settingDialog.findViewById(R.id.settings_save_button);
        settingSaveButton.setOnClickListener(this);
        isSettingInit = true;
    }

    private void openSetting(){
        if (!isSettingInit) initDialog();
        settingDialog.show();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("DashBoard Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
