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

public class GMS {
    AdView mAdView;

    GMS() {
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
}
