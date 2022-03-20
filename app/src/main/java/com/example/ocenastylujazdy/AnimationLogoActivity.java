package com.example.ocenastylujazdy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

public class AnimationLogoActivity extends Activity {

    ImageView logo;
    TextView logoText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_logo);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = new Intent(this, MainActivity.class);

        logoText = findViewById(R.id.textViewLTitle);
        logo = findViewById(R.id.imageViewLogo);

        Animation alpha = new AlphaAnimation(0f, 1f);//animacja  od pełnej przeźroczystości do pełnej widoczności
        alpha.setDuration(2000);

        logo.startAnimation(alpha);
        logoText.startAnimation(alpha);

        alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(intent);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {


            }
        });
    }
    //język
    @Override
    protected void attachBaseContext (Context base){
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onPause() {
        super.onPause();
       finish();
        }
    }



