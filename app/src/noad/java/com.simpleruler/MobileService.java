package com.simpleruler;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;

public class MobileService {
    MobileService() {
    }

    public void init(Context getContext, FrameLayout adContainerView, Display display) {
        adContainerView.setVisibility(View.GONE);
    }

    public void rateApp(Context getContext) {
        try {
            Intent rateIntent = rateIntentForUrl("market://details?id=com.simpleruler.noad");
            getContext.startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://appgallery.huawei.com/#/app/C102640913");
            getContext.startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String urlString) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        intent.addFlags(flags);
        return intent;
    }

    public void shareApp(Context getContext) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getContext.getString(R.string.app_name));
        String shareMessage = getContext.getString(R.string.shareMessageText);
        shareMessage = shareMessage + "https://appgallery.huawei.com/#/app/C102640913";
        intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        getContext.startActivity(Intent.createChooser(intent, getContext.getString(R.string.chooseAppText)));
    }
}
