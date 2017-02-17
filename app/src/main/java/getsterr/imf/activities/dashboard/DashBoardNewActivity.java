package getsterr.imf.activities.dashboard;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.SwitchPreference;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
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
import java.util.ArrayList;
import java.util.List;
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
        SearchPreviewFragment.OnPreviewClickListener, TextView.OnEditorActionListener {

    private static final String TAG = DashBoardActivity.class.getSimpleName();

    private GoogleApiClient client;
    private FrameLayout previewContainer, parentLayout, previewContainerFrame;
    private LinearLayout searchOptionBar, menuHamburgerLayout;
    private RelativeLayout giphyPowerByLayout;
    private ImageButton snapchatButton, twitterButton, pinterestButton, linkedinButton, youtubeButton, instagramButton, facebookButton;
    private ImageButton gmailButton, outlookButton, yahooButton, aolButton, zohoButton;
    private EditText dashSearchEditText;
    private TextView webSearchButton, imageSearchButton, videoSearchButton, memeSearchButton;
    private RecyclerView dashBoardRecyclerView;
    private DashBoardRVAdapter dashBoardRVAdapter;
    private List<List<Object>> newsFeedObjectLists = new ArrayList<>();
    private List<Object> searchItemList = new ArrayList<>();
    private Dialog settingDialog;
    private String currentQuery, selectedUrl, query;
    private boolean isSearchMode, isSettingInit, isPreviewOpen, isPreviewEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_new);
        initXML();
        setListeners();
        initGoogleApiClient();
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
                handleSearchOptionXml(webSearchButton);
                searchItemList.clear();
                if (currentQuery!= null) makeBingApiCall(currentQuery, 0);
                break;
            case R.id.search_option_image:
                parentLayout.requestFocus();
                handleSearchOptionXml(imageSearchButton);
                if (currentQuery!=null)makeBingImageApiCall(currentQuery, 0);
                break;
            case R.id.search_option_video:
                handleSearchOptionXml(videoSearchButton);
                if (currentQuery!=null)makeBingVideoApiCall(currentQuery, 0);
                break;
            case R.id.search_option_gif:
                handleSearchOptionXml(memeSearchButton);
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
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_GO) {
            dashSearchEditText.clearFocus();
            handleSearchOptionXml(webSearchButton);
            searchItemList.clear();
            currentQuery = dashSearchEditText.getText().toString();
            makeBingApiCall(currentQuery, 0);
            searchOptionBar.setVisibility(View.VISIBLE);
            isSearchMode = true;
            dashSearchEditText.getText().clear();
            return true;
        }
        return false;
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

    // ----------------- Manage List & RV ------------------ //

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


    // ----------------- Listeners ------------------ //

    private void setListeners(){
        setSearchEditTextListener();
        setSocialMediaButtonListeners();
        handleSearchOptions();
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

    /**
     * Perform Bing API Search when search icon is clicked
     */
    private void setSearchEditTextListener() {
        dashSearchEditText.setOnEditorActionListener(this);
    }

    private void handleSearchOptions(){
        webSearchButton.setOnClickListener(this);
        videoSearchButton.setOnClickListener(this);
        imageSearchButton.setOnClickListener(this);
        memeSearchButton.setOnClickListener(this);
        webSearchButton.setPaintFlags(webSearchButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    // ----------------- API Calls ------------------ //

    private void makeYoutubeApiCall(String query, final List<Object> bingList) {
        Call<YoutubeObject> call = ApiServiceManager.createYoutubeApiService().getYoutubeSearch(query);
        call.enqueue(new Callback<YoutubeObject>() {
            @Override
            public void onResponse(Call<YoutubeObject> call, Response<YoutubeObject> response) {
                List<Object> youtubeList = new ArrayList<Object>();
                for (YoutubeObject.Resource resource : response.body().getItems()) youtubeList.add(resource);
                combineSearchLists(youtubeList, bingList);
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
        if (enteredQuery.equals("")) return;
        query = enteredQuery;
        BingAPISearchService.BingSearchRx bingSearch = BingAPISearchService.createRx(Constants.BING_API_SEARCH_URL);
        Observable<BingResult> observable = bingSearch.getBingAPIResult(query, 25, offset, "en-us", "Moderate", Constants.BING_SUBSCRIPTION_KEY);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BingResult>() {
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        makeBingApiCall(enteredQuery,offset);
                    }

                    @Override
                    public void onNext(BingResult bingResults) {
                        for (Value val : bingResults.getWebPages().getValue()) {
                            searchItemList.add(val);
                        }
                        if (searchItemList.size()<=0){
                            Toast.makeText(DashBoardNewActivity.this,"No Results Found", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (offset > 0) updateSearchRv();
                        else displayRv(searchItemList);
                    }
                });
    }

    /**
     * Use RxJava to make a bing image api call
     */
    private void makeBingImageApiCall(final String enteredQuery, final int offset) {
        if (enteredQuery.equals("")) return;
        query = enteredQuery;
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
                        if (offset == 0) searchItemList.clear();
                        for (BingImageResult.BingImageObj val : bingResults.getValue()) {
                            searchItemList.add(val);
                        }
                        if (searchItemList.size()<=0){
                            Toast.makeText(DashBoardNewActivity.this,"No Results Found", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (offset > 0) updateSearchRv();
                        else displayRv(searchItemList);
                    }
                });
    }

    /**
     * Use RxJava to make a bing video api call
     */
    private void makeBingVideoApiCall(final String enteredQuery, final int offset) {
        if (enteredQuery.equals("")) return;
        query = enteredQuery;
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
        Call<GiphyObject> call = ApiServiceManager.createGiphyApiService().getGiphySearch(enteredQuery,Constants.GIPHY_API_KEY);
        call.enqueue(new Callback<GiphyObject>() {
            @Override
            public void onResponse(Call<GiphyObject> call, Response<GiphyObject> response) {
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

    // ----------------- Search Display ------------------ //

    private void openWebActivity(String url, String title){
        Intent webIntent = new Intent(DashBoardNewActivity.this, WebViewActivity.class);
        webIntent.putExtra(Constants.URL_INTENTKEY, url);
        webIntent.putExtra(Constants.TITLE_INTENTKEY, title);
        startActivity(webIntent);
    }

    private void showSearchPreview(String url) {
        isPreviewOpen = true;
        FrameLayout previewContainerCover = (FrameLayout)findViewById(R.id.preview_container_cover);
        previewContainerCover.setOnClickListener(this);
        previewContainerFrame.setVisibility(View.VISIBLE);
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        SearchPreviewFragment previewFragment = new SearchPreviewFragment();
        previewFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.preview_container, previewFragment);
        fragmentTransaction.commit();

    }

    //--------------------Sharing--------------------//

    private void startShareAppIntent() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Hey! Get Getsterr!");
        i.putExtra(Intent.EXTRA_TEXT, "Hey! Get Getsterr! \nhttps://play.google.com/store/apps/details?id=getsterr.imf&ah=02E7Fv-xKVeD-_tcwnyRMeEnxfc");
        startActivity(Intent.createChooser(i, "Share via"));
    }

    private void startShareIntent(String url) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Check out this search!");
        i.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(i, "Share via"));
    }

    //--------------------Search Options--------------------//

    private void handleSearchOptionXml(TextView tv){
        TextView[] searchOptionTvArray = {webSearchButton,imageSearchButton,videoSearchButton,memeSearchButton};
        turnSearchOptionOn(tv);
        for (TextView textView : searchOptionTvArray) {
            if (textView!=tv) turnSearchOptionOff(textView);
        }
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

    //--------------------XML Utilities--------------------//

    private void initXML(){
        initViews();
        initActionBar();
        lockSwipeRefresh();
    }

    private void initViews() {
        parentLayout = (FrameLayout) findViewById(R.id.activity_dash_board);
        dashBoardRecyclerView = (RecyclerView) findViewById(R.id.dashbard_recyclerView);
        previewContainer = (FrameLayout) findViewById(R.id.preview_container);
        facebookButton = (ImageButton) findViewById(R.id.dash_facebook_button);
        twitterButton = (ImageButton) findViewById(R.id.dash_twitter_button);
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
        previewContainerFrame = (FrameLayout)findViewById(R.id.preview_container_frame);
        giphyPowerByLayout = (RelativeLayout)findViewById(R.id.giphy_powerby_layout);
//        snapchatButton = (ImageButton) findViewById(R.id.dash_snapchat_button);
//        pinterestButton = (ImageButton) findViewById(R.id.dash_pinterest_button);
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
        TextView menuSettingsTv = (TextView)findViewById(R.id.menu_settings_tv);
        menuSettingsTv.setOnClickListener(this);
        toolbarTitleTv.setText("Dashboard");
        mainActionBar.setCustomView(actionBarView);
        mainActionBar.setDisplayShowCustomEnabled(true);
        ImageView loginButton = (ImageView) actionBarView.findViewById(R.id.menu_hamburger_iv);
        loginButton.setOnClickListener(this);
        ImageView iconButton = (ImageView) actionBarView.findViewById(R.id.menu_logo_iv);
        iconButton.setOnClickListener(this);
    }

    private void lockSwipeRefresh(){
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_container);
        swipeRefreshLayout.setEnabled(false);
    }

    //--------------------Google Client--------------------//

    private void initGoogleApiClient(){
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
