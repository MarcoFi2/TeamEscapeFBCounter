package de.teamescape.dev.teamescapefbcounter;


import android.annotation.SuppressLint; //required
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import de.teamescape.dev.teamescapefbcounter.utils.AsyncCounterHandler;
import de.teamescape.dev.teamescapefbcounter.utils.AsyncFacebookHandler;
import de.teamescape.dev.teamescapefbcounter.utils.SharedPreferenceHandler;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private static final String TAG = FullscreenActivity.class.getSimpleName();

    Activity context = this;

    public SharedPreferenceHandler sharedPreferenceHandler;

    private long starttime;
    private int clickcounter;

    private ImageView mContentView;
    private View mControlsView;
    private boolean mVisible;
    public static boolean initcountersizeisnotset = true;
    public boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mVisible = true;
        mContentView =  (ImageView)findViewById(R.id.fullscreen_content);
        mControlsView = findViewById(R.id.fullscreen_content_controls);

        sharedPreferenceHandler = new SharedPreferenceHandler(context);

        //initSystem(sharedPreferenceHandler);
        invokeAsyncCounterHandler((TextView) findViewById(R.id.counter_content), 1000);
        //invokeAsyncFacebookHandler(updateinterval.intValue(), txtview);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  open a second intent where the edit functions are locted
                long currenttime= System.currentTimeMillis();
                if(currenttime-starttime>10000){
                    starttime = currenttime;
                    clickcounter=1;
                }else{
                    clickcounter++;
                    if(clickcounter>10){
                        toggle();
                    }
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.settings_button).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.exit_button).setOnTouchListener(mDelayHideTouchListenerExit);
    }

    private void invokeAsyncFacebookHandler(int intervall, final TextView mCounterView) {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            AsyncFacebookHandler asyncfb = new AsyncFacebookHandler(context,mCounterView);
                            asyncfb.execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, intervall);

    }

    private void invokeAsyncCounterHandler(final TextView mCounterView, int intervall){

        final Handler handler = new Handler();
        final boolean[] dorequest = {false};
        final long[] startrequesttime = {System.currentTimeMillis()};
        final Timer[] timer = {new Timer()};
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            long currenttime = System.currentTimeMillis();
                            if(currenttime - startrequesttime[0] >5000){
                                dorequest[0] = true;
                                startrequesttime[0] = currenttime;
                            }else{
                                dorequest[0]=false;
                            }
                            AsyncCounterHandler asynccounter = new AsyncCounterHandler(context,mCounterView,context,flag,dorequest[0]);
                            asynccounter.execute();
                            flag = !flag;
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer[0].schedule(doAsynchronousTask, 0, intervall);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        //TODO TESTING
        delayedHide(0);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);

            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private final View.OnTouchListener mDelayHideTouchListenerExit = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            context.finish();
            System.exit(0);

            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
        mControlsView.invalidate();
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }



    //////////////////////////////////////////////
    //////////           INIT SECTION      //////////////
    //////////////////////////////////////////////

    /**
     * This method coordinates all initial System settings.
     *
     *
     * @param sharedPreferenceHandler
     */
    private void initSystem(SharedPreferenceHandler sharedPreferenceHandler){
        //init background
        initElemet(sharedPreferenceHandler.BACKGROUNDIMAGETITLE, sharedPreferenceHandler.BACKGROUNDIMAGEINTERNALURL,
                null, null,
                "IMAGE", "BACKGROUND",
                null, null,
                null, null);
        //init logo
        initElemet(sharedPreferenceHandler.LOGOIMAGETITLE, sharedPreferenceHandler.LOGOIMAGEINTERNALURL,
                null, null,
                "IMAGE", "LOGO",
                sharedPreferenceHandler.LOGOIMAGESIZERATIO, sharedPreferenceHandler.LOGOIMAGELEFTMARGIN,
                sharedPreferenceHandler.LOGOIMAGETOPMARGIN, null);
        //init counter
        initElemet(null,null,
                sharedPreferenceHandler.LASTKNOWNFACEBOOKCOUNT, sharedPreferenceHandler.COUNTERTEXTSIZE,
                "COUNTER","COUNTER",
                null, sharedPreferenceHandler.COUNTERTEXTLEFTMARGIN,
                sharedPreferenceHandler.COUNTERTEXTTOPMARGIN, sharedPreferenceHandler.COUNTERUPDATEINTEVAL);
        //init facebook
        initElemet(sharedPreferenceHandler.FACEBOOKIMAGETITLE, sharedPreferenceHandler.FACEBOOKIMAGEINTERNALURL,
                null, null,
                "IMAGE", "FACEBOOK",
                sharedPreferenceHandler.FACEBOOKIMAGESIZERATIO, sharedPreferenceHandler.FACEBOOKIMAGELEFTMARGIN,
                sharedPreferenceHandler.FACEBOOKIMAGETOPMARGIN, null);
        //init qr code
        initElemet(sharedPreferenceHandler.QRCODEIMAGETITLE, sharedPreferenceHandler.QRCODEIMAGEINTERNALURL,
                null, null,
                "IMAGE", "QR",
                sharedPreferenceHandler.QRCODEIMAGESIZERATIO, sharedPreferenceHandler.QRCODEIMAGELEFTMARGIN,
                sharedPreferenceHandler.QRCODEIMAGETOPMARGIN, null);
        //init text center
        initElemet(null,null,
                sharedPreferenceHandler.TEXTCONTENTCENTER, sharedPreferenceHandler.TEXTCONTENTCENTERFONTSIZE,
                "TEXT","CENTERTEXT",
                null, sharedPreferenceHandler.TEXTCONTENTCENTERLEFTMARGIN,
                sharedPreferenceHandler.TEXTCONTENTCENTERTOPMARGIN,null);
        //init text buttom
        initElemet(null,null,
                sharedPreferenceHandler.TEXTCONTENTBUTTOM, sharedPreferenceHandler.TEXTCONTENTBUTTOMFONTSIZE,
                "TEXT","BUTTOMTEXT",
                null, sharedPreferenceHandler.TEXTCONTENTBUTTOMLEFTMARGIN,
                sharedPreferenceHandler.TEXTCONTENTBUTTOMTOPMARGIN,null);

    }

    @Override
    protected void onResume(){
        super.onResume();
        sharedPreferenceHandler = new SharedPreferenceHandler(context);
        initSystem(sharedPreferenceHandler);
    }

    @Override
    protected void onStart(){
        super.onStart();
    }


    public void initElemet(String imgresourcetitle, String imageinternalurl, String content, String fontsize, String elementtag, String tagid, Double sizeratio, Double leftmargin, Double topmargin, Double updateinterval) {
        Log.d(TAG, tagid + " element updated:");

        try {
            Display display = getWindowManager().getDefaultDisplay();
            Point screensize = new Point();
            try {
                display.getRealSize(screensize);
                Log.d(TAG, "Screen resolution: " + screensize.x + "/" + screensize.y);
            } catch (NoSuchMethodError e) {
                Log.e(TAG, "exception", e);
            }

            Point imagesize = new Point();

            ImageView imgview = null;
            TextView txtview = null;
            int calculatedleftmargin = 0;
            int calculatedtopmargin = 0;

            switch(tagid) {
                case "BACKGROUND":
                    imgview = mContentView;
                    imagesize.x = screensize.x;
                    imagesize.y = screensize.y;
                    break;
                case "LOGO":
                    imgview = (ImageView)findViewById(R.id.logo_image);
                    imagesize.x = (int) (screensize.x *sizeratio);
                    imagesize.y = (int) (imagesize.x * 0.5);
                    calculatedleftmargin = (int) ((screensize.x-imagesize.x)  * leftmargin);
                    calculatedtopmargin = (int) ((screensize.y-imagesize.y) * topmargin);
                    break;
                case "FACEBOOK":
                    imgview = (ImageView) findViewById(R.id.facebooklike_image);
                    if(sharedPreferenceHandler.FIXEDALLOCATIONCOUNTERFB){
                        //fixed allocation is activated
                        TextView counterview = (TextView) findViewById(R.id.counter_content);
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) counterview.getLayoutParams();
                        imagesize.x = counterview.getMeasuredHeight();
                        imagesize.y = imagesize.x;
                        calculatedleftmargin = (int) (lp.leftMargin - imagesize.x - (imagesize.x*0.5));
                        calculatedtopmargin = (int)((screensize.y-imagesize.y) * sharedPreferenceHandler.COUNTERTEXTTOPMARGIN);
                    }else {
                        //fixed allocation is deactivated
                        imagesize.x = (int) (screensize.x * sizeratio);
                        imagesize.y = (int) (screensize.x * sizeratio);
                        calculatedleftmargin = (int) ((screensize.x - imagesize.x) * leftmargin);
                        calculatedtopmargin = (int) ((screensize.y - imagesize.y) * topmargin);
                    }
                    break;
                case "QR":
                    imgview = (ImageView) findViewById(R.id.qrcode_image);
                    imagesize.x = (int) (screensize.x *sizeratio);
                    imagesize.y = (int) (screensize.x *sizeratio);
                    calculatedleftmargin = (int) ((screensize.x - imagesize.x) * leftmargin);
                    calculatedtopmargin = (int)((screensize.y-imagesize.y) * topmargin);
                    break;
                case "COUNTER":
                    txtview = (TextView) findViewById(R.id.counter_content);
                    calculatedleftmargin = (int) ((screensize.x)  * leftmargin);
                    calculatedtopmargin = (int) ((screensize.y)  * topmargin);
                    //set background image counter_background_init scale image to size of textview
                    break;
                case "CENTERTEXT":
                    txtview = (TextView) findViewById(R.id.maintext_content);
                    calculatedleftmargin = (int) (screensize.x * leftmargin);
                    calculatedtopmargin = (int) (screensize.y * topmargin);
                    break;
                case "BUTTOMTEXT":
                    txtview = (TextView) findViewById(R.id.linktext_content);
                    calculatedleftmargin = (int) (screensize.x * leftmargin);
                    calculatedtopmargin = (int) (screensize.y * topmargin);
                    break;
                default:
            }

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            switch(elementtag) {
                case "IMAGE":
                    try{
                        //look in assets

                        int resID =getApplicationContext().getResources().getIdentifier(imgresourcetitle, "drawable", context.getPackageName());
                        Resources resources = getResources();
                        Bitmap bMap=BitmapFactory.decodeStream(context.getResources().openRawResource(resID));

                        Bitmap bScaled = getResizedBitmap(bMap, imagesize.x);
                        if(tagid!="BACKGROUND"){
                            lp.setMargins(calculatedleftmargin, calculatedtopmargin , 0, 0);
                            imgview.setLayoutParams(lp);
                        }
                        imgview.setImageBitmap(bScaled);
                        Log.d(TAG, "image " + imgresourcetitle + " loaded from assets");

                    }catch(Exception e) {
                        Log.d(TAG, "no image with name " +imgresourcetitle+" found in apk assets");
                        try{
                            //look in internal storage
                            Bitmap bMap = getBitmapFromInternalURL(imageinternalurl, imgresourcetitle);
                            Bitmap bScaled = getResizedBitmap(bMap, imagesize.x);
                            //Bitmap bScaled = Bitmap.createScaledBitmap(bMap, imagesize.x, imagesize.y, true);
                            // Assign the bitmap to an ImageView in this layout
                            if(tagid!="BACKGROUND"){
                                lp.setMargins(calculatedleftmargin, calculatedtopmargin , 0, 0);
                                if (imgview != null) {
                                    imgview.setLayoutParams(lp);
                                }
                            }
                            if (imgview != null) {
                                imgview.setImageBitmap(bScaled);
                            }
                            Log.d(TAG, "image " + imgresourcetitle + " loaded from internal storage");
                        }catch (Exception f){
                            Log.d(TAG, "no image " + imgresourcetitle + " found");
                        }
                    }
                    break;

                case "TEXT":
                    txtview.setGravity(Gravity.CENTER);
                    int TeamEscapeColor = Color.parseColor("#E6C846");
                    if(tagid=="BUTTOMTEXT" && sharedPreferenceHandler.TEXTCONTENTBUTTOMHYPERLINK){
                        txtview.setClickable(true);
                        String hyperlink_text = "<a href='http://"+content+"'> "+content + " </a>";
                        txtview.setText(Html.fromHtml(hyperlink_text));
                        txtview.setMovementMethod(LinkMovementMethod.getInstance());
                        txtview.setLinkTextColor(TeamEscapeColor);
                    }else{
                        txtview.setText(content);
                    }
                    txtview.setTextSize(Float.parseFloat(fontsize));
                    Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.otf");
                    txtview.setTypeface(tf, Typeface.BOLD);
                    int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(screensize.x, View.MeasureSpec.AT_MOST);
                    int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    txtview.measure(widthMeasureSpec, heightMeasureSpec);
                    calculatedleftmargin = (int) ((screensize.x - txtview.getMeasuredWidth() )  * leftmargin);
                    calculatedtopmargin = (int) ((screensize.y - txtview.getMeasuredHeight())  * topmargin);
                    lp.setMargins(calculatedleftmargin, calculatedtopmargin, 0, 0);
                    txtview.setLayoutParams(lp);
                    txtview.setTextColor(TeamEscapeColor);
                    break;

                case "COUNTER":
                    if (txtview != null) {
                        txtview.setGravity(Gravity.CENTER);
                        int teamEscapeColor = Color.parseColor("#E6C846");
                        String formattedcontent = formatcount(content);
                        txtview.setText(formattedcontent);
                        txtview.setTextSize(Float.parseFloat(fontsize));
                        Typeface tf_counter = Typeface.createFromAsset(getAssets(), "fonts/digital-7_mono.ttf");
                        txtview.setTypeface(tf_counter, Typeface.NORMAL);
                        int widthMeasureSpecCounter = View.MeasureSpec.makeMeasureSpec(screensize.x, View.MeasureSpec.AT_MOST);
                        int heightMeasureSpecCounter = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        txtview.measure(widthMeasureSpecCounter, heightMeasureSpecCounter);
                        calculatedleftmargin = (int) ((screensize.x - txtview.getMeasuredWidth() )  * leftmargin);
                        calculatedtopmargin = (int) ((screensize.y - txtview.getMeasuredHeight())  * topmargin);
                        lp.setMargins(calculatedleftmargin, calculatedtopmargin, 0, 0);
                        txtview.setLayoutParams(lp);
                        txtview.setTextColor(teamEscapeColor);
                    }
                    break;
                default:
            }
            Log.d(TAG, "init " + tagid + "...completed");
        } catch (Exception e) {
            Log.e(TAG, "exception", e);
        }

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //options.inPremultiplied = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        //options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        //The new size we want to scale to
        final int REQUIRED_WIDTH=reqWidth;
        final int REQUIRED_HIGHT=reqHeight;
        //Find the correct scale value. It should be the power of 2.
        int scale=1;
        while(options.outWidth/scale/2>=REQUIRED_WIDTH && options.outHeight/scale/2>=REQUIRED_HIGHT)
            scale*=2;
        BitmapFactory.Options options2 = new BitmapFactory.Options();
        options2.inSampleSize=scale;
        // Decode bitmap with inSampleSize set
        options2.inJustDecodeBounds = false;
        //options.inPreferredConfig = Bitmap.Config.RGB_565;
        //options.inDither = true;
        //return BitmapFactory.decodeResource(res, resId, options2);
        return BitmapFactory.decodeResource(res,resId,options2);
    }

    private Bitmap getResizedBitmap(Bitmap bm, int newWidth) {

        int width = bm.getWidth();

        int height = bm.getHeight();

        float aspect = (float)width / height;

        float scaleWidth = newWidth;

        float scaleHeight = scaleWidth / aspect;        // yeah!

        // create a matrix for the manipulation

        Matrix matrix = new Matrix();

        // resize the bit map

        matrix.postScale(scaleWidth / width, scaleHeight / height);

        // recreate the new Bitmap

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);

        bm.recycle();

        return resizedBitmap;
    }

    private String formatcount(String formatedcount) {
        char[] input = formatedcount.toCharArray();
        char[] prepareoutput = new char[6];
        int j = 0;
        //fill up to 6 digits
        for (int i=0; i<prepareoutput.length;++i){
            if(prepareoutput.length-(input.length +i )>0){
                prepareoutput[i]='0';
            }else{
                prepareoutput[i]=input[j];
                ++j;
            }
        }
        //insert : into digits
        char[]output = new char[8];
        int k =0;
        for (int i=0; i<output.length;++i){
            if(i==2 || i==5){
                output[i]=':';
            }else{
                output[i]=prepareoutput[k];
                ++k;
            }
        }

        return new String (output);
    }

    private Bitmap getBitmapFromInternalURL(String url, String title) {
        try {
            File f=new File(url, title);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;

    }

}
