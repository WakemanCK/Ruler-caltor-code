package com.simpleruler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

public class InformationPopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_popup);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels * 0.8);
        int height = (int) (displayMetrics.heightPixels * 0.4);
        getWindow().setLayout(width, height);
        try {
            PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            TextView textView = findViewById(R.id.informationTextView);
            String version = packageInfo.versionName;
            String s = String.format(getString(R.string.informationTextViewText), getString(R.string.app_name),version);
            textView.setText(s);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void goBack(View view) {
        finish();
    }
}