package getsterr.getsterr.activities.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import getsterr.getsterr.R;
import getsterr.getsterr.activities.main.MainActivity;
import getsterr.getsterr.models.instagram.TokenResponse;
import getsterr.getsterr.utilities.ApiServiceManager;
import getsterr.getsterr.utilities.AuthenticationDialog;
import getsterr.getsterr.utilities.AuthenticationListener;
import getsterr.getsterr.utilities.Constants;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AuthenticationListener{

    private static final String TAG = LoginActivity.class.getSimpleName();

    LoginButton facebookLoginButton;
    CallbackManager callbackManager;
    private boolean isInstagramLoggedIn, isFacebookLoggedIn,
            isLinkedInLoggedIn, isPinterestLoggedIn, isTwitterLoggedIn = false;
    private PDKClient pdkClient;
    Button pinterestLoginButton;
    ImageButton linkedinLoginButton;
    Button instagramLoginButton;
    TwitterLoginButton twitterLoginButton;
    TwitterAuthClient twitterAuthClient;
    String instaAuthToken, instaCode;
    AuthenticationDialog instagramDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSDK();
        setContentView(R.layout.activity_login_updated);
        initSaveButton();
        initLinkedinLoginButton();
        initTwitterLoginButton();
        initPinterestLoginButton();
        initFacebookLoginButton();
        initInstagramLoginButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //LinkedIn
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
        //Twitter
        twitterAuthClient.onActivityResult(requestCode, resultCode, data);
        //Pinterest
        PDKClient.getInstance().onOauthResponse(requestCode, resultCode, data);
        //Facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.pinterst_login_button:
                onPinterestLogin();
                break;
            case R.id.linkedin_login_button:
                onLinkedinLogin();
                break;
            case R.id.instagram_login_button:
                onInstagramLogin();
                break;
            case R.id.login_save_button:
                goBackToMainActivity();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        goBackToMainActivity();
        super.onBackPressed();
    }

    private void goBackToMainActivity(){
        Intent goToMainIntent = new Intent(LoginActivity.this, MainActivity.class);
        goToMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        goToMainIntent.putExtra(Constants.INSTAGRAM_OAUTH_INTENTKEY,instaAuthToken);
        goToMainIntent.putExtra(Constants.INSTAGRAM_CODE_INTENTKEY,instaCode);
        goToMainIntent.putExtra(Constants.FACEBOOK_LOGGEDIN_INTENTKEY, isFacebookLoggedIn);
        goToMainIntent.putExtra(Constants.INSTAGRAM_LOGGEDIN_INTENTKEY, isInstagramLoggedIn);
        goToMainIntent.putExtra(Constants.PINTEREST_LOGGEDIN_INTENTKEY, isPinterestLoggedIn);
        goToMainIntent.putExtra(Constants.TWITTER_LOGGEDIN_INTENTKEY, isTwitterLoggedIn);
        goToMainIntent.putExtra(Constants.LINKEDIN_LOGGEDIN_INTENTKEY, isLinkedInLoggedIn);
        startActivity(goToMainIntent);
        finish();
    }

    private void initSaveButton(){
        Button saveButton = (Button)findViewById(R.id.login_save_button);
        saveButton.setOnClickListener(this);
    }

    private void initSDK(){
        // Facebook SDK Init
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        // Twitter SDK Init
        TwitterAuthConfig authConfig = new TwitterAuthConfig(Constants.TWITTER_KEY, Constants.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        twitterAuthClient = new TwitterAuthClient();
    }

    private void initInstagramLoginButton(){
        instagramLoginButton = (Button)findViewById(R.id.instagram_login_button);
        instagramLoginButton.setOnClickListener(this);
    }

    private void initLinkedinLoginButton(){
        linkedinLoginButton = (ImageButton) findViewById(R.id.linkedin_login_button);
        linkedinLoginButton.setOnClickListener(this);
    }

    private void initTwitterLoginButton(){
        final ImageButton twitterLoginButtonCustom = (ImageButton) findViewById(R.id.twitter_login_button);
        twitterLoginButtonCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twitterAuthClient.authorize(LoginActivity.this, new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> result) {
                        Log.d(TAG, "success: TWITTER LOGIN SUCCESSFUL");
                        TwitterSession session = Twitter.getSessionManager().getActiveSession();
                        TwitterAuthToken authToken = session.getAuthToken();
                        Log.i(TAG, "Twitter success: username " + session.getUserName());
                        String token = authToken.token;
                        String secret = authToken.secret;
                        Log.d(TAG, "onCreate: Twitter token "+token);
                        isTwitterLoggedIn = true;
                        Toast.makeText(LoginActivity.this, "Logged into Twitter", Toast.LENGTH_SHORT).show();
                        twitterLoginButtonCustom.setBackgroundResource(R.drawable.logout_twitter);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        exception.printStackTrace();
                    }
                });
            }
        });
    }

