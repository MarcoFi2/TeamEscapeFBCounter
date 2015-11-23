package de.teamescape.dev.teamescapefbcounter.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.teamescape.dev.teamescapefbcounter.*;

/**
 * Created by Marco on 07.11.2015.
 */
public class LogActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //setupActionBar();
        setContentView(R.layout.logscreen);

        try {
            ///DEBUG LOG
            Process debugprocess = Runtime.getRuntime().exec("logcat -d");
            BufferedReader debugbufferedReader = new BufferedReader(
                    new InputStreamReader(debugprocess.getInputStream()));

            StringBuilder dlog=new StringBuilder();
            String debugline;
            while ((debugline = debugbufferedReader.readLine()) != null) {
                dlog.append(debugline);
            }
            ///ERROR LOG
            Process errorprocess = Runtime.getRuntime().exec("logcat -e");
            BufferedReader errorbufferedReader = new BufferedReader(
                    new InputStreamReader(errorprocess.getInputStream()));

            StringBuilder elog=new StringBuilder();
            String errorline;
            while ((errorline = errorbufferedReader.readLine()) != null) {
                elog.append(errorline);
            }

            //output
            TextView tv = (TextView)findViewById(R.id.logtext_content);
            tv.setText(
                    "DEBUGLOG --> Runtime.getRuntime().exec(logcat -d): " + dlog.toString()+
                    "ERRORLOG --> Runtime.getRuntime().exec(logcat -e): " + elog.toString());
        } catch (IOException e) {
        }

        Button copybutton= (Button) findViewById(R.id.logbutton_content);
        copybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) findViewById(R.id.logtext_content);
                String stringYouExtracted = tv.getText().toString();

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(stringYouExtracted);
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied Text", stringYouExtracted);
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(LogActivity.this, "text copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        Button exitbutton= (Button) findViewById(R.id.logexitbutton_content);
        exitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



}