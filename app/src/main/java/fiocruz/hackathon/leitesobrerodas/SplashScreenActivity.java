package fiocruz.hackathon.leitesobrerodas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import fiocruz.hackathon.leitesobrerodas.MainActivity;

public class SplashScreenActivity extends Activity {

    private final static int SPLASH_TIME_OUT = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(i);

                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_splash);
    }
}