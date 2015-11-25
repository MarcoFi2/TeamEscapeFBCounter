package de.teamescape.dev.teamescapefbcounter.utils;

/**
 * Created by Marco on 25.11.2015.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class MailSenderTask extends AsyncTask {
    private String mailContent;

    public MailSenderTask(String mailcontent){
        this.mailContent = mailcontent;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            GMailSender sender = new GMailSender("developteamescape@gmail.com", "binichschondrin?");
            sender.sendMail("Log Report TeamEscape Facebook Counter",
                    mailContent,
                    "developteamescape@gmail.com",
                    "developteamescape@gmail.com");
            return true;
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
            return false;
        }
    }
}
