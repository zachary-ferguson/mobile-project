package com.example.mobileproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;
        Constants.X_UNIT = Constants.SCREEN_WIDTH/10;
        Constants.Y_UNIT = Constants.SCREEN_HEIGHT/5;

        setContentView(new GamePanel(this));
    }

    public void goHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}