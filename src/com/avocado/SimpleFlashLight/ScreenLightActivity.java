package com.avocado.SimpleFlashLight;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import com.mopub.mobileads.MoPubView;

/**
 * Created by kyawmyintcho on 4/30/14.
 */
public class ScreenLightActivity extends Activity{
    public Button toogle;
    public boolean state;
    public Button goBack;
    public LinearLayout screenLayout;

    //For advertisement
    private MoPubView moPubView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

         super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_light);
        toogle = (Button) findViewById(R.id.toogle);
        screenLayout = (LinearLayout) findViewById(R.id.screenLayout);
        goBack = (Button) findViewById(R.id.back);
        state = false;



        toogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state == false)
                {
                    toogle.setText("Touch To Off");
                    toogle.setTextColor(getResources().getColor(R.color.onColor));
                    screenLayout.setBackgroundColor(Color.WHITE);
                    state = true;
                    goBack.setBackgroundColor(Color.TRANSPARENT);
                    goBack.setClickable(false);
                    goBack.setTextColor(Color.WHITE);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                }
                else
                {
                    toogle.setText("Touch To On");
                    toogle.setTextColor(getResources().getColor(R.color.offColor));
                    screenLayout.setBackgroundColor(Color.parseColor("#4f4f4b"));
                    state = false;
                    goBack.setBackground(getResources().getDrawable(R.drawable.round_conor_button));
                    goBack.setClickable(true);
                    goBack.setTextColor(Color.parseColor("#ff2aff20"));
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


                }
            }

        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 onBackPressed();
            }
        });

        //For advertisement
        moPubView = (MoPubView) findViewById(R.id.adview);
        moPubView.setAdUnitId("a4615ef6d15c4c9d80e5d47ac144c0f8"); // Enter your Ad Unit ID from www.mopub.com
        moPubView.loadAd();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        moPubView.destroy();


    }

    public void onBackPressed()
    {

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.startActivity(new Intent(ScreenLightActivity.this,MyActivity.class));

    }
}
