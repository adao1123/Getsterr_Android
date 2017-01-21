package getsterr.getsterr.activities.splash;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import getsterr.getsterr.R;
import getsterr.getsterr.activities.dashboard.DashBoardActivity;
import getsterr.getsterr.activities.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    private static boolean splashLoaded = false;
    RelativeLayout splashLayout;
//    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!splashLoaded) {
            setContentView(R.layout.activity_splash);
            splashLayout = (RelativeLayout)findViewById(R.id.splash_layout);
            splashLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SplashActivity.this, DashBoardActivity.class));
                }
            });
            int secondsDelayed = 1;
//            setFont();

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(SplashActivity.this, DashBoardActivity.class));
                    finish();
                }
            }, secondsDelayed * 5000);

            splashLoaded = true;
        }
        else {
            Intent goToFacebookActivity = new Intent(SplashActivity.this, DashBoardActivity.class);
            goToFacebookActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(goToFacebookActivity);
            finish();
        }
    }
}