package getsterr.imf.activities.dashboard;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pinterest.android.pdk.PDKResponse;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import getsterr.imf.R;
import getsterr.imf.activities.login.LoginActivity;
import getsterr.imf.activities.main.MainActivity;
import getsterr.imf.activities.youtube.YoutubeDisplayActivity;
import getsterr.imf.fragments.SearchPreviewFragment;
import getsterr.imf.models.bing.BingImageResult;
import getsterr.imf.models.bing.BingResult;
import getsterr.imf.models.bing.BingVideoResult;
import getsterr.imf.models.bing.Value;
import getsterr.imf.models.giphy.GiphyObject;
import getsterr.imf.models.youtube.YoutubeObject;
import getsterr.imf.providers.BingAPISearchService;
import getsterr.imf.utilities.ApiServiceManager;
import getsterr.imf.utilities.Constants;
import getsterr.imf.utilities.WebViewActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DashBoardNewActivity extends AppCompatActivity implements View.OnClickListener, DashBoardRVAdapter.CardClickListener,
        DashBoardRVAdapter.YoutubeCardClickListener, DashBoardRVAdapter.CardLongClickListener, DashBoardRVAdapter.LastResultShownListener,
        SearchPreviewFragment.OnPreviewClickListener, View.OnTouchListener {

    private static final String TAG = DashBoardActivity.class.getSimpleName();

    private GoogleApiClient client;
    private FrameLayout previewContainer, parentLayout, previewContainerFrame;
    private LinearLayout searchOptionBar, menuHamburgerLayout;
    private SwipeRefreshLayout swipeRefreshContainer;
    private RelativeLayout giphyPowerByLayout;
    private ImageButton snapchatButton, twitterButton, pinterestButton, linkedinButton, youtubeButton, instagramButton, facebookButton;
    private ImageButton gmailButton, outlookButton, yahooButton, aolButton, zohoButton;
    private EditText dashSearchEditText;
    private TextView webSearchButton, imageSearchButton, videoSearchButton, memeSearchButton;
    private float startX, dx;
    private DashBoardRVAdapter dashBoardRVAdapter;
    private RecyclerView dashBoardRecyclerView;
    private List<List<Object>> newsFeedObjectLists = new ArrayList<>();
    private List<Object> searchItemList = new ArrayList<>();
    private Dialog settingDialog;
    private String currentQuery, selectedUrl, query;
    private boolean isKeyboardOpen;
    private boolean isSearchMode = false;
    private boolean isSettingInit = false;
    private boolean isPreviewOpen = false;
    private boolean isPreviewEnabled = true; //save to preferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_new);
        logKeyHash();
        initViews();
        initActionBar();
        setSocialMediaButtonListeners();
//        displayRv(socialMediaItemList);
        handleSearchOptions();
        // Setup search bar
//        storeCheckedButtons();
        setSearchEditTextListener();

        // Get items from social media
//        displayNewsFeed();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        swipeRefreshContainer.setEnabled(false);
        swipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newsFeedObjectLists.clear();
//                displayNewsFeed();
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
                startShareAppIntent();
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
                openWebActivity(selectedUrl,"Search");
                break;
            case R.id.search_option_web:
                turnSearchOptionOn(webSearchButton);
                turnSearchOptionOff(imageSearchButton);
                turnSearchOptionOff(videoSearchButton);
                turnSearchOptionOff(memeSearchButton);
                searchItemList.clear();
                if (currentQuery!= null) makeBingApiCall(currentQuery, 0);
                break;
            case R.id.search_option_image:
                parentLayout.requestFocus();
                turnSearchOptionOn(imageSearchButton);
                turnSearchOptionOff(webSearchButton);
                turnSearchOptionOff(videoSearchButton);
                turnSearchOptionOff(memeSearchButton);
                if (currentQuery!=null)makeBingImageApiCall(currentQuery, 0);
                break;
            case R.id.search_option_video:
                turnSearchOptionOn(videoSearchButton);
                turnSearchOptionOff(imageSearchButton);
                turnSearchOptionOff(webSearchButton);
                turnSearchOptionOff(memeSearchButton);
                if (currentQuery!=null)makeBingVideoApiCall(currentQuery, 0);
                break;
            case R.id.search_option_gif:
                turnSearchOptionOn(memeSearchButton);
                turnSearchOptionOff(imageSearchButton);
                turnSearchOptionOff(webSearchButton);
                turnSearchOptionOff(videoSearchButton);
                if (currentQuery!=null)makeGiphyApiCall(currentQuery, 0);
                break;
            case R.id.settings_preview_switch:
                isPreviewEnabled = !isPreviewEnabled;
                break;
            case R.id.settings_save_button:
                settingDialog.cancel();
                break;
            case R.id.dash_facebook_button:
                openWebActivity(Constants.FACEBOOK_HOME_URL,"Facebook");
                break;
            case R.id.dash_twitter_button:
                openWebActivity(Constants.TWITTER_HOME_URL, "Twitter");
                break;
            case R.id.dash_instagram_button:
                openWebActivity(Constants.INSTAGRAM_HOME_URL, "Instagram");
                break;
            case R.id.dash_youtube_button:
                openWebActivity(Constants.YOUTUBE_HOME_URL, "Youtube");
                break;
            case R.id.dash_linkedin_button:
                openWebActivity(Constants.LINKEDIN_HOME_URL, "LinkedIn");
                break;
            case R.id.dash_outlook_button:
                openWebActivity(Constants.OUTLOOK_HOME_URL, "Outlook");
                break;
            case R.id.dash_gmail_button:
                openWebActivity(Constants.GMAIL_HOME_URL, "Gmail");
                break;
            case R.id.dash_yahoo_button:
                openWebActivity(Constants.YAHOO_HOME_URL, "Yahoo Mail");
                break;
            case R.id.dash_aol_button:
                openWebActivity(Constants.AOL_HOME_URL, "Aol Mail");
                break;
            case R.id.dash_zoho_button:
                openWebActivity(Constants.ZOHO_HOME_URL, "ZOHO Mail");
                break;
//            case R.id.dash_pinterest_button:
//                openWebActivity(Constants.PINTEREST_HOME_URL, "Pinterest");
//                break;
//            case R.id.dash_snapchat_button:
//                openWebActivity(Constants.SNAPCHAT_HOME_URL, "Snapchat");
//                break;
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
            openWebActivity(cardUrl,"Search");
        }
    }

    @Override
    public void onPreviewClicked(String url) {
        previewContainer.setVisibility(View.GONE);
        openWebActivity(url,"Search");
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
            case 'm':
                makeGiphyApiCall(query,offset);
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
        else if (isSearchMode){
            newsFeedObjectLists.clear();
            searchItemList.clear();
            if(dashBoardRVAdapter!=null) dashBoardRVAdapter.notifyDataSetChanged();
//            displayNewsFeed();
//            swipeRefreshContainer.setEnabled(true);
            searchOptionBar.setVisibility(View.GONE);
            isSearchMode = false;
//            super.onBackPressed();
        }
    }

    private void initViews() {
        parentLayout = (FrameLayout) findViewById(R.id.activity_dash_board);
        dashBoardRecyclerView = (RecyclerView) findViewById(R.id.dashbard_recyclerView);
        previewContainer = (FrameLayout) findViewById(R.id.preview_container);
        facebookButton = (ImageButton) findViewById(R.id.dash_facebook_button);
        twitterButton = (ImageButton) findViewById(R.id.dash_twitter_button);
//        snapchatButton = (ImageButton) findViewById(R.id.dash_snapchat_button);
//        pinterestButton = (ImageButton) findViewById(R.id.dash_pinterest_button);
        linkedinButton = (ImageButton) findViewById(R.id.dash_linkedin_button);
        youtubeButton = (ImageButton) findViewById(R.id.dash_youtube_button);
        instagramButton = (ImageButton) findViewById(R.id.dash_instagram_button);
        outlookButton = (ImageButton) findViewById(R.id.dash_outlook_button);
        gmailButton = (ImageButton) findViewById(R.id.dash_gmail_button);
        yahooButton = (ImageButton) findViewById(R.id.dash_yahoo_button);
        aolButton = (ImageButton) findViewById(R.id.dash_aol_button);
        zohoButton = (ImageButton) findViewById(R.id.dash_zoho_button);
        dashSearchEditText = (EditText) findViewById(R.id.dash_search_editText);
        webSearchButton = (TextView)findViewById(R.id.search_option_web);
        imageSearchButton = (TextView)findViewById(R.id.search_option_image);
        videoSearchButton = (TextView)findViewById(R.id.search_option_video);
        memeSearchButton = (TextView)findViewById(R.id.search_option_gif);
        searchOptionBar = (LinearLayout) findViewById(R.id.search_options_bar);
        swipeRefreshContainer = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_container);
        giphyPowerByLayout = (RelativeLayout)findViewById(R.id.giphy_powerby_layout);
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
        ImageView shareButton = (ImageView) actionBarView.findViewById(R.id.menu_share_iv);
        shareButton.setVisibility(View.GONE);
//        TextView menuLoginTv = (TextView)findViewById(R.id.menu_login_tv);
//        TextView menuSetupTv = (TextView)findViewById(R.id.menu_setup_tv);
        TextView menuSettingsTv = (TextView)findViewById(R.id.menu_settings_tv);
//        menuLoginTv.setOnClickListener(this);
//        menuSetupTv.setOnClickListener(this);
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
//        snapchatButton.setOnClickListener(this);
//        pinterestButton.setOnClickListener(this);
        youtubeButton.setOnClickListener(this);
        linkedinButton.setOnClickListener(this);
        outlookButton.setOnClickListener(this);
        gmailButton.setOnClickListener(this);
        yahooButton.setOnClickListener(this);
        aolButton.setOnClickListener(this);
        zohoButton.setOnClickListener(this);
    }

    private void displayRv(List<Object> list) {
        dashBoardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dashBoardRVAdapter = new DashBoardRVAdapter(list, DashBoardNewActivity.this, this, this, this);
        dashBoardRecyclerView.setAdapter(dashBoardRVAdapter);
    }

    private void displayGridRv(List<Object> list) {
        dashBoardRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        dashBoardRVAdapter = new DashBoardRVAdapter(list, DashBoardNewActivity.this, this, this, this);
        dashBoardRecyclerView.setAdapter(dashBoardRVAdapter);
    }

    private void updateSearchRv() {
        dashBoardRVAdapter.notifyItemRangeInserted(searchItemList.size() - 25, 25);
    }

    private void combineSearchLists(List<Object> youtubeList, List<Object> bingList) {
        List<Object> searchList = new ArrayList<>();
        int minSize;
        if (youtubeList.size() <= bingList.size()) minSize = youtubeList.size();
        else minSize = bingList.size();
        if (youtubeList.size()<=0&&bingList.size()<=0) {
            Toast.makeText(this,"No Results Found", Toast.LENGTH_SHORT).show();
            return;
        }
        if (youtubeList.size()<=0) displayRv(bingList);
        else {
            for (int index = 0; index < minSize; index++) {
                searchList.add( bingList.get(index));
                searchList.add( youtubeList.get(index));
            }
            displayRv(searchList);
        }
    }

    private void handleSearchOptions(){
        webSearchButton.setOnClickListener(this);
        videoSearchButton.setOnClickListener(this);
        imageSearchButton.setOnClickListener(this);
        memeSearchButton.setOnClickListener(this);
        webSearchButton.setPaintFlags(webSearchButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    /**
     * Perform Bing API Search when search icon is clicked
     */
    private void setSearchEditTextListener() {
//        dashSearchEditText.setCursorVisible(true);
        dashSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO) {
                    dashSearchEditText.clearFocus();
                    Log.i(TAG, "onKey: enter clicked");
                    webSearchButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                    webSearchButton.setTypeface(null,Typeface.BOLD);
                    webSearchButton.setPaintFlags(webSearchButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    imageSearchButton.setTextColor(getResources().getColor(R.color.lightgray));
                    imageSearchButton.setTypeface(Typeface.DEFAULT);
                    imageSearchButton.setPaintFlags(imageSearchButton.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                    videoSearchButton.setTextColor(getResources().getColor(R.color.lightgray));
                    videoSearchButton.setTypeface(Typeface.DEFAULT);
                    videoSearchButton.setPaintFlags(videoSearchButton.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                    memeSearchButton.setTextColor(getResources().getColor(R.color.lightgray));
                    memeSearchButton.setTypeface(Typeface.DEFAULT);
                    memeSearchButton.setPaintFlags(memeSearchButton.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                    searchItemList.clear();
                    currentQuery = dashSearchEditText.getText().toString();
                    makeBingApiCall(dashSearchEditText.getText().toString(), 0);
                    searchOptionBar.setVisibility(View.VISIBLE);
                    swipeRefreshContainer.setEnabled(false);
                    isSearchMode = true;
                    dashSearchEditText.getText().clear();
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
//                YoutubeObject youtubeObject = response.body();
//                Log.i(TAG, "onResponse: " + youtubeObject.getItems()[0].getId().getVideoId());
                List<Object> youtubeList = new ArrayList<Object>();
                for (YoutubeObject.Resource resource : response.body().getItems())
                    youtubeList.add(resource);
//                displayRv(dataList);
                combineSearchLists(youtubeList, bingList);
                Log.i(TAG, "onResponse: in youtube API");
            }

            @Override
            public void onFailure(Call<YoutubeObject> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    /**
     * Use RxJava to make a bing api call
     */
    private void makeBingApiCall(final String enteredQuery, final int offset) {
//        String input = dashSearchEditText.getText().toString();
        if (enteredQuery.equals("")) return;
        query = enteredQuery;
//        if (offset == 0) hideKeyboard();
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
                        makeBingApiCall(enteredQuery,offset);
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
                        if (searchItemList.size()<=0){
                            Toast.makeText(DashBoardNewActivity.this,"No Results Found", Toast.LENGTH_SHORT).show();
                            return;
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
//        if (offset == 0) hideKeyboard();
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
                        if (searchItemList.size()<=0){
                            Toast.makeText(DashBoardNewActivity.this,"No Results Found", Toast.LENGTH_SHORT).show();
                            return;
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
        Log.i(TAG, "makeBingVideoApiCall: ");
        query = enteredQuery;
//        if (offset == 0) hideKeyboard();
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
                        Log.d(TAG, "onNext: BING VIDEO RESULTS RETURNED");
                        Log.d(TAG, "onNext: " + bingResults.toString());
                        Log.d(TAG, "onNext: " + bingResults.getWebSearchUrl());

                        if (offset == 0) searchItemList.clear();
                        for (BingVideoResult.BingVideoObj val : bingResults.getValue()) {
                            searchItemList.add(val);
                        }
                        if (offset > 0) updateSearchRv();
                        else makeYoutubeApiCall(query, searchItemList);
                    }
                });
    }

    private void makeGiphyApiCall(String enteredQuery, final int offset){
        Call<GiphyObject> call = ApiServiceManager.createGiphyApiService().getGiphySearch(enteredQuery);
        call.enqueue(new Callback<GiphyObject>() {
            @Override
            public void onResponse(Call<GiphyObject> call, Response<GiphyObject> response) {
                if (response.body().getData()[0] != null) Log.i(TAG, "GIPHY onResponse: id: "+ response.body().getData()[0].getId());
                else Log.i(TAG, "onResponse: list is null ");
                ArrayList<Object> items = new ArrayList<Object>();
                for (GiphyObject.Giphy giphy : response.body().getData()){
                    items.add(giphy);
                }
                if (offset > 0) updateSearchRv();
                else displayGridRv(items);
            }

            @Override
            public void onFailure(Call<GiphyObject> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void openWebActivity(String url, String title){
        Intent webIntent = new Intent(DashBoardNewActivity.this, WebViewActivity.class);
        webIntent.putExtra(Constants.URL_INTENTKEY, url);
        webIntent.putExtra(Constants.TITLE_INTENTKEY, title);
        startActivity(webIntent);
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

    private void startShareAppIntent() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Hey! Get Getsterr!");
        i.putExtra(Intent.EXTRA_TEXT, "Hey! Get Getsterr! \nhttps://play.google.com/store/apps/details?id=getsterr.imf&ah=02E7Fv-xKVeD-_tcwnyRMeEnxfc");
        startActivity(Intent.createChooser(i, "Share via"));
    }

    private void turnSearchOptionOn(TextView tv){
        tv.setTextColor(getResources().getColor(R.color.colorPrimary));
        tv.setTypeface(null,Typeface.BOLD);
        tv.setPaintFlags(memeSearchButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        if (tv == memeSearchButton) giphyPowerByLayout.setVisibility(View.VISIBLE);
        else giphyPowerByLayout.setVisibility(View.GONE);
    }

    private void turnSearchOptionOff(TextView tv){
        tv.setTextColor(getResources().getColor(R.color.lightgray));
        tv.setTypeface(Typeface.DEFAULT);
        tv.setPaintFlags(imageSearchButton.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
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
