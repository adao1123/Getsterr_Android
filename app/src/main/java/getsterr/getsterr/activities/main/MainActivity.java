package getsterr.getsterr.activities.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;

import getsterr.getsterr.activities.login.LoginActivity;
import getsterr.getsterr.R;
import getsterr.getsterr.activities.dashboard.DashBoardActivity;
import getsterr.getsterr.utilities.Constants;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    boolean youtubeChecked = false;
    boolean pinterestChecked = false;
    boolean facebookChecked = false;
    boolean linkedinChecked = false;
    boolean instagramChecked = false;
    boolean twitterChecked = false;
    boolean snapchatChecked = false;

    Button nextButton;
    ImageButton youtubeImageButton;
    ImageButton pinterestImageButton;
    ImageButton facebookImageButton;
    ImageButton linkedinImageButton;
    ImageButton instagramImageButton;
    ImageButton twitterImageButton;
    ImageButton snapchatImageButton;

    Toolbar mainToolbar;
    ActionBar mainActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_setup);
        initActionBar();
        initButtons();
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: ONCLICK");
        //TODO Use a factory
        switch(view.getId()){
            case R.id.main_youtube_button:
                if(youtubeChecked == false){
                    Log.d(TAG, "onClick: YOUTUBE CLICKED - FALSE to TRUE");
                    youtubeImageButton.setBackgroundResource(R.drawable.circle_youtube_color);
                    youtubeChecked = true;
                }else{
                    Log.d(TAG, "onClick: YOUTUBE CLICKED - TRUE to FALSE");
                    youtubeImageButton.setBackgroundResource(R.drawable.circle_youtube_grey);
                    youtubeChecked = false;
                }
                break;
            case R.id.main_pinterest_button:
                if(pinterestChecked == false){
                    if (!getIsPinterestLoggedIn()) {
                        Toast.makeText(this,"Please Sign In to Pinterest",Toast.LENGTH_SHORT).show();
                        goToLoginActivity();
                        return;
                    }
                    pinterestImageButton.setBackgroundResource(R.drawable.circle_pinterest_color);
                    pinterestChecked = true;
                }else{
                    pinterestImageButton.setBackgroundResource(R.drawable.circle_pinterest_grey);
                    pinterestChecked = false;
                }
                break;
            case R.id.main_facebook_button:
                if(facebookChecked == false){
                    if (!getIsFacebookLoggedIn()) {
                        Toast.makeText(this,"Please Sign In to Facebook",Toast.LENGTH_SHORT).show();
                        goToLoginActivity();
                        return;
                    }
                    facebookImageButton.setBackgroundResource(R.drawable.circle_facebook_color);
                    facebookChecked = true;
                }else {
                    facebookImageButton.setBackgroundResource(R.drawable.circle_facebook_grey);
                    facebookChecked = false;
                }
                break;
            case R.id.main_linkedin_button:
                if(linkedinChecked == false){
                    if (!getIsLinkedInLoggedIn()) {
                        Toast.makeText(this,"Please Sign In to LinkedIn",Toast.LENGTH_SHORT).show();
                        goToLoginActivity();
                        return;
                    }
                    linkedinImageButton.setBackgroundResource(R.drawable.circle_linkedin_color);
                    linkedinChecked = true;
                }else{
                    linkedinImageButton.setBackgroundResource(R.drawable.circle_linkedin_grey);
                    linkedinChecked = false;
                }
                break;
            case R.id.main_instagram_button:
                Log.i(TAG, "onClick: " + getInstaAuthFromIntent() + " " + getInstaCodeFromIntent());
                if(instagramChecked == false){
                    if (!getIsInstagramLoggedIn()) {
                        Toast.makeText(this,"Please Sign In to Instagram",Toast.LENGTH_SHORT).show();
                        goToLoginActivity();
                        return;
                    }
                    instagramImageButton.setBackgroundResource(R.drawable.circle_instagram_color);
                    instagramChecked = true;
                }else{
                    instagramImageButton.setBackgroundResource(R.drawable.circle_instagram_grey);
                    instagramChecked = false;
                }
                break;
            case R.id.main_twitter_button:
                if(twitterChecked == false){
                    if (!getIsTwitterLoggedIn()) {
                        Toast.makeText(this,"Please Sign In to Twitter",Toast.LENGTH_SHORT).show();
                        goToLoginActivity();
                        return;
                    }
                    twitterImageButton.setBackgroundResource(R.drawable.circle_twitter_color);
                    twitterChecked = true;
                }else{
                    twitterImageButton.setBackgroundResource(R.drawable.circle_twitter_grey);
                    twitterChecked = false;
                }
                break;
            case R.id.main_snapchat_button:
                if(snapchatChecked == false){
                    if (!getIsSnapchatLoggedIn()) {
                        Toast.makeText(this,"Please Sign In to Snapchat",Toast.LENGTH_SHORT).show();
                        goToLoginActivity();
                        return;
                    }
                    snapchatImageButton.setBackgroundResource(R.drawable.circle_snapchat_color);
                    snapchatChecked = true;
                }else{
                    snapchatImageButton.setBackgroundResource(R.drawable.circle_snapchat_grey);
                    snapchatChecked = false;
                }
                break;
            case R.id.main_next_button:
                saveCheckedButtonSP();
                goToDashBoardActivity();
                break;
            case R.id.menu_hamburger_iv:
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void goToLoginActivity(){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void goToDashBoardActivity(){
        Intent intent = new Intent(MainActivity.this, DashBoardActivity.class);
        intent.putExtra(Constants.YOUTUBE_CHECK_INTENTKEY, youtubeChecked);
        intent.putExtra(Constants.PINTEREST_CHECK_INTENTKEY, pinterestChecked);
        intent.putExtra(Constants.FACEBOOK_CHECK_INTENTKEY, facebookChecked);
        intent.putExtra(Constants.LINKEDIN_CHECK_INTENTKEY, linkedinChecked);
        intent.putExtra(Constants.INSTAGRAM_CHECK_INTENTKEY, instagramChecked);
        intent.putExtra(Constants.TWITTER_CHECK_INTENTKEY, twitterChecked);
        intent.putExtra(Constants.INSTAGRAM_OAUTH_INTENTKEY,getInstaAuthFromIntent());
        intent.putExtra(Constants.INSTAGRAM_CODE_INTENTKEY,getInstaCodeFromIntent());
        startActivityForResult(intent, 40);
    }

    private void saveCheckedButtonSP(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.CHECKED_SP,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.YOUTUBE_CHECK_SPKEY,youtubeChecked);
        editor.putBoolean(Constants.PINTEREST_CHECK_SPKEY,pinterestChecked);
        editor.putBoolean(Constants.FACEBOOK_CHECK_SPKEY,facebookChecked);
        editor.putBoolean(Constants.LINKEDIN_CHECK_SPKEY,linkedinChecked);
        editor.putBoolean(Constants.INSTAGRAM_CHECK_SPKEY,instagramChecked);
        editor.putBoolean(Constants.TWITTER_CHECK_SPKEY,twitterChecked);
        editor.putBoolean(Constants.SNAPCHAT_CHECK_SPKEY,snapchatChecked);
        editor.commit();
    }

    private void initActionBar(){
        mainToolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        mainActionBar = getSupportActionBar();
        mainActionBar.setDisplayShowHomeEnabled(false);
        mainActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = LayoutInflater.from(this);
        View actionBarView = inflater.inflate(R.layout.actionbar_layout,null);
        mainActionBar.setCustomView(actionBarView);
        mainActionBar.setDisplayShowCustomEnabled(true);
        mainActionBar.setTitle("Set Up");
        ImageView loginButton = (ImageView)actionBarView.findViewById(R.id.menu_hamburger_iv);
        loginButton.setOnClickListener(this);
    }

    private void initButtons(){
        initViews();
//        initImageButtonColors();
        initClickListeners();
    }

    private void initViews(){
        youtubeImageButton = (ImageButton)findViewById(R.id.main_youtube_button);
        pinterestImageButton = (ImageButton)findViewById(R.id.main_pinterest_button);
        facebookImageButton = (ImageButton)findViewById(R.id.main_facebook_button);
        linkedinImageButton = (ImageButton)findViewById(R.id.main_linkedin_button);
        instagramImageButton = (ImageButton)findViewById(R.id.main_instagram_button);
        twitterImageButton = (ImageButton)findViewById(R.id.main_twitter_button);
        snapchatImageButton = (ImageButton)findViewById(R.id.main_snapchat_button);
        nextButton = (Button)findViewById(R.id.main_next_button);
    }

    private void initImageButtonColors(){
        youtubeImageButton.getBackground().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccentLight), PorterDuff.Mode.SRC_ATOP);
        pinterestImageButton.getBackground().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccentLight), PorterDuff.Mode.SRC_ATOP);
        facebookImageButton.getBackground().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccentLight), PorterDuff.Mode.SRC_ATOP);
        linkedinImageButton.getBackground().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccentLight), PorterDuff.Mode.SRC_ATOP);
        instagramImageButton.getBackground().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccentLight), PorterDuff.Mode.SRC_ATOP);
        twitterImageButton.getBackground().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccentLight), PorterDuff.Mode.SRC_ATOP);
    }

    private void initClickListeners(){
        youtubeImageButton.setOnClickListener(this);
        pinterestImageButton.setOnClickListener(this);
        facebookImageButton.setOnClickListener(this);
        linkedinImageButton.setOnClickListener(this);
        instagramImageButton.setOnClickListener(this);
        twitterImageButton.setOnClickListener(this);
        snapchatImageButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }

    private String getInstaAuthFromIntent(){
        return getIntent().getStringExtra(Constants.INSTAGRAM_OAUTH_INTENTKEY);
    }

    private String getInstaCodeFromIntent(){
        return getIntent().getStringExtra(Constants.INSTAGRAM_CODE_INTENTKEY);
    }

    private boolean getIsFacebookLoggedIn(){
        return getIntent().getBooleanExtra(Constants.FACEBOOK_LOGGEDIN_INTENTKEY,false);
    }
    private boolean getIsInstagramLoggedIn(){
        return getIntent().getBooleanExtra(Constants.INSTAGRAM_LOGGEDIN_INTENTKEY,false);
    }
    private boolean getIsLinkedInLoggedIn(){
        return getIntent().getBooleanExtra(Constants.LINKEDIN_LOGGEDIN_INTENTKEY,false);
    }
    private boolean getIsPinterestLoggedIn(){
        return getIntent().getBooleanExtra(Constants.PINTEREST_LOGGEDIN_INTENTKEY,false);
    }
    private boolean getIsTwitterLoggedIn(){
        return getIntent().getBooleanExtra(Constants.TWITTER_LOGGEDIN_INTENTKEY,false);
    }
    private boolean getIsSnapchatLoggedIn(){
        return true;
    }

}
