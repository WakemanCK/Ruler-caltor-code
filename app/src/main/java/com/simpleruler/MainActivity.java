/*  Ruler (with Calc) v1.03a
    Wakeman Chau
    hauwingstudio@hotmail.com
    © 2020
    All rights reserved
 */

package com.simpleruler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    // Ruler related
    public static final int RULER_IMAGE_HEIGHT = 3000;
    final int RULER_IMAGE_WIDTH = 300;
    static int rulerHeight, rulerWidth;
    int maxScreenHeight;
    static boolean calibrated = false;
    static boolean rulerHead;
    static boolean thickline;
    static String rulerImage = "rulerthin";
    static String rulerInchImage = "rulerinchthin";
    static String calibrateTextStatic;
    // Measurement lines related
    static boolean guidingLines;
    static int decimalPlace;
    static boolean metricCM;
    View guidingLineView1;
    View guidingLineView2;
    GuidingLine Line1 = new GuidingLine();
    GuidingLine Line2 = new GuidingLine();
    // Data group related
    float[] dataValue = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1}; //new float[10];
    int dataIndex = -1;
    ChipGroup dataGroup;
    public static final String EXTRA_VALUE = "com.simpleruler.VALUE";
    public static final String EXTRA_INDEX = "com.simpleruler.INDEX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load settings
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        if (!calibrated) {
            calibrated = sharedPref.getBoolean(getString(R.string.pref_calibrated_key), false);
            if (!calibrated) {
                initRuler(sharedPref);
            } else {
                rulerHeight = sharedPref.getInt(getString(R.string.pref_ruler_height_key), RULER_IMAGE_HEIGHT);
                rulerHead = sharedPref.getBoolean(getString(R.string.pref_ruler_head_key), true);
                thickline = sharedPref.getBoolean(getString(R.string.pref_thick_lines_key), false);
                guidingLines = sharedPref.getBoolean(getString(R.string.pref_guiding_lines_key), true);
                decimalPlace = sharedPref.getInt(getString(R.string.pref_decimal_place_key), 1);
                metricCM = sharedPref.getBoolean(getString(R.string.pref_metric_cm_key), true);
                Line1.setButtonY(maxScreenHeight / 2f - Line1.getButtonHeight(this) * 2);
                Line2.setButtonY(maxScreenHeight / 2f);
            }
        }
        chooseRuler();
        setRuler();

        // Measurement Lines
        if (!guidingLines) {
            hideGuidingLines();
        } else {
            guidingLineView1 = findViewById(R.id.guidingButton1);
            guidingLineView2 = findViewById(R.id.guidingButton2);
            WindowManager windowManager = getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);
            maxScreenHeight = displayMetrics.heightPixels;
            Line1.setMax(maxScreenHeight);
            Line2.setMax(maxScreenHeight);
            // Measurement line 1
            guidingLineView1.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    float touchY = event.getY();
                    Line1.setButtonY(guidingLineView1.getY());
                    Line2.setButtonY(guidingLineView2.getY());
                    Line1.setEventY(touchY);
                    if (touchY > 3f || touchY < -3f) {
                        float lineY = Line1.newLineY();
                        view.setY(lineY - Line1.getButtonHeight(MainActivity.this));
                        view = findViewById(R.id.divider1);
                        view.setY(lineY);
                        view = findViewById(R.id.arrowHeadCMView1);
                        view.setY(lineY - view.getHeight() / 2f);
                        view = findViewById(R.id.arrowHeadInchView1);
                        view.setY(lineY - view.getHeight() / 2f);
                        printGuidingLineText();
                    }
                    return true;
                }
            });
            // Measurement line 2
            guidingLineView2.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    float touchY = event.getY();
                    Line1.setButtonY(guidingLineView1.getY());
                    Line2.setButtonY(guidingLineView2.getY());
                    Line2.setEventY(touchY);
                    if (touchY > 3f || touchY < -3f) {
                        float lineY = Line2.newLineY();
                        view.setY(lineY - Line2.getButtonHeight(MainActivity.this));
                        view = findViewById(R.id.divider2);
                        view.setY(lineY);
                        view = findViewById(R.id.arrowHeadCMView2);
                        view.setY(lineY - view.getHeight() / 2f);
                        view = findViewById(R.id.arrowHeadInchView2);
                        view.setY(lineY - view.getHeight() / 2f);
                        printGuidingLineText();
                    }
                    return true;
                }
            });
        }
        dataGroup = findViewById(R.id.dataChipGroup);
    } // End of onCreate

    // Init
    private void initRuler(final SharedPreferences initPref) {
        // Init variables
        rulerHeight = (int) autoCalibrate(this);
        calibrated = true;
        rulerHead = true;
        thickline = false;
        guidingLines = true;
        decimalPlace = 1;
        metricCM = true;
        // Save variables
        SharedPreferences.Editor editor = initPref.edit();
        editor.putInt(getString(R.string.pref_ruler_height_key), rulerHeight);
        editor.putBoolean(getString(R.string.pref_calibrated_key), true);
        editor.putBoolean(getString(R.string.pref_ruler_head_key), true);
        editor.putBoolean(getString(R.string.pref_thick_lines_key), false);
        editor.putBoolean(getString(R.string.pref_guiding_lines_key), true);
        editor.putInt(getString(R.string.pref_decimal_place_key), 1);
        editor.putBoolean(getString(R.string.pref_metric_cm_key), true);
        editor.apply();
        // Display rulers and measurement lines
        chooseRuler();
        findViewById(R.id.guidingButton1).postDelayed(new Runnable() {
            @Override
            public void run() {
                Line1.setButtonY(maxScreenHeight / 2f - Line1.getButtonHeight(MainActivity.this) * 2);
                Line2.setButtonY(maxScreenHeight / 2f);
                showGuidingLines();
            }
        }, 1000);

    }

    double autoCalibrate(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Toast.makeText(this, R.string.autoCalibrateText, Toast.LENGTH_SHORT).show();
        return (30 / 2.54) * displayMetrics.ydpi;
    }

    // Open Option page
    public void openOption(View view) {
        Line1.setButtonY(findViewById(R.id.guidingButton1).getY()); // needed to avoid bug
        Line2.setButtonY(findViewById(R.id.guidingButton2).getY()); // needed to avoid bug
        Intent intent = new Intent(this, Option.class);
        startActivityForResult(intent, 1); // Option Request Code = 1
    }

    public void openOption() {
        Intent intent = new Intent(this, Option.class);
        startActivityForResult(intent, 1); // Option Request Code = 1
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    // return from Option page (show Up Down Button)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Line1.newLineY(); // keep this to avoid line disappear bug
        Line2.newLineY(); // keep this to avoid line disappear bug
        if (guidingLines) {
            showGuidingLines();
        }
        if (requestCode == 1) {
            // Calibrate
            if (resultCode == 1) {
                int[] viewInt = {R.id.calibrateTextView, R.id.upButton, R.id.downButton, R.id.doneButton};
                for (int i : viewInt) {
                    findViewById(i).setVisibility(View.VISIBLE);
                }
                Button optionButton = findViewById(R.id.optionButton);
                optionButton.setVisibility(View.INVISIBLE);
                hideGuidingLines();
            }

            //  Switch head
            if (resultCode == 2) {
                setRuler();
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.pref_ruler_head_key), rulerHead);
                editor.apply();
                openOption();
            }

            //  Switch lines thickness
            if (resultCode == 3) {
                chooseRuler();
                setRuler();
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.pref_thick_lines_key), thickline);
                editor.apply();
                openOption();
            }

            //  Switch guiding lines presence
            if (resultCode == 4) {
                if (guidingLines) {
                    showGuidingLines();
                } else {
                    hideGuidingLines();
                }
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.pref_guiding_lines_key), guidingLines);
                editor.apply();
                openOption();
            }

            // Adjust decimal places
            if (resultCode == 5) {
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.pref_decimal_place_key), decimalPlace);
                editor.apply();
                printGuidingLineText();
                openOption();
            }

            // Adjust cm or mm
            if (resultCode == 6) {
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.pref_metric_cm_key), metricCM);
                editor.apply();
                printGuidingLineText();
                openOption();
            }

            //  Reset
            if (resultCode == 9) {
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                initRuler(sharedPref);
                setRuler();
                Toast.makeText(this, R.string.resetToastText, Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 2 && resultCode == 11) {
            dataGroup.removeAllViews();
            dataIndex = -1;
        }
    }

    // Calibrate
    public void scaleUp(View view) {
        rulerHeight += 10;
        if (rulerHeight > 10000) {
            rulerHeight = 10000;
        }
        setRuler();
    }

    public void scaleDown(View view) {
        rulerHeight -= 10;
        if (rulerHeight < 1000) {
            rulerHeight = 1000;
        }
        setRuler();
    }

    public void hideUpDownButton(View view) {
        int[] viewInt = {R.id.calibrateTextView, R.id.upButton, R.id.downButton, R.id.doneButton};
        for (int i : viewInt) {
            findViewById(i).setVisibility(View.INVISIBLE);
        }
        Button optionButton = findViewById(R.id.optionButton);
        optionButton.setVisibility(View.VISIBLE);
        // Save ruler height
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.pref_ruler_height_key), rulerHeight);
        editor.apply();
        if (guidingLines) {
            Line1.newLineY();
            Line2.newLineY();
            showGuidingLines();
        }
    }

    private void chooseRuler() {
        if (thickline) {
            rulerImage = "rulerthick";
            rulerInchImage = "rulerinchthick";
        } else {
            rulerImage = "rulerthin";
            rulerInchImage = "rulerinchthin";
        }
    }

    private void setRuler() {
        rulerWidth = rulerHeight * RULER_IMAGE_WIDTH / RULER_IMAGE_HEIGHT;
        ImageView[] imageView = new ImageView[3];
        InputStream inputStream = null;
        BitmapRegionDecoder decoder = null;
        int headAdjustment = 50;
        if (!rulerHead) {
            headAdjustment = 100;
        }
        // Adjust cm ruler
        imageView[0] = findViewById(R.id.rulerImageTop);
        imageView[1] = findViewById(R.id.rulerImageMiddle);
        imageView[2] = findViewById(R.id.rulerImageBottom);
        for (int i = 0; i < 3; i++) {
            try {
                inputStream = this.getContentResolver().openInputStream(Uri.parse("android.resource://" + getPackageName() + "/drawable/" + rulerImage));
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            try {
                decoder = BitmapRegionDecoder.newInstance(inputStream, false);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            Bitmap bitMap = null;
            switch (i) {
                case 0:
                    bitMap = decoder.decodeRegion(new Rect(1, headAdjustment, 300, 1000 + headAdjustment), null);
                    break;
                case 1:
                    bitMap = decoder.decodeRegion(new Rect(1, 1000 + headAdjustment, 300, 2000 + headAdjustment), null);
                    break;
                case 2:
                    bitMap = decoder.decodeRegion(new Rect(1, 2000 + headAdjustment, 300, 3000 + headAdjustment), null);
                    break;
                default:
                    break;
            }
            ViewGroup.LayoutParams params = imageView[i].getLayoutParams();
            params.height = rulerHeight / 3;
            params.width = rulerWidth;
            imageView[i].setLayoutParams(params);
            imageView[i].setImageBitmap(bitMap);
        }
        // Adjust inch ruler
        if (rulerHead) {
            headAdjustment = 61;
        }
        imageView[0] = findViewById(R.id.rulerInchImageTop);
        imageView[1] = findViewById(R.id.rulerInchImageMiddle);
        imageView[2] = findViewById(R.id.rulerInchImageBottom);
        for (int i = 0; i < 3; i++) {
            try {
                inputStream = this.getContentResolver().openInputStream(Uri.parse("android.resource://" + getPackageName() + "/drawable/" + rulerInchImage));
            } catch (FileNotFoundException e1) {
                Toast.makeText(this, "file not found", Toast.LENGTH_SHORT).show();
                e1.printStackTrace();
            }
            try {
                decoder = BitmapRegionDecoder.newInstance(inputStream, false);
            } catch (IOException e) {
                Toast.makeText(this, "file not found2 decoder", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            Bitmap bitMap = null;
            switch (i) {
                case 0:
                    bitMap = decoder.decodeRegion(new Rect(1, headAdjustment, 300, 1000 + headAdjustment), null);
                    break;
                case 1:
                    bitMap = decoder.decodeRegion(new Rect(1, 1000 + headAdjustment, 300, 2000 + headAdjustment), null);
                    break;
                case 2:
                    bitMap = decoder.decodeRegion(new Rect(1, 2000 + headAdjustment, 300, 3000 + headAdjustment), null);
                    break;
                default:
                    break;
            }


            ViewGroup.LayoutParams params = imageView[i].getLayoutParams();
            params.height = (int) (rulerHeight / 3 * 1.27);
            params.width = (int) (rulerWidth * 1.27);
            imageView[i].setLayoutParams(params);
            imageView[i].setImageBitmap(bitMap);
        }
        // Show calibration ratio
        TextView textView = findViewById(R.id.calibrateTextView);
        calibrateTextStatic = getString(R.string.calibrateText) + (rulerHeight / 10);
        textView.setText(calibrateTextStatic);
    }

    // Guiding Lines
    private void showGuidingLines() {
        // Guiding line 1
        ImageView gBView1 = findViewById(R.id.guidingButton1);
        gBView1.setY(Line1.getY() - Line1.getButtonHeight(this));
        findViewById(R.id.divider1).setY(Line1.getY());
        ImageView aHCView1 = findViewById(R.id.arrowHeadCMView1);
        aHCView1.setY(Line1.getY() - aHCView1.getHeight() / 2f);
        ImageView aHIView1 = findViewById(R.id.arrowHeadInchView1);
        aHIView1.setY(Line1.getY() - aHIView1.getHeight() / 2f);
        // Guiding line 2
        ImageView gBview2 = findViewById(R.id.guidingButton2);
        gBview2.setY(Line2.getY() - Line2.getButtonHeight(this));
        findViewById(R.id.divider2).setY(Line2.getY());
        ImageView aHCView2 = findViewById(R.id.arrowHeadCMView2);
        aHCView2.setY(Line2.getY() - aHCView2.getHeight() / 2f);
        ImageView aHIView2 = findViewById(R.id.arrowHeadInchView2);
        aHIView2.setY(Line2.getY() - aHIView2.getHeight() / 2f);
        printGuidingLineText();
        int[] viewInt = {R.id.guidingButton1, R.id.divider1, R.id.arrowHeadCMView1, R.id.arrowHeadInchView1,
                R.id.guidingButton2, R.id.divider2, R.id.arrowHeadCMView2, R.id.arrowHeadInchView2,
                R.id.guidingLineInchText, R.id.guidingLineCMText, R.id.tapTextView,
                R.id.dataChipGroup, R.id.calculatorButton};
        for (int i : viewInt) {
            findViewById(i).setVisibility(View.VISIBLE);
        }
    }

    private void hideGuidingLines() {
        int[] viewInt = {R.id.guidingButton1, R.id.divider1, R.id.arrowHeadCMView1, R.id.arrowHeadInchView1,
                R.id.guidingButton2, R.id.divider2, R.id.arrowHeadCMView2, R.id.arrowHeadInchView2,
                R.id.guidingLineInchText, R.id.guidingLineCMText, R.id.tapTextView,
                R.id.dataChipGroup, R.id.calculatorButton};
        for (int i : viewInt) {
            findViewById(i).setVisibility(View.INVISIBLE);
        }
    }

    float measuredLength; // in cm
    String lengthStringMetric;
    String lengthStringInch;

    private void printGuidingLineText() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        measuredLength = (Math.abs(Line1.getY() - Line2.getY())) * 30 / rulerHeight;
        if (metricCM) {
            lengthStringMetric = String.format("%." + decimalPlace + "f " + getString(R.string.cmTextViewText), measuredLength);
        } else {
            lengthStringMetric = String.format("%." + decimalPlace + "f " + getString(R.string.mmTextViewText), measuredLength * 10);
        }
        TextView cmView = findViewById(R.id.guidingLineCMText);
        cmView.setText(lengthStringMetric);
        lengthStringInch = String.format("%." + decimalPlace + "f " + getString(R.string.inTextViewText), measuredLength / 2.54);
        TextView inchView = findViewById(R.id.guidingLineInchText);
        inchView.setText(lengthStringInch);
        findViewById(R.id.tapTextView).setVisibility(View.VISIBLE);
    }


    // Data group
    public void saveInchData(View view) {
        if (lengthStringInch == null) {
            return;
        }
        if (dataIndex >= 9) {
            Toast.makeText(this, R.string.dataFullToastText, Toast.LENGTH_SHORT).show();
        } else {
            dataIndex += 1;
            dataValue[dataIndex] = measuredLength;
            addChip(lengthStringInch);
        }
    }

    public void saveCMData(View view) {
        if (lengthStringMetric == null) {
            return;
        }
        if (dataIndex >= 9) {
            Toast.makeText(this, R.string.dataFullToastText, Toast.LENGTH_SHORT).show();
        } else {
            dataIndex += 1;
            dataValue[dataIndex] = measuredLength;
            addChip(lengthStringMetric);
        }
    }

    private void addChip(String getText) {
        final Chip chip = new Chip(this);
        chip.setText(getText);
        chip.setCloseIconVisible(true);
        dataGroup.addView(chip);
        if (dataGroup.getHeight() > findViewById(R.id.tapTextView).getY() - 100) {
            dataGroup.setChipSpacingVertical(-10);
        } else {
            dataGroup.setChipSpacingVertical(0);
        }
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataGroup.getChildCount() - 1 - dataGroup.indexOfChild(chip) >= 0)
                    System.arraycopy(dataValue, dataGroup.indexOfChild(chip) + 1, dataValue, dataGroup.indexOfChild(chip), dataGroup.getChildCount() - 1 - dataGroup.indexOfChild(chip));
                dataGroup.removeView(chip);
                if (dataGroup.getHeight() < findViewById(R.id.tapTextView).getY() - 100) {
                    dataGroup.setChipSpacingVertical(0);
                }
                dataIndex -= 1;
            }
        });
    }

    public void openCalculator(View view) {
        Line1.setButtonY(findViewById(R.id.guidingButton1).getY()); // needed to avoid bug
        Line2.setButtonY(findViewById(R.id.guidingButton2).getY()); // needed to avoid bug
        Intent intent = new Intent(this, Calculator.class);
        intent.putExtra(EXTRA_VALUE, dataValue);
        intent.putExtra(EXTRA_INDEX, dataIndex);
        startActivityForResult(intent, 2); // Calculator Request Code = 2
    }

}

class GuidingLine {
    float realY;
    float realEventY;
    int buttonHeightGL;
    int maxY;

    GuidingLine() {
        realY = 1000;
        realEventY = 0;
        maxY = 2000;
        buttonHeightGL = 125;
    }

    public void setMax(int getMaxScreenHeight) {
        maxY = getMaxScreenHeight;
    }

    public void setButtonY(float getButtonY) {
        realY = getButtonY + buttonHeightGL;
    }

    public void setEventY(float getEventY) {
        realEventY = getEventY - buttonHeightGL;
    }

    public float getY() {
        return realY;
    }

    public float newLineY() {
        if ((realEventY > 0) && (realY < maxY)) {
            if (realEventY > 30f) {
                realY = realY + 30f;
            } else if (realEventY > 10f) {
                realY = realY + 10f;
            } else if (realEventY > 3f) {
                realY = realY + 3f;
            }
        }
        if (realEventY < 0 && realY > 0) {
            if (realEventY < -30f) {
                realY = realY - 30f;
            } else if (realEventY < -10f) {
                realY = realY - 10f;
            } else if (realEventY < -3f) {
                realY = realY - 3f;
            }
        }
        if (MainActivity.rulerHead) {
            float headPosition = MainActivity.rulerHeight * 50f / MainActivity.RULER_IMAGE_HEIGHT;
            if (realY < headPosition) {
                realY = headPosition;
            }
        } else if (realY < 0) {
            realY = 0f;
        }
        float endPosition = MainActivity.rulerHeight * (float) maxY / MainActivity.RULER_IMAGE_HEIGHT;
        if (realY > endPosition) {
            realY = endPosition;
        }
        realEventY = 0;
        return realY;
    }

    public int getButtonHeight(Activity activity) {
        if (buttonHeightGL == 0) {
            buttonHeightGL = activity.findViewById(R.id.guidingButton1).getHeight() / 2;
        }
        return buttonHeightGL;
    }
}