package com.simpleruler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.ULocale;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import static com.simpleruler.MainActivity.decimalPlace;
import static com.simpleruler.MainActivity.guidingLines;
import static com.simpleruler.MainActivity.metricCM;
import static com.simpleruler.MainActivity.rulerHead;
import static com.simpleruler.MainActivity.shortFormUnit;
import static com.simpleruler.MainActivity.thickline;


public class Option extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels * 0.7);
        int height = (int) (displayMetrics.heightPixels * 0.09f * (13f - displayMetrics.heightPixels / displayMetrics.ydpi));
        getWindow().setLayout(width, height);

        // Init
        if (!String.valueOf(java.util.Locale.getDefault()).contains("zh")){
            findViewById(R.id.shortFormUnitCheckBox).setVisibility(View.GONE);
        }
        // Head switch
        CheckBox toggleHead = findViewById(R.id.headCheckBox);
        toggleHead.setChecked(rulerHead);
        toggleHead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rulerHead = buttonView.isChecked();
                Intent intent = new Intent();
                setResult(2, intent);    // Request 1 result 2 = switch head
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        // Thick lines switch
        CheckBox toggleThickLines = findViewById(R.id.thickLinesCheckBox);
        toggleThickLines.setChecked(thickline);
        toggleThickLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                thickline = buttonView.isChecked();
                Intent intent = new Intent();
                setResult(3, intent);   // Request 1 result 3 = switch lines thickness
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        // Use shortform unit switch
        CheckBox toggleShortFormUnit = findViewById(R.id.shortFormUnitCheckBox);
        toggleShortFormUnit.setChecked(shortFormUnit);
        toggleShortFormUnit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                shortFormUnit = buttonView.isChecked();
                Intent intent = new Intent();
                setResult(8, intent);   // Request 1 result 8 = switch shortform unit
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        // Guiding Lines switch
        CheckBox toggleGuidingLines = findViewById(R.id.guidingLinesCheckBox);
        toggleGuidingLines.setChecked(guidingLines);
        toggleGuidingLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                guidingLines = buttonView.isChecked();
                Intent intent = new Intent();
                setResult(4, intent);   // Request 1 result 4 = switch guiding lines presence
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        // Seek bar and metric buttons
        SeekBar seekBar = findViewById(R.id.decimalPlaceBar);
        seekBar.setProgress(decimalPlace);
        TextView decimalPlaceText = findViewById(R.id.decimalPlaceTextView);
        RadioButton cmRadioButton = findViewById(R.id.cmButton);
        RadioButton mmRadioButton = findViewById(R.id.mmButton);
        TextView metricText = findViewById(R.id.metricTextView);
        String s = (getString(R.string.decimalPlaceText) + decimalPlace);
        decimalPlaceText.setText(s);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                decimalPlace = progress;
                TextView textView = findViewById(R.id.decimalPlaceTextView);
                String s = (getString(R.string.decimalPlaceText) + decimalPlace);
                textView.setText(s);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Intent intent = new Intent();
                setResult(5, intent);   // Request 1 result 5 = adjust decimal places
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        if (metricCM) {
            cmRadioButton.setChecked(true);
        } else {
            mmRadioButton.setChecked(true);
        }
        if (guidingLines) {
            seekBar.setEnabled(true);
            decimalPlaceText.setTextColor(Color.BLACK);
            cmRadioButton.setEnabled(true);
            mmRadioButton.setEnabled(true);
            metricText.setTextColor(Color.BLACK);
        } else {
            seekBar.setEnabled(false);
            decimalPlaceText.setTextColor(Color.GRAY);
            cmRadioButton.setEnabled(false);
            mmRadioButton.setEnabled(false);
            metricText.setTextColor(Color.GRAY);
        }
    }

    public void clearAll(View view) {
        Intent intent = new Intent();
        setResult(7, intent);  // Request 1 result 7 = clear data
        finish();
    }

    public void chooseCM(View view) {
        metricCM = true;
        Intent intent = new Intent();
        setResult(6, intent);   // Request 1 result 6 = pick cm/mm
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void chooseMM(View view) {
        metricCM = false;
        Intent intent = new Intent();
        setResult(6, intent);   // Request 1 result 6 = pick cm/mm
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void goCalibrate(View view) {
        Intent intent = new Intent();
        setResult(1, intent);  // Request 1 result 1 = calibrate
        finish();
    }


    public void showResetPopup(View view) {
        Intent intent = new Intent(this, ResetPopup.class);
        startActivityForResult(intent, 2); // Reset popup Request Code = 2
    }

    public void showInformationPopup(View view) {
        Intent intent = new Intent(this, InformationPopup.class);
        startActivity(intent);
    }

    // Reset preferences
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();
            setResult(9, intent); // request 1 result 9 = reset
            finish();
        }
    }
}