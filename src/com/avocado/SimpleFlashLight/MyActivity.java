package com.avocado.SimpleFlashLight;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.content.pm.PackageManager;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Camera.Parameters;

import com.mopub.mobileads.MoPubView;


import java.io.IOException;

public class MyActivity extends Activity implements SurfaceHolder.Callback {
    private static final String TAG ="Simple Touch" ;
    /**
     * Called when the activity is first created.
     */
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    boolean on = false;
    private static Button powerButton;
    private static TextView batteryView;
    private static Button screenButton;
    Parameters p;


    //Notification
    private static final int NOTIFICATION_ID = 7;
    private Intent mNotificationIntent;
    private PendingIntent mContentIntent;
    String contentTitle = "Simple Touch Light";
    String contentText ="Touch Light is On";
    static final int sdk = android.os.Build.VERSION.SDK_INT;

    //variable
    private static int batteryPercent;
    private static float batteryFloat;

    //For advertisement
    private MoPubView moPubView;

    //for battery
    BroadcastReceiver batteryReceiver;
    IntentFilter filter;

    //for advertisement
    // Declare an instance variable for your MoPubView.


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreated called");
        setContentView(R.layout.main);
         powerButton = (Button) findViewById(R.id.On);
         powerButton.setText("OFF");
        screenButton = (Button) findViewById(R.id.screenButton);
        Context context = this;
        final PackageManager mypackageManager = context.getPackageManager();

        // if device support camera?
        if (!mypackageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            alertUser();
        }


       camera = Camera.open();
        p = camera.getParameters();


         //For notification
        mNotificationIntent = new Intent(getApplicationContext(),MyActivity.class);
        mContentIntent = PendingIntent.getActivity(this, 0,mNotificationIntent, Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);



        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mypackageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    alertUser();}

                 if (on == false)
                {
                    onTheme();
                    on = true;
                    turnOnLight();

                }
                else
                {
                   offTheme();
                    on = false;
                   turnOffLight();

                }
            }
        });

        screenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent screenIntent = new Intent(MyActivity.this,ScreenLightActivity.class);

               // screenIntent.putExtra("ActivityName","MyActivity");
                offTheme();
                turnOffLight();
                startActivity(screenIntent);


            }
        });


        surfaceView = (SurfaceView) this.findViewById(R.id.hiddenSurfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        batteryView = (TextView)findViewById(R.id.textView);

         batteryReceiver = new BroadcastReceiver() {
            int scale = -1;
            int level = -1;

            @Override
            public void onReceive(Context context, Intent intent) {
                level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                batteryFloat = level /(float)scale * 100 ;
                batteryPercent = (int) batteryFloat;
                batteryView.setText(Integer.toString(batteryPercent) + "%");
            }
        };
        filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);


        //For advertisement
        moPubView = (MoPubView) findViewById(R.id.adview);
        moPubView.setAdUnitId("a4615ef6d15c4c9d80e5d47ac144c0f8"); // Enter your Ad Unit ID from www.mopub.com
        moPubView.loadAd();

    }




    public void onTheme()
    {
        powerButton.setText("ON");
        powerButton.setTextColor(getResources().getColor(R.color.onColor));
        screenButton.setTextColor(getResources().getColor(R.color.onColor));
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            powerButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_orange));
            screenButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_conor_button_orange));
        } else {
            powerButton.setBackground(getResources().getDrawable(R.drawable.round_button_orange));
            screenButton.setBackground(getResources().getDrawable(R.drawable.round_conor_button_orange));
        }

         batteryView.setTextColor(getResources().getColor(R.color.onColor));

        prepareNotification();
    }

    public void offTheme()
    {
        powerButton.setText("OFF");
        powerButton.setTextColor(getResources().getColor(R.color.offColor));
        screenButton.setTextColor(getResources().getColor(R.color.offColor));
        //setBackground method is for API above 16
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            powerButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
            screenButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_conor_button));
        }
        else//for API 11 to 16
        {
            powerButton.setBackground(getResources().getDrawable(R.drawable.round_button));
            screenButton.setBackground(getResources().getDrawable(R.drawable.round_conor_button));
        }

        batteryView.setTextColor(getResources().getColor(R.color.offColor));

        cancelNotification();

    }


    public void turnOnLight()
    {

        Runnable TurnOnLight = new Runnable() {
            public void run() {


                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                try{
                    camera.setParameters(p);
                    camera.startPreview();
                }
                catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }

            }

        };

        Thread t = new Thread(TurnOnLight);
        t.start();
    }
    public void turnOffLight()
    {
        Runnable TurnOnLight = new Runnable() {
            public void run() {
                p.setFlashMode(Parameters.FLASH_MODE_OFF);
                try{
                    camera.setParameters(p);
                    camera.stopPreview();
                }
                catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }

            }

        };

        Thread t = new Thread(TurnOnLight);
        t.start();
    }


    public void alertUser()
    {
        {
            Toast.makeText(this, "This device don't have Camera", Toast.LENGTH_LONG).show();
        }
    }
    /*
    public void testOutput(int n,Parameters p)
    {
        if(n == 1)
            Toast.makeText(this, "camera data " + p.getFlashMode(), Toast.LENGTH_LONG).show();
        else if(n == 2)
            Toast.makeText(this, "camera data "+ p.getFlashMode() , Toast.LENGTH_LONG).show();
    }
     */
    @Override
    public void onPause()
    {
        super.onPause();
      //  Log.d(TAG, "onPause() called");
        if(p.getFlashMode().equals(Parameters.FLASH_MODE_TORCH))
        {
            prepareNotification();
        }
        else
        {
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
        try{
            registerReceiver(batteryReceiver,filter);
        }
        catch (IllegalArgumentException e)
        {
            Log.d(TAG,"Receiver Not register") ;
        }



    }

    @Override
    protected void onStop() {
        super.onStop();
     //   Log.d(TAG, "onStop() called");

        if(p.getFlashMode().equals(Parameters.FLASH_MODE_TORCH))
        {
            prepareNotification();
        }
        else
        {   try{

            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }

        catch (RuntimeException e){}

        }
        try{
        unregisterReceiver(batteryReceiver);    }
        catch(IllegalArgumentException e)
        {
            Log.d(TAG,"Receiver Not register");
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
      //  Log.d(TAG, "onStart() called");
          try{
        if(p.getFlashMode().equals(Parameters.FLASH_MODE_TORCH))
        {
            onTheme();
        }
        else
        {
            offTheme();

        }
          }catch(NullPointerException e)
          {

          }

    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
       // Log.d(TAG, "onRestart() called");
        try{
            if(camera == null)
            {

                camera = Camera.open();
                p = camera.getParameters();
            }
            //if I don't call this method, it the touch light won't open
            camera.setPreviewDisplay(surfaceHolder);
            registerReceiver(batteryReceiver, filter);

        }
        catch (IOException e)
        {

        }


    }
    @Override
    protected void onResume()
    {
        super.onResume();
      //  Log.d(TAG, "onResume() called");
        try{
            if(camera == null)
            {

                camera = Camera.open();
                p = camera.getParameters();
            }

          registerReceiver(batteryReceiver,filter);

        }
        catch (RuntimeException e)
        {

        }

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
      //  Log.d(TAG, "surfaceCrated() called");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        try {
             camera.setPreviewDisplay(holder);
           // Log.d(TAG, "surfaceChanged() called");
        } catch (IOException e) {
            Log.e(TAG, "Unexpected IO Exception in setPreviewDisplay()", e);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
       // Log.d(TAG, "surfaceDestroyed called");
    }

    @Override
    public void onDestroy()
    {
         super.onDestroy();
      //  Log.d(TAG, "onDestroy() called");
        if(camera != null)
        {
            camera.release();
            camera = null;
        }
        moPubView.destroy();

    }

    public void prepareNotification()
    {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        Notification notification = notificationBuilder.setContentIntent(mContentIntent)
                 .setSmallIcon(R.drawable.icon)
                 .setAutoCancel(true)
                .setContentTitle(contentTitle)
                .setContentText(contentText).build();


        // Pass the Notification to the NotificationManager:
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, notification);


    }

    public void cancelNotification()
    {
        if (Context.NOTIFICATION_SERVICE!=null) {
            String notificationString = Context.NOTIFICATION_SERVICE;
            NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(notificationString);
            nMgr.cancel(NOTIFICATION_ID);
        }
    }

    public void onBackPressed() {
        offTheme();
        turnOffLight();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }




}
