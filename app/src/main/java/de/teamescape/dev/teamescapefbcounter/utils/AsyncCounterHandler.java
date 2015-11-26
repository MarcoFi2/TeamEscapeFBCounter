package de.teamescape.dev.teamescapefbcounter.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Created by Marco on 13.11.2015.
 */

public class AsyncCounterHandler extends AsyncTask<String, String, String> {


    private boolean switcher;
    private boolean dorequest;
    private Activity activity;

    private static SharedPreferences settings;
    private Context context;
    private TextView view;
    public static final String TEFBC_NAME = "TEFBC_PREFS";
    //SharedPreferences.Editor editor;
    private static final String TAG = AsyncFacebookHandler.class.getSimpleName();

    public int likeCount;

    public AsyncCounterHandler(Context constructercontext, TextView mCounterView, Activity activity,boolean switcher,boolean dorequest){
            context = constructercontext;
            this.view = mCounterView;
            this.activity=activity;
            this.switcher = switcher;
            this.dorequest =dorequest;
        }

    @Override
    protected String doInBackground(String... params) {
        settings = context.getSharedPreferences(TEFBC_NAME, Context.MODE_PRIVATE);
        if(Boolean.valueOf(getValue(context, "COUNTERANIMATION"))) {
            if (switcher) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String countertext = formatcountanimated(getValue(context, "LASTKNOWNFACEBOOKCOUNT"), true);
                            view.setText(countertext);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                switcher = !switcher;
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String countertext = formatcountanimated(getValue(context, "LASTKNOWNFACEBOOKCOUNT"), false);
                            view.setText(countertext);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                switcher = !switcher;
            }
        }
        //ToDo send request every x ms
        long currenttime= System.currentTimeMillis();
        if(dorequest) {
            if(!Boolean.valueOf(getValue(context, "COUNTERANIMATION"))){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String countertext = formatcountnonanimated(getValue(context, "LASTKNOWNFACEBOOKCOUNT"));
                            view.setText(countertext);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            try {
                URL url = new URL(getValue(context,"FACEBOOKFQLCALLURL"));
                //URL url = new URL("https://api.facebook.com/method/fql.query?query=select%20like_count,%20total_count,%20share_count,%20click_count%20from%20link_stat%20where%20url=%22www.facebook.com/TeamEscapeDE%22");
                //Test Fritz.de
                //URL url = new URL("https://api.facebook.com/method/fql.query?query=select%20like_count,%20total_count,%20share_count,%20click_count%20from%20link_stat%20where%20url=%22www.facebook.com/Fritz.de%22");

                DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                domFactory.setNamespaceAware(true);
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openStream());
                XPath xPath = XPathFactory.newInstance().newXPath();
                likeCount = Integer.parseInt(xPath.evaluate("//like_count/text()", doc));
                //EXPANDABLE
                //int totalCount = Integer.parseInt(xPath.evaluate("//total_count/text()", doc));
                //int shareCount = Integer.parseInt(xPath.evaluate("//share_count/text()", doc));
                //int clickCount = Integer.parseInt(xPath.evaluate("//click_count/text()", doc));

                //play sound when counter increases increases only
                if(Integer.valueOf(getValue(context, "LASTKNOWNFACEBOOKCOUNT"))<=likeCount){
                    save("FACEBOOKCOUNTINCREASED",true);
                }else{
                    save("FACEBOOKCOUNTINCREASED",false);
                }
                save("LASTKNOWNFACEBOOKCOUNT", likeCount);
                publishProgress(String.valueOf(likeCount));
                Log.d(TAG, "FB FQL request fired with the result:" + likeCount);
            } catch (IOException | ParserConfigurationException | SAXException | XPathExpressionException e) {
                e.printStackTrace();
            } catch (Exception f) {
                f.printStackTrace();
            }
        }


        return null;
        }

    private String formatcountnonanimated(String formatedcount) {
        //return blank count with . at pos
        char[] input = formatedcount.toCharArray();
        //insert . into digits
        if(input.length>3){
            char[]output = new char[input.length+1];
            int k =0;
            for (int i=0; i<output.length;++i){
                if(i== output.length-4){
                    output[i]='.';
                }else{
                    output[i]=input[k];
                    ++k;
                }
            }
            return new String (output);
        }else{
            return new String (input);
        }

    }

    private String formatcountanimated(String formatedcount, boolean substitute) {
            char[] input = formatedcount.toCharArray();
            char[] prepareoutput = new char[6];
            int j = 0;
            //fill up to 6 digits
            for (int i=0; i<prepareoutput.length;++i){
                if(prepareoutput.length-(input.length+i)>0){
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
                    if(substitute){
                        output[i]=':';
                    }else{
                        output[i]=' ';
                    }
                }else{
                    output[i]=prepareoutput[k];
                    ++k;
                }
            }
            return new String (output);
        }

    public String getValue(Context context, String TEFBC_KEY) {
            SharedPreferences settings;
            String text;

            //settings = PreferenceManager.getDefaultSharedPreferences(context);
            settings = context.getSharedPreferences(TEFBC_NAME, Context.MODE_PRIVATE);
            text = settings.getString(TEFBC_KEY, null);
            return text;
        }

    private static void save(String key, Object newValue) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key,newValue.toString());
        editor.apply();
    }
}


