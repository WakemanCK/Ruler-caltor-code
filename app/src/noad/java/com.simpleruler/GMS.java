package com.simpleruler;

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;

public class GMS {
    GMS() {
    }

    public void init(Context getContext, FrameLayout adContainerView, Display display) {
        adContainerView.setVisibility(View.GONE);
    }
}
