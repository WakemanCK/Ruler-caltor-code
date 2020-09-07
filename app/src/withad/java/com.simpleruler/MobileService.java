package com.simpleruler;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MobileService {
    AdView mAdView;

    MobileService() {
    }

    public void init(Context getContext, FrameLayout adContainerView, Display display) {
        MobileAds.initialize(getContext, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = new AdView(getContext);
        mAdView.setAdUnitId("ca-app-pub-1067337728169403/4623341024");
        adContainerView.addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(getContext, adWidth);
        mAdView.setAdSize(adSize);
        mAdView.loadAd(adRequest);
    }

    public void rateApp(Context getContext) {
        try {
            Intent rateIntent = rateIntentForUrl("market://details?id=com.simpleruler.g");
            getContext.startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details?id=com.simpleruler.g");
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
        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=com.simpleruler.g";
        intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        getContext.startActivity(Intent.createChooser(intent, getContext.getString(R.string.chooseAppText)));
    }
}