//    private void initTwitterLoginButton(){
//        // Twitter Stuff
//        twitterLoginButton = (TwitterLoginButton)findViewById(R.id.twitter_login_button);
//        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
//            @Override
//            public void success(Result<TwitterSession> result) {
//                Log.d(TAG, "success: TWITTER LOGIN SUCCESSFUL");
//                TwitterSession session = Twitter.getSessionManager().getActiveSession();
//                TwitterAuthToken authToken = session.getAuthToken();
//                Log.i(TAG, "Twitter success: username " + session.getUserName());
//                String token = authToken.token;
//                String secret = authToken.secret;
//                Log.d(TAG, "onCreate: Twitter token "+token);
//                isTwitterLoggedIn = true;
//                Toast.makeText(LoginActivity.this, "Logged into Twitter", Toast.LENGTH_SHORT).show();
////                RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.login_frame_twitter);
////                relativeLayout.setBackgroundResource(R.color.twitter);
//            }
//            @Override
//            public void failure(TwitterException exception) {
//                exception.printStackTrace();
//            }
//        });
//    }

    private void initPinterestLoginButton(){
        pinterestLoginButton = (Button)findViewById(R.id.pinterst_login_button);
        pinterestLoginButton.setOnClickListener(this);
        pdkClient = PDKClient.configureInstance(this, Constants.PINTEREST_ID);
        PDKClient.getInstance().onConnect(this);
        pdkClient.setDebugMode(true);
    }

    private void initFacebookLoginButton(){
        AccessToken fbAccessToken = AccessToken.getCurrentAccessToken();
        Profile fbProfile = Profile.getCurrentProfile();

        facebookLoginButton = (LoginButton)findViewById(R.id.fb_login_button);
        facebookLoginButton.setReadPermissions(Arrays.asList("email", "user_posts","user_photos","public_profile","user_about_me"));
        callbackManager = CallbackManager.Factory.create();
        isFacebookLoggedIn = AccessToken.getCurrentAccessToken()!=null;
//        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.login_frame_facebook);
//        relativeLayout.setBackgroundResource(R.color.facebook);
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess: FACEBOOK LOGIN SUCCESS");
                isFacebookLoggedIn = true;
                Log.i(TAG, "onSuccess: " + loginResult.getAccessToken().getUserId());
                Log.i(TAG, "onSuccess: " + AccessToken.getCurrentAccessToken().getUserId());
                Toast.makeText(LoginActivity.this, "Logged into Facebook", Toast.LENGTH_SHORT).show();
//                RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.login_frame_facebook);
//                relativeLayout.setBackgroundResource(R.color.facebook);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
            }
        });
    }


    // -------------------- PINTERST ONCLICK---------------------//
    private void onPinterestLogin(){
        List scopes = new ArrayList<String>();
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_RELATIONSHIPS);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_RELATIONSHIPS);

        pdkClient.login(this, scopes, new PDKCallback() {
            @Override
            public void onSuccess(PDKResponse response) {
                Log.d(getClass().getName(), response.getData().toString());
//                goBackToMainActivity();
                isPinterestLoggedIn = true;
                Toast.makeText(LoginActivity.this, "Logged into Pinterest", Toast.LENGTH_SHORT).show();
//                pinterestLoginButton.setText("Logged Into Pinterest");
                pinterestLoginButton.setBackgroundResource(R.drawable.logout_pinterest);
//                RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.login_frame_pinterest);
//                relativeLayout.setBackgroundResource(R.color.pinterest);
            }

            @Override
            public void onFailure(PDKException exception) {
                Log.e(getClass().getName(), exception.getDetailMessage());
            }
        });
    }

//    private void onPinterestLoginSuccess() {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        finish();
//    }

    // ------------------- LINKEDIN ONCLICK-----------------//
    private void onLinkedinLogin(){
        LISessionManager.getInstance(getApplicationContext()).init(this, linkedinBuildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                Toast.makeText(LoginActivity.this, "Logged into LinkedIn", Toast.LENGTH_SHORT).show();
                isLinkedInLoggedIn = true;
                Log.d(TAG, "onAuthSuccess: LINKEDIN SUCCESS  ");
                linkedinLoginButton.setBackgroundResource(R.drawable.logout_linkedin);
//                RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.login_frame_linkedin);
//                relativeLayout.setBackgroundResource(R.color.linkedin);
            }

            @Override
            public void onAuthError(LIAuthError error) {

                Log.d(TAG, "onAuthError: LINKEDIN ERROR: " + error.toString());

            }
        }, true);
    }
    private static Scope linkedinBuildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    // ------------------- INSTAGRAM ONCLICK-----------------//

    private void onInstagramLogin(){
        instagramDialog = new AuthenticationDialog(this, this);
        instagramDialog.show();
    }

    @Override
    public void onCodeReceived(final String code) {
        if (code != null) {
            Log.i(TAG, "onCodeReceived: code null");
//            instagramDialog.dismiss();
        }
        Log.i(TAG, "onCodeReceived: " + code);
        final Call<TokenResponse> accessTokenCall = ApiServiceManager.createInstagramApiService()
                .getAccessToken(Constants.INSTAGRAM_CLIENT_ID, Constants.INSTAGRAM_CLIENT_SECRET,
                        Constants.INSTAGRAM_REDIRECT_URL, Constants.INSTAGRAM_AUTHORIZATION_CODE,code);
        accessTokenCall.enqueue(new retrofit2.Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful()){
                    String authToken = response.body().getAccess_token();
                    Log.i(TAG, "onResponse: authToken: " + authToken);
//                    goBackToMainActivity(authToken, code);
                    instaAuthToken = authToken;
                    instaCode = code;
                    instagramDialog.dismiss();
                    instagramLoginButton.setBackgroundResource(R.drawable.logout_instagram);
                    isInstagramLoggedIn = true;
//                    RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.login_frame_instagram);
//                    relativeLayout.setBackgroundResource(R.color.instagram);
                    Toast.makeText(LoginActivity.this, "Logged into Instagram", Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        Log.i(TAG, "onResponse: " + response.errorBody().string());
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    // ------------------- FACEBOOK ONCLICK-----------------//
    private void onFacebookLoggedIn(){

    }
}
