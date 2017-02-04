package getsterr.getsterr.activities.splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
//            splashLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (isFirstTime()) {
//                        setNotFirstTime();
//                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                    }else startActivity(new Intent(SplashActivity.this, DashBoardActivity.class));
//                }
//            });
            int secondsDelayed = 3;

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (isFirstTime()) {
                        setNotFirstTime();
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }else startActivity(new Intent(SplashActivity.this, DashBoardActivity.class));
                    finish();
                }
            }, secondsDelayed * 1000);

            splashLoaded = true;
        }
        else {
            Intent goToFacebookActivity;
            if (isFirstTime()) {
                setNotFirstTime();
                goToFacebookActivity = new Intent(SplashActivity.this, MainActivity.class);
            }else goToFacebookActivity = new Intent(SplashActivity.this, DashBoardActivity.class);
            goToFacebookActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(goToFacebookActivity);
            finish();
        }
    }

    private boolean isFirstTime(){
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        if (sharedPreferences.getString("FIRST_TIME_KEY","FIRST_TIME").equals("FIRST_TIME")) return true;
        else return false;
    }

    private void setNotFirstTime(){
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("FIRST_TIME_KEY","HERE");
        editor.commit();
    }
}