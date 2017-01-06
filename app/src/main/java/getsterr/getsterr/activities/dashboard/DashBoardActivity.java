package getsterr.getsterr.activities.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Rect;
import android.icu.util.BuddhistCalendar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
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
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.CollectionTimeline;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TwitterListTimeline;
import com.twitter.sdk.android.tweetui.UserTimeline;

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

public class DashBoardActivity extends AppCompatActivity implements View.OnClickListener, DashBoardRVAdapter.CardClickListener, DashBoardRVAdapter.YoutubeCardClickListener, DashBoardRVAdapter.CardLongClickListener, DashBoardRVAdapter.LastResultShownListener{

    private static final String TAG = DashBoardActivity.class.getSimpleName();

    RecyclerView dashBoardRecyclerView;

    ImageButton  twitterButton, pinterestButton, linkedinButton, youtubeButton, instagramButton;
    FrameLayout facebookButton;
    Toolbar dashBoardToolbar;
    ActionBar dashBoardActionBar;
    Map<String,Boolean> checkedMap;
    String query;
    EditText dashSearchEditText;
    boolean isKeyboardOpen;
    DashBoardRVAdapter dashBoardRVAdapter;
    List<List<Object>> newsFeedObjectLists = new ArrayList<>();
    ArrayList<Object> socialMediaItemList = new ArrayList<>();
    List<Object> searchItemList = new ArrayList<>();

    private PDKResponse myPinsResponse;
    private boolean loading = false;
    private static final String PIN_FIELDS = "id,link,creator,image,counts,note,created_at,board,metadata";
    private static final String FB_PERMISSIONS = "id,name,link,icon,message,created_time,description,picture,story,permalink_url,from, full_picture";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        logKeyHash();
        initViews();
        initActionBar();
        displayRv(socialMediaItemList);

        // Setup search bar
        storeCheckedButtons();
        setSearchEditTextListener();

        // Get items from social media
        displayNewsFeed();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.menu_hamburger_iv:
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                break;
            case R.id.menu_logo_iv:
                newsFeedObjectLists.clear();
                displayNewsFeed();
                break;
        }
    }

    /**
     * When a card is clicked use url passed from DashBoardRVAdapter to open url in WebView Activity
     * @param cardUrl
     */
    @Override
    public void onCardClick(String cardUrl) {

        Intent intent = new Intent(DashBoardActivity.this, WebViewActivity.class);
        intent.putExtra(Constants.URL_INTENTKEY, cardUrl);
        startActivity(intent);
    }

    @Override
    public void onCardLongClick(String url) {
        startShareIntent(url);
    }

    @Override
    public void onYoutubeCardClick(String videoId) {
        Intent youtubeIntent = new Intent(this, YoutubeDisplayActivity.class);
        youtubeIntent.putExtra(YoutubeDisplayActivity.YOUTUBE_DISPLAY_KEY,videoId);
        startActivity(youtubeIntent);
    }

    @Override
    public void onLastResultShown(int offset, char searchType) {
        switch (searchType){
            case 'w':
                makeBingApiCall(query,offset);
                break;
            case 'i':
                makeBingImageApiCall(query,offset);
                break;
            case 'v':
                makeBingVideoApiCall(query,offset);
                break;
        }
    }

    private void initViews(){
        dashBoardRecyclerView = (RecyclerView)findViewById(R.id.dashbard_recyclerView);
        dashBoardToolbar = (Toolbar) findViewById(R.id.dashboard_toolbar);
        facebookButton = (FrameLayout)findViewById(R.id.dash_facebook_button);
        twitterButton = (ImageButton)findViewById(R.id.dash_twitter_button);
        pinterestButton = (ImageButton)findViewById(R.id.dash_pinterest_button);
        linkedinButton = (ImageButton)findViewById(R.id.dash_linkedin_button);
        youtubeButton = (ImageButton)findViewById(R.id.dash_youtube_button);
        instagramButton = (ImageButton)findViewById(R.id.dash_instagram_button);
        dashSearchEditText = (EditText)findViewById(R.id.dash_search_editText);
    }

    private void initActionBar(){
        Toolbar mainToolbar = (Toolbar)findViewById(R.id.dashboard_toolbar);
        setSupportActionBar(mainToolbar);
        ActionBar mainActionBar = getSupportActionBar();
        mainActionBar.setDisplayShowHomeEnabled(false);
        mainActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = LayoutInflater.from(this);
        View actionBarView = inflater.inflate(R.layout.actionbar_layout,null);
        TextView toolbarTitleTv = (TextView)actionBarView.findViewById(R.id.menu_title_tv);
        toolbarTitleTv.setText("Dashboard");
        mainActionBar.setCustomView(actionBarView);
        mainActionBar.setDisplayShowCustomEnabled(true);
        ImageView loginButton = (ImageView)actionBarView.findViewById(R.id.menu_hamburger_iv);
        loginButton.setOnClickListener(this);
        ImageView iconButton = (ImageView)actionBarView.findViewById(R.id.menu_logo_iv);
        iconButton.setOnClickListener(this);
    }

    private void displayRv(List<Object> list){
        dashBoardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dashBoardRVAdapter = new DashBoardRVAdapter(list, DashBoardActivity.this, this, this, this);
        dashBoardRecyclerView.setAdapter(dashBoardRVAdapter);
    }

    private void updateSearchRv(){
        dashBoardRVAdapter.notifyItemRangeInserted(searchItemList.size()-25,25);
    }

    private void displayNewsFeed(){
        makeCheckedApiCalls();
        displayRv(combineNewsFeedLists());
    }

    private void updateNewsFeed(){
        displayRv(combineNewsFeedLists());
    }

    private void makeCheckedApiCalls(){
        if (checkedMap.get(Constants.INSTAGRAM_CHECK_INTENTKEY)) handleInstagramApi();
        if (checkedMap.get(Constants.FACEBOOK_CHECK_INTENTKEY)) handleFacebookApi();
        if (checkedMap.get(Constants.LINKEDIN_CHECK_INTENTKEY)) handleLinkedInApi();
        if (checkedMap.get(Constants.PINTEREST_CHECK_INTENTKEY)) handlePinterestApi();
        if (checkedMap.get(Constants.TWITTER_CHECK_INTENTKEY)) handleTwitterApi();
    }

    private List<Object> combineNewsFeedLists(){
        List<Object> combinedList = new ArrayList<>();
        int min = 100; //max num
        for (List<Object> list : newsFeedObjectLists) if (list.size()<min) min=list.size();
        for (int index = 0; index < min; index++){
            for (List<Object> list : newsFeedObjectLists) combinedList.add(list.get(index));
        }
        return combinedList;
    }

    private void combineSearchLists(List<Object> youtubeList, List<Object> bingList){
        List<Object> searchList = new ArrayList<>();
        int minSize;
        if (youtubeList.size()<=bingList.size()) minSize = youtubeList.size();
        else minSize = bingList.size();
        for (int index = 0; index < minSize; index++){
            searchList.add((Value)bingList.get(index));
            searchList.add((YoutubeObject.Resource)youtubeList.get(index));
        }
        displayRv(searchList);
    }

    /**
     * Perform Bing API Search when search icon is clicked
     *
     */
    private void setSearchEditTextListener(){
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
                if (i == EditorInfo.IME_ACTION_GO){
                    Log.i(TAG, "onKey: enter clicked");
                    makeBingVideoApiCall(dashSearchEditText.getText().toString(),0); //TODO: CHANGE BACK TO makeBingSearchCall ONCE UI IS IMPLEMENTED
//                    makeYoutubeApiCall(dashSearchEditText.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    // ----------------- YOUTUBE ------------------ //

    private void makeYoutubeApiCall(String query, final List<Object> bingList){
        Call<YoutubeObject> call = ApiServiceManager.createYoutubeApiService().getYoutubeSearch(query);
        call.enqueue(new Callback<YoutubeObject>() {
            @Override
            public void onResponse(Call<YoutubeObject> call, Response<YoutubeObject> response) {
                YoutubeObject youtubeObject = response.body();
                Log.i(TAG, "onResponse: "+youtubeObject.getItems()[0].getId().getVideoId());
                List<Object> youtubeList = new ArrayList<Object>();
                for (YoutubeObject.Resource resource : response.body().getItems())youtubeList.add(resource);
//                displayRv(dataList);
                combineSearchLists(youtubeList,bingList);
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
    private ArrayList<Object> getSampleArrayList(){
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
    private void makeBingApiCall(final String enteredQuery, final int offset){
//        String input = dashSearchEditText.getText().toString();
        if (enteredQuery.equals("")) return;
        query = enteredQuery;
        if (offset==0) hideKeyboard();
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

                        for(Value val: bingResults.getWebPages().getValue()){
                            searchItemList.add(val);
                        }
                        if (offset>0) updateSearchRv();
                        else if (checkedMap.get(Constants.YOUTUBE_CHECK_INTENTKEY)) makeYoutubeApiCall(query,searchItemList);
                        else displayRv(searchItemList);
                    }
                });
    }

    /**
     * Use RxJava to make a bing image api call
     */
    private void makeBingImageApiCall(final String enteredQuery, final int offset){
//        String input = dashSearchEditText.getText().toString();
        if (enteredQuery.equals("")) return;
        query = enteredQuery;
        if (offset==0) hideKeyboard();
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
                        if (offset==0) searchItemList.clear();
                        for(BingImageResult.BingImageObj val: bingResults.getValue()){
                            searchItemList.add(val);
                        }
                        if (offset>0) updateSearchRv();
                        else if (checkedMap.get(Constants.YOUTUBE_CHECK_INTENTKEY)) makeYoutubeApiCall(query,searchItemList);
                        else displayRv(searchItemList);
                    }
                });
    }

    /**
     * Use RxJava to make a bing video api call
     */
    private void makeBingVideoApiCall(final String enteredQuery, final int offset){
//        String input = dashSearchEditText.getText().toString();
        if (enteredQuery.equals("")) return;
        query = enteredQuery;
        if (offset==0) hideKeyboard();
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
                        if (offset==0) searchItemList.clear();
                        for(BingVideoResult.BingVideoObj val: bingResults.getValue()){
                            searchItemList.add(val);
                        }
                        if (offset>0) updateSearchRv();
                        else if (checkedMap.get(Constants.YOUTUBE_CHECK_INTENTKEY)) makeYoutubeApiCall(query,searchItemList);
                        else displayRv(searchItemList);
                    }
                });
    }

    // ----------------- FACEBOOK ------------------ //

    private void handleFacebookApi(){
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
                        if(!fbFeed.getData().toString().isEmpty()){
                            String postId = fbFeed.getData().get(0).getId();
                            Log.d(TAG, "onCompleted: ID<><><><" + postId);
                            List<Object> fbFeedList = new ArrayList<Object>();
                            for (FacebookFeedObject.FbData data : fbFeed.getData()) fbFeedList.add(data);
                            newsFeedObjectLists.add(fbFeedList);
                            updateNewsFeed();
                        }
                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString("fields",FB_PERMISSIONS);
        request.setParameters(parameters);
        request.executeAsync();
    }

    // -------------- PINTERST --------------------//

    private void handlePinterestApi(){
        fetchPins();
    }

    /**
     * Get users' pins and combine them with the list of social media objects
     */
    private PDKCallback getPinCallback(){
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

    private void handleInstagramApi(){
        makeApiCallforUserInfo();
    }

    private void makeApiCallforUserRecent(String userId) {
        Call<InstagramResponseObj> response = ApiServiceManager.createInstagramApiService()
                .getInstagramUserRecent(userId,getInstaAuthFromIntent());
        response.enqueue(new Callback<InstagramResponseObj>() {
            @Override
            public void onResponse(Call<InstagramResponseObj> call, Response<InstagramResponseObj> response) {
                List<Object> instagramList = new ArrayList<Object>();
                for (InstagramResponseObj.InstagramData data : response.body().getInstagramData()){
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

    private String getInstaAuthFromIntent(){
        return getIntent().getStringExtra(Constants.INSTAGRAM_OAUTH_INTENTKEY);
    }

    private String getInstaCodeFromIntent(){
        return getIntent().getStringExtra(Constants.INSTAGRAM_CODE_INTENTKEY);
    }

    // -------------- LINKEDIN --------------------//

    private void handleLinkedInApi(){
        makeLinkedInApiRequest(getLinkedInApiHelper());
    }

    private APIHelper getLinkedInApiHelper(){
        return APIHelper.getInstance(getApplicationContext());
    }

    private void makeLinkedInApiRequest(APIHelper apiHelper){
        apiHelper.getRequest(this, Constants.LINKEDIN_BASIC_URL,getLinkedInApiListener(Constants.LINKEDIN_BASIC_URL));
        apiHelper.getRequest(this, Constants.LINKEDIN_DETAIL_URL,getLinkedInApiListener(Constants.LINKEDIN_DETAIL_URL));
    }

    private ApiListener getLinkedInApiListener(final String url){
        return new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                Log.i(TAG, "onApiSuccess: LinkedIn ");
                try {
                    if (url.equals(Constants.LINKEDIN_BASIC_URL))setLinkedInData(apiResponse.getResponseDataAsJson());
                    if (url.equals(Constants.LINKEDIN_DETAIL_URL))setLinkedInImage(apiResponse.getResponseDataAsJson());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onApiError(LIApiError LIApiError) {
                LIApiError.printStackTrace();
            }
        };
    }

    private void setLinkedInData(JSONObject json){
        try{
            Log.i(TAG, "setLinkedInData: " + json.get("firstName").toString());
            Log.i(TAG, "setLinkedInData: " + json.get("headline").toString());
//            Object object = json.get("siteStandardProfileRequest");
            Log.i(TAG, "setLinkedInData: " + json.get("siteStandardProfileRequest").toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setLinkedInImage(JSONObject json){
        try{
            Log.i(TAG, "setLinkedInData: " + json.get("pictureUrl").toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // ----------------- TWITTER ------------------ //

    private void handleTwitterApi(){
        StatusesService statusService = Twitter.getInstance().getApiClient().getStatusesService();
        statusService.homeTimeline(null,null,null,null,null,null,null).enqueue(new Callback<List<Tweet>>() {
            @Override
            public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
                final List<Object> tweetList = new ArrayList<>();
                for (Tweet tweet : response.body()){
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
    private void storeCheckedButtons(){
        checkedMap = new HashMap<>();
        checkedMap.put(Constants.YOUTUBE_CHECK_INTENTKEY,getIntent().getBooleanExtra(Constants.YOUTUBE_CHECK_INTENTKEY,false));
        checkedMap.put(Constants.PINTEREST_CHECK_INTENTKEY,getIntent().getBooleanExtra(Constants.PINTEREST_CHECK_INTENTKEY,false));
        checkedMap.put(Constants.FACEBOOK_CHECK_INTENTKEY,getIntent().getBooleanExtra(Constants.FACEBOOK_CHECK_INTENTKEY,false));
        checkedMap.put(Constants.LINKEDIN_CHECK_INTENTKEY,getIntent().getBooleanExtra(Constants.LINKEDIN_CHECK_INTENTKEY,false));
        checkedMap.put(Constants.INSTAGRAM_CHECK_INTENTKEY,getIntent().getBooleanExtra(Constants.INSTAGRAM_CHECK_INTENTKEY,false));
        checkedMap.put(Constants.TWITTER_CHECK_INTENTKEY,getIntent().getBooleanExtra(Constants.TWITTER_CHECK_INTENTKEY,false));

        if(checkedMap.get(Constants.YOUTUBE_CHECK_INTENTKEY))youtubeButton.setVisibility(View.VISIBLE);
        else youtubeButton.setVisibility(View.GONE);
        if(checkedMap.get(Constants.PINTEREST_CHECK_INTENTKEY)) pinterestButton.setVisibility(View.VISIBLE);
        else pinterestButton.setVisibility(View.GONE);
        if(checkedMap.get(Constants.FACEBOOK_CHECK_INTENTKEY)) facebookButton.setVisibility(View.VISIBLE);
        else facebookButton.setVisibility(View.GONE);
        if(checkedMap.get(Constants.LINKEDIN_CHECK_INTENTKEY)) linkedinButton.setVisibility(View.VISIBLE);
        else linkedinButton.setVisibility(View.GONE);
        if(checkedMap.get(Constants.INSTAGRAM_CHECK_INTENTKEY)) instagramButton.setVisibility(View.VISIBLE);
        else instagramButton.setVisibility(View.GONE);
        if(checkedMap.get(Constants.TWITTER_CHECK_INTENTKEY)) twitterButton.setVisibility(View.VISIBLE);
        else twitterButton.setVisibility(View.GONE);
    }

    private void startShareIntent(String url){
        Intent i=new Intent(android.content.Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(android.content.Intent.EXTRA_SUBJECT,"Check out this search!");
        i.putExtra(android.content.Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(i,"Share via"));
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

    }

    private boolean isKeyboardOpen(){
        final LinearLayout rootLayout = (LinearLayout) findViewById(R.id.activity_dash_board);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                rootLayout.getWindowVisibleDisplayFrame(rect);
                int screenHeight = rootLayout.getRootView().getHeight();
                int keypadHeight = screenHeight - rect.bottom;
                Log.i(TAG, "onGlobalLayout: keypad height " + keypadHeight);
                if (keypadHeight>screenHeight*0.15) isKeyboardOpen = true;
                else isKeyboardOpen = false;
            }
        });
        return isKeyboardOpen;
    }


    private void logKeyHash(){
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.facebookapittest", PackageManager.GET_SIGNATURES);
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

}
