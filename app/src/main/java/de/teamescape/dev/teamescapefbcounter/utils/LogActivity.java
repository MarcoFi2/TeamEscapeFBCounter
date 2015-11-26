package de.teamescape.dev.teamescapefbcounter.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.teamescape.dev.teamescapefbcounter.R;

/**
 * Created by Triax Sijambo on 07.11.2015.
 */
public class LogActivity extends Activity {

    private Activity logactivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.logscreen);
        logactivity = this;

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
                    "DEBUGLOG --> Runtime.getRuntime().exec(logcat -d): " + dlog.toString()+'\n'+'\n'+
                    "ERRORLOG --> Runtime.getRuntime().exec(logcat -e): " + elog.toString()+'\n'+'\n');
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

        Button logsendmailbutton_content= (Button) findViewById(R.id.logsendmailbutton_content);
        logsendmailbutton_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv1 = (TextView) findViewById(R.id.describtiontext_content);
                TextView tv2 = (TextView) findViewById(R.id.logtext_content);
                final String systmInfo = getSystemInformation();
                final String stringOfLog =tv1.getText().toString() + '\n' + systmInfo +'\n'+ tv2.getText().toString();
                MailSenderTask mailsender = new MailSenderTask(stringOfLog);
                Boolean sendstatus = false;
                try {
                    sendstatus = (boolean) mailsender.execute(stringOfLog).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if(sendstatus){
                    Toast.makeText(logactivity, "email send", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(logactivity, "something went wrong", Toast.LENGTH_SHORT).show();
                }

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

    private String getSystemInformation() {
        String systemInformation =
                " Android Build SDK: "+android.os.Build.VERSION.SDK_INT+
                " Android Build Version:"+ Build.VERSION.RELEASE+
                " Hardware Manufacturer: "+Build.MANUFACTURER+
                " Hardware Model: "+Build.MODEL+
                " Hardware Product: "+Build.PRODUCT+
                " Hardware Brand: "+Build.BRAND+
                " User Name: "+getUsername()+
                " User Email: "+getUserEmail();
        return systemInformation;
    }

    public String getUsername() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");

            if (parts.length > 1)
                return parts[0];
        }
        return null;
    }

    public String getUserEmail() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            return possibleEmails.get(0);
        }
        return null;
    }

}