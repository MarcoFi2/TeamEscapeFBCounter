package de.teamescape.dev.teamescapefbcounter.utils;



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
 * Created by Marco on 25.10.2015.
 */
public class AsyncFacebookHandler extends AsyncTask<String, String, String> {
    private static SharedPreferences settings;
    private Context context;
    private TextView view;
    public static final String TEFBC_NAME = "TEFBC_PREFS";
    SharedPreferences.Editor editor;
    private static final String TAG = AsyncFacebookHandler.class.getSimpleName();

    public int likeCount;

    public AsyncFacebookHandler(Context constructercontext, TextView mCounterView){
        context = constructercontext;
        this.view = mCounterView;
    }

    @Override
    protected String doInBackground(String... params) {
        //TODO Synchronisation in x time
        settings = context.getSharedPreferences(TEFBC_NAME, Context.MODE_PRIVATE);


        try {
            URL url = new URL("https://api.facebook.com/method/fql.query?query=select%20like_count,%20total_count,%20share_count,%20click_count%20from%20link_stat%20where%20url=%22www.facebook.com/TeamEscapeDE%22");
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openStream());
            XPath xPath = XPathFactory.newInstance().newXPath();
            likeCount = Integer.parseInt(xPath.evaluate("//like_count/text()", doc));
            //EXPANDABLE
            //int totalCount = Integer.parseInt(xPath.evaluate("//total_count/text()", doc));
            //int shareCount = Integer.parseInt(xPath.evaluate("//share_count/text()", doc));
            //int clickCount = Integer.parseInt(xPath.evaluate("//click_count/text()", doc));
            save("LASTKNOWNFACEBOOKCOUNT", likeCount);
            publishProgress(String.valueOf(likeCount));
            Log.d(TAG, "FB FQL request fired with the result:"+ likeCount);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        //update counterview
        //view.setText(progress[0]);
        //mCounterView.invalidate();
        //Log.d(TAG, "counter updated: " + progress[0]);
        super.onProgressUpdate(progress);
    }

    private static void save(String key, Object newValue) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key,newValue.toString());
        editor.commit();
    }
}
