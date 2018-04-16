package com.mudassirkhan.androidportfolio.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.app.ShareCompat;

import com.mudassirkhan.androidportfolio.R;

import java.util.List;

public class ImplicitIntentUtils {

    //Static method that sends an implicit Intent to call the indicated number
    public static void dialNumber(Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + context.getString(R.string.intents_my_phone_number)));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }


    //Static method that sends an implicit Intent to send a SMS to the indicated number
    public static void sendSms(Context context) {
        Uri uri = Uri.parse("smsto:" + context.getString(R.string.intents_my_phone_number));
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", context.getString(R.string.intents_message_content));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }


    //Static method that sends an implicit Intent to send an email to the indicated email address
    public static void sendEmail(Context context) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + context.getString(R.string.intents_my_email_address)));
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.intents_message_content));
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.intents_message_subject));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }


    //Static method that sends an Intent in order to open my Linkedin profile on Linkedin's app, if installed. If not, it will open it on a Web Browser.
    public static void openLinkedin(Context context) {
        Uri uri = Uri.parse(context.getString(R.string.navigation_drawer_linkedin_scheme) + context.getString(R.string.navigation_drawer_linkedin_id));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.isEmpty()) {
            uri = Uri.parse(context.getString(R.string.navigation_drawer_linkedin_url) + context.getString(R.string.navigation_drawer_linkedin_id));
            intent = new Intent(Intent.ACTION_VIEW, uri);
        }
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }


    //Static method that sends an implicit Intent to open the indicated Web Page
    public static void openWebPage(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }


    //Static method that sends an Intent in order for the user to be able to share my App
    public static void shareContent(Activity context, String content) {

        Intent shareIntent = ShareCompat.IntentBuilder.from(context)
                .setType("text/plain")
                .setText(content)
                .getIntent();
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (shareIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.intents_share_title)));
        }

    }

}





