package getsterr.getsterr.activities.main;

import android.content.Intent;
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

    Button nextButton;
    ImageButton youtubeImageButton;
    ImageButton pinterestImageButton;
    ImageButton facebookImageButton;
    ImageButton linkedinImageButton;
    ImageButton instagramImageButton;
    ImageButton twitterImageButton;

    Toolbar mainToolbar;
    ActionBar mainActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                    youtubeImageButton.getBackground().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.youtube), PorterDuff.Mode.SRC_ATOP);
                    youtubeChecked = true;
                }else{
                    Log.d(TAG, "onClick: YOUTUBE CLICKED - TRUE to FALSE");
                    youtubeImageButton.getBackground().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccentLight), PorterDuff.Mode.SRC_ATOP);
                    youtubeChecked = false;
                }
                youtubeImageButton.setBackgroundResource(R.drawable.roundcorner);
                break;
            case R.id.main_pinterest_button:
                if(pinterestChecked == false){
                    if (!getIsPinterestLoggedIn()) {
                        Toast.makeText(this,"Please Sign In to Pinterest",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    pinterestImageButton.getBackground().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.pinterest), PorterDuff.Mode.SRC_ATOP);
                    pinterestChecked = true;
                }else{
                    pinterestImageButton.getBackground().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccentLight), PorterDuff.Mode.SRC_ATOP);
                    pinterestChecked = false;
                }
                pinterestImageButton.setBackgroundResource(R.drawable.roundcorner);
                break;
            case R.id.main_facebook_button:
                if(facebookChecked == false){
                    if (!getIsFacebookLoggedIn()) {
                        Toast.makeText(this,"Please Sign In to Facebook",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    facebookImageButton.getBackground().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.facebook), PorterDuff.Mode.SRC_ATOP);
                    facebookChecked = true;
                }else {
                    facebookImageButton.getBackground().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccentLight), PorterDuff.Mode.SRC_ATOP);
                    facebookChecked = false;
                }
                facebookImageButton.setBackgroundResource(R.drawable.roundcorner);
                break;
            case R.id.main_linkedin_button:
                if(linkedinChecked == false){
                    if (!getIsLinkedInLoggedIn()) {
                        Toast.makeText(this,"Please Sign In to LinkedIn",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    linkedinImageButton.getBackground().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.linkedin), PorterDuff.Mode.SRC_ATOP);
                    linkedinChecked = true;
                }else{
                    linkedinImageButton.getBackground().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccentLight), PorterDuff.Mode.SRC_ATOP);
                    linkedinChecked = false;
                }
                linkedinImageButton.setBackgroundResource(R.drawable.roundcorner);
                break;
            case R.id.main_instagram_button:
                Log.i(TAG, "onClick: " + getInstaAuthFromIntent() + " " + getInstaCodeFromIntent());
                if(instagramChecked == false){
                    if (!getIsInstagramLoggedIn()) {
                        Toast.makeText(this,"Please Sign In to Instagram",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    instagramImageButton.getBackground().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.instagram), PorterDuff.Mode.SRC_ATOP);
                    instagramChecked = true;
                }else{
                    instagramImageButton.getBackground().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccentLight), PorterDuff.Mode.SRC_ATOP);
                    instagramChecked = false;
                }
                instagramImageButton.setBackgroundResource(R.drawable.roundcorner);
                break;
            case R.id.main_twitter_button:
                if(twitterChecked == false){
                    if (!getIsTwitterLoggedIn()) {
                        Toast.makeText(this,"Please Sign In to Twitter",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    twitterImageButton.getBackground().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.twitter), PorterDuff.Mode.SRC_ATOP);
                    twitterChecked = true;
                }else{
                    twitterImageButton.getBackground().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorAccentLight), PorterDuff.Mode.SRC_ATOP);
                    twitterChecked = false;
                }
                twitterImageButton.setBackgroundResource(R.drawable.roundcorner);
                break;
            case R.id.main_next_button:
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
        initImageButtonColors();
        initClickListeners();
    }

    private void initViews(){
        youtubeImageButton = (ImageButton)findViewById(R.id.main_youtube_button);
        pinterestImageButton = (ImageButton)findViewById(R.id.main_pinterest_button);
        facebookImageButton = (ImageButton)findViewById(R.id.main_facebook_button);
        linkedinImageButton = (ImageButton)findViewById(R.id.main_linkedin_button);
        instagramImageButton = (ImageButton)findViewById(R.id.main_instagram_button);
        twitterImageButton = (ImageButton)findViewById(R.id.main_twitter_button);
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

}