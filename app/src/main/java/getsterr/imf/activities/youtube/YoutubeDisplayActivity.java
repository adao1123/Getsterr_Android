package getsterr.imf.activities.youtube;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import getsterr.imf.R;

import static getsterr.imf.utilities.Constants.YOUTUBE_API_KEY;

public class YoutubeDisplayActivity extends YouTubeBaseActivity implements View.OnClickListener, YouTubePlayer.OnInitializedListener {

    public static final String YOUTUBE_DISPLAY_KEY = "YOUTUBEVIDKEY";
    public static final String YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v=";
    private static final String TAG = YoutubeDisplayActivity.class.getSimpleName();
    private YouTubePlayerView youTubePlayerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_display);
        initSharebutton();
        initYoutubePlayer();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.youtube_share_button:
                startShareIntent(YOUTUBE_VIDEO_URL + getVideoIdFromIntent());
                break;
            default:break;
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.loadVideo(getVideoIdFromIntent());
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Log.i(TAG, "onInitializationFailure: " + youTubeInitializationResult.toString());
    }

    private void initSharebutton(){
        Button shareButton = (Button)findViewById(R.id.youtube_share_button);
        shareButton.setOnClickListener(this);
    }

    private void initYoutubePlayer(){
        youTubePlayerView = (YouTubePlayerView)findViewById(R.id.youtube_view);
        youTubePlayerView.initialize(YOUTUBE_API_KEY,this);
    }

    private String getVideoIdFromIntent(){
        return getIntent().getStringExtra(YOUTUBE_DISPLAY_KEY);
    }

    private void startShareIntent(String videoLink){
        Intent i=new Intent(android.content.Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(android.content.Intent.EXTRA_SUBJECT,"Check out this Youtube Video!");
        i.putExtra(android.content.Intent.EXTRA_TEXT, videoLink);
        startActivity(Intent.createChooser(i,"Share via"));
    }
}
