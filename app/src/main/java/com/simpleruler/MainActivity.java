/*  Ruler-caltor v2.00
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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class MainActivity extends AppCompatActivity {

    // Ruler related
    float finalYDpi = 400f;
    int maxScreenHeight;
    static boolean calibrated = false;
    static boolean rulerHead;
    static boolean hasSound;
    static boolean thickLine;
    static boolean shortFormUnit;
    static String inchForm, cmForm, mmForm;
    static int rulerColor, numberColor;
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
                finalYDpi = sharedPref.getFloat(getString(R.string.pref_final_y_dpi_key), 400);
                rulerColor = sharedPref.getInt(getString(R.string.pref_ruler_color_key), getResources().getColor(R.color.white));
                numberColor = sharedPref.getInt(getString(R.string.pref_number_color_key), getResources().getColor(R.color.black));
                rulerHead = sharedPref.getBoolean(getString(R.string.pref_ruler_head_key), true);
                hasSound = sharedPref.getBoolean(getString(R.string.pref_sound_key), true);
                thickLine = sharedPref.getBoolean(getString(R.string.pref_thick_lines_key), false);
                shortFormUnit = sharedPref.getBoolean(getString(R.string.pref_short_form_unit_key), true);
                guidingLines = sharedPref.getBoolean(getString(R.string.pref_guiding_lines_key), true);
                decimalPlace = sharedPref.getInt(getString(R.string.pref_decimal_place_key), 1);
                metricCM = sharedPref.getBoolean(getString(R.string.pref_metric_cm_key), true);
                Line1.setButtonY(maxScreenHeight / 2f - Line1.getButtonHeight(this) * 2);
                Line2.setButtonY(maxScreenHeight / 2f);
            }
        }
        setShortForm();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setTextColor();
                setRuler();
            }
        }, 100);
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
            maxScreenHeight = displayMetrics.heightPixels + 50;
            Line1.setMax(maxScreenHeight);
            Line2.setMax(maxScreenHeight);
            // Measurement line 1
            guidingLineView1.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        stopSlidingSound();
                        return true;
                    }
                    float touchY = event.getY();
                    Line1.setButtonY(guidingLineView1.getY());
                    Line2.setButtonY(guidingLineView2.getY());
                    Line1.setEventY(touchY);
                    if (touchY > 3f || touchY < -3f) {
                        playSlidingSound();
                        float lineY = Line1.newLineY();
                        if (Math.abs(touchY - Line1.getButtonHeight(MainActivity.this)) < 3) {
                            stopSlidingSound();
                        }
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
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        stopSlidingSound();
                        return true;
                    }
                    float touchY = event.getY();
                    Line1.setButtonY(guidingLineView1.getY());
                    Line2.setButtonY(guidingLineView2.getY());
                    Line2.setEventY(touchY);
                    if (touchY > 3f || touchY < -3f) {
                        playSlidingSound();
                        float lineY = Line2.newLineY();
                        if (Math.abs(touchY - Line2.getButtonHeight(MainActivity.this)) < 3) {
                            stopSlidingSound();
                        }
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
        finalYDpi = (int) autoCalibrate(this);
        rulerColor = getResources().getColor(R.color.white);
        numberColor = getResources().getColor(R.color.black);
        calibrated = true;
        rulerHead = true;
        hasSound = true;
        thickLine = false;
        shortFormUnit = true;
        guidingLines = true;
        decimalPlace = 1;
        metricCM = true;
        // Save variables
        SharedPreferences.Editor editor = initPref.edit();
        editor.putBoolean(getString(R.string.pref_calibrated_key), calibrated);
        editor.putFloat(getString(R.string.pref_final_y_dpi_key), finalYDpi);
        editor.putInt(getString(R.string.pref_ruler_color_key), rulerColor);
        editor.putInt(getString(R.string.pref_number_color_key), numberColor);
        editor.putBoolean(getString(R.string.pref_ruler_head_key), rulerHead);
        editor.putBoolean(getString(R.string.pref_sound_key), hasSound);
        editor.putBoolean(getString(R.string.pref_thick_lines_key), thickLine);
        editor.putBoolean(getString(R.string.pref_short_form_unit_key), shortFormUnit);
        editor.putBoolean(getString(R.string.pref_guiding_lines_key), true);
        editor.putInt(getString(R.string.pref_decimal_place_key), 1);
        editor.putBoolean(getString(R.string.pref_metric_cm_key), true);
        editor.apply();
        // Display rulers and measurement lines
        setShortForm();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Line1.setButtonY(maxScreenHeight / 2f - Line1.getButtonHeight(MainActivity.this) * 2);
                Line2.setButtonY(maxScreenHeight / 2f);
                showGuidingLines();
            }
        }, 100);

    }

    double autoCalibrate(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Toast.makeText(this, R.string.autoCalibrateText, Toast.LENGTH_SHORT).show();
        //return (30 / 2.54) * displayMetrics.ydpi;
        return displayMetrics.ydpi;
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
            // Change color
            if (resultCode == 10) {
                setTextColor();
                setRuler();
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.pref_ruler_color_key), rulerColor);
                editor.putInt(getString(R.string.pref_number_color_key), numberColor);
                editor.apply();
                openOption();
            }
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
            //  Change sound
            if (resultCode == 12) {
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.pref_sound_key), hasSound);
                editor.apply();
                openOption();
            }
            //  Switch lines thickness
            if (resultCode == 3) {
                setRuler();
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.pref_thick_lines_key), thickLine);
                editor.apply();
                openOption();
            }
            // Choose short form unit
            if (resultCode == 8) {
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.pref_short_form_unit_key), shortFormUnit);
                editor.apply();
                setShortForm();
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
                setRuler();
                openOption();
            }
            // Clear data
            if (resultCode == 7) {
                dataGroup.removeAllViews();
                dataIndex = -1;
            }
            //  Reset
            if (resultCode == 9) {
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                initRuler(sharedPref);
                setTextColor();
                setRuler();
                Toast.makeText(this, R.string.resetToastText, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setTextColor() {
        int[] viewInt = {R.id.calibrateTextView, R.id.inchTextView, R.id.cmTextView, R.id.tapTextView,
                R.id.guidingLineInchText, R.id.guidingLineCMText};
        for (int i : viewInt) {
            TextView textView = findViewById(i);
            textView.setTextColor(numberColor);
        }
    }

    private void setShortForm() {
        if (shortFormUnit) {
            inchForm = getString(R.string.inchShortFormText);
            cmForm = getString(R.string.cmShortFormText);
            mmForm = getString(R.string.mmShortFormText);
        } else {
            inchForm = getString(R.string.inchTextViewText);
            cmForm = getString(R.string.cmTextViewText);
            mmForm = getString(R.string.mmTextViewText);
        }
    }

    // Calibrate
    public void scaleUp(View view) {
        finalYDpi += 1f;
        if (finalYDpi > 1000f) {
            finalYDpi = 1000f;
        }
        setRuler();
    }

    public void scaleDown(View view) {
        finalYDpi -= 1f;
        if (finalYDpi < 100f) {
            finalYDpi = 100f;
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
        editor.putFloat(getString(R.string.pref_final_y_dpi_key), finalYDpi);
        editor.apply();
        if (guidingLines) {
            Line1.newLineY();
            Line2.newLineY();
            showGuidingLines();
        }
    }

    private void setRuler() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        ImageView rulerImage = findViewById(R.id.rulerImageView);
        rulerImage.setBackgroundColor(rulerColor);
        Bitmap rulerBitmap = Bitmap.createBitmap(rulerImage.getWidth(), rulerImage.getHeight(), Bitmap.Config.ARGB_8888);
        Paint rulerPaint = new Paint();
        Canvas rulerCanvas = new Canvas(rulerBitmap);
        rulerPaint.setColor(numberColor);
        float rulerHeadLength = 0f;
        if (rulerHead) {
            rulerHeadLength = 50f;
        }
        rulerPaint.setTextSize(60);
        rulerPaint.setColor(numberColor);
        float lineLength;
        int screenHeight = displayMetrics.heightPixels;
        // Draw inch
        rulerPaint.setTextAlign(Paint.Align.LEFT);
        float lineY = 0f;
        int lineCount = 0;
        do {
            if (lineCount % 16 == 0) {
                lineLength = 100;
                rulerCanvas.drawText(String.valueOf(lineCount / 16), 110, lineY + rulerHeadLength + 20, rulerPaint);
            } else {
                if (lineCount % 8 == 0) {
                    lineLength = 90;
                } else {
                    if (lineCount % 4 == 0) {
                        lineLength = 70;
                    } else {
                        if (lineCount % 2 == 0) {
                            lineLength = 50;
                        } else {
                            lineLength = 40;
                        }
                    }
                }
            }
            if (thickLine) {
                rulerCanvas.drawRect(0, lineY + rulerHeadLength - 1, lineLength, lineY + rulerHeadLength + 1, rulerPaint);
            } else {
                rulerCanvas.drawLine(0, lineY + rulerHeadLength, lineLength, lineY + rulerHeadLength, rulerPaint);
            }
            lineY += finalYDpi / 16f;
            lineCount++;
        } while (lineY < screenHeight + 50);
        // Draw cm/mm
        rulerPaint.setTextAlign(Paint.Align.RIGHT);
        lineY = 0f;
        lineCount = 0;
        int screenWidth = displayMetrics.widthPixels;
        do {
            if (lineCount % 10 == 0) {
                lineLength = 100;
                if (metricCM) {
                    rulerCanvas.drawText(String.valueOf(lineCount / 10), screenWidth - 110, lineY + rulerHeadLength + 20, rulerPaint);
                } else {
                    rulerCanvas.drawText(String.valueOf(lineCount), screenWidth - 110, lineY + rulerHeadLength + 20, rulerPaint);
                }
            } else {
                if (lineCount % 5 == 0) {
                    lineLength = 90;
                } else {
                    lineLength = 70;
                }
            }
            if (thickLine) {
                rulerCanvas.drawRect(screenWidth - lineLength, lineY + rulerHeadLength - 1, screenWidth, lineY + rulerHeadLength + 1, rulerPaint);
            } else {
                rulerCanvas.drawLine(screenWidth - lineLength, lineY + rulerHeadLength, screenWidth, lineY + rulerHeadLength, rulerPaint);
            }
            lineY += finalYDpi / 25.4f;
            lineCount++;
        } while (lineY < screenHeight + 50);
        rulerImage.setImageBitmap(rulerBitmap);
        // Show calibration ratio
        TextView textView = findViewById(R.id.calibrateTextView);
        String s = getString(R.string.calibrateText) + finalYDpi;
        textView.setText(s);
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
        measuredLength = (Math.abs(Line1.getY() - Line2.getY())) * 2.54f / finalYDpi;
        if (metricCM) {
            lengthStringMetric = String.format("%." + decimalPlace + "f " + cmForm, measuredLength);
        } else {
            lengthStringMetric = String.format("%." + decimalPlace + "f " + mmForm, measuredLength * 10);
        }
        TextView cmView = findViewById(R.id.guidingLineCMText);
        cmView.setText(lengthStringMetric);
        lengthStringInch = String.format("%." + decimalPlace + "f " + inchForm, measuredLength / 2.54);
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
                    System.arraycopy(dataValue, dataGroup.indexOfChild(chip) + 1, dataValue,
                            dataGroup.indexOfChild(chip), dataGroup.getChildCount() - 1 - dataGroup.indexOfChild(chip));
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


    MediaPlayer slidingMediaPlayer;

    public void playSlidingSound() {
        if (hasSound) {
            if (slidingMediaPlayer == null) {
                slidingMediaPlayer = MediaPlayer.create(this, R.raw.sliding);
                slidingMediaPlayer.setLooping(true);
                slidingMediaPlayer.start();
            }
        }
    }

    public void stopSlidingSound() {
        if (hasSound) {
            if (slidingMediaPlayer != null) {
                slidingMediaPlayer.stop();
                slidingMediaPlayer.release();
                slidingMediaPlayer = null;
            }
        }
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
            if (realY < 50f) {
                realY = 50f;
            }
        } else if (realY < 0f) {
            realY = 0f;
        }
        if (realY > maxY) {
            realY = maxY;
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