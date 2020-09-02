package com.simpleruler;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.simpleruler.MainActivity.decimalPlace;

public class Calculator extends AppCompatActivity {
    float[] dataValue;
    int dataUnit = 2; // 0 = inch; 1 = mm; 2 = cm
    int dataIndex = 0;
    ChipGroup dataGroup;
    private FrameLayout adContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        // Load Ad
        adContainerView = findViewById(R.id.adFrameLayout);
        Display display = getWindowManager().getDefaultDisplay();
        GMS gmsAd = new GMS();
        gmsAd.init(this, adContainerView, display);

        // Unit selection
        final RadioGroup unitGroup = findViewById(R.id.unitRadioGroup);
        highLight(dataUnit);
        unitGroup.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int childIndex) {
                        hideSoftKeyboard(Calculator.this);
                        int newIndex = radioGroup.indexOfChild(findViewById(childIndex));
                        highLight(newIndex);
                        final TextView customUnit = findViewById(R.id.customUnitView);
                        int oldUnit = dataUnit;
                        dataUnit = newIndex;
                        // set custom value unit
                        EditText editText = findViewById(R.id.customValueEditText);
                        String customValue = editText.getText().toString();
                        if (testValidFloat(customValue)) {
                            float newValue = convertInchMMCM(oldUnit, dataUnit, Float.parseFloat(customValue));
                            editText.setText(String.valueOf(newValue));
                        }
                        switch (dataUnit) { // 0 = inch; 1 = mm; 2 = cm
                            case 0:
                                customUnit.setText(R.string.inTextViewText);
                                break;
                            case 1:
                                customUnit.setText(R.string.mmTextViewText);
                                break;
                            case 2:
                                customUnit.setText(R.string.cmTextViewText);
                                break;
                            default:
                                break;
                        }
                        // set data unit
                        printDataChip(dataGroup);
                    }
                }
        );
        // Get data
        Intent intent = getIntent();
        dataValue = intent.getFloatArrayExtra(MainActivity.EXTRA_VALUE);
        dataIndex = intent.getIntExtra(MainActivity.EXTRA_INDEX, 0);
        dataGroup = findViewById(R.id.dataChipGroup);
        printDataChip(dataGroup);
        // Setup equation
        final Chip equationChip1 = findViewById(R.id.equationChip1);
        final Chip equationChip2 = findViewById(R.id.equationChip2);
        final Chip equationChip3 = findViewById(R.id.equationChip3);
        equationChip1.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                equationChip1.setText(getString(R.string.fiveEmptySpaceText));
            }
        });
        equationChip2.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                equationChip2.setText(getString(R.string.oneEmptySpaceText));
            }
        });
        equationChip3.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                equationChip3.setText(getString(R.string.fiveEmptySpaceText));
            }
        });
        int[] viewInt = {R.id.calculatorParent, R.id.dataScrollView, R.id.dataChipGroup, R.id.customValueTextView, R.id.customUnitView};
        for (int i : viewInt) {
            setupHideKeyboardListener(findViewById(i));
        }
    }// end of OnCreate

    private void highLight(int getIndex) {
        RadioGroup radioGroup = findViewById(R.id.unitRadioGroup);
        for (int i = 0; i < 3; i++) {
            if (i == getIndex) {
                radioGroup.getChildAt(i).setBackgroundColor(Color.BLACK);
            } else {
                radioGroup.getChildAt(i).setBackgroundColor(Color.GRAY);
            }
        }
    }

    // Change units
    private float convertInchMMCM(int getOld, int getNew, float getValue) {
        float f = 0f;
        switch (getOld) { // 0 = inch; 1 = mm; 2 = cm
            case 0:
                if (getNew == 1) {
                    f = getValue * 25.4f;
                } else {
                    f = getValue * 2.54f;
                }
                break;
            case 1:
                if (getNew == 2) {
                    f = getValue / 10f;
                } else {
                    f = getValue / 25.4f;
                }
                break;
            case 2:
                if (getNew == 1) {
                    f = getValue * 10f;
                } else {
                    f = getValue / 2.54f;
                }
                break;
            default:
                break;
        }
        return f;
    }

    private String convertNumberWithUnit(float getValue) {
        String convertedText = "";
        switch (dataUnit) { // 0 = inch; 1 = mm; 2 = cm
            case 0:
                convertedText = String.format("%." + decimalPlace + "f " + getString(R.string.inTextViewText), getValue / 2.54);
                break;
            case 1:
                convertedText = String.format("%." + decimalPlace + "f " + getString(R.string.mmTextViewText), getValue * 10);
                break;
            case 2:
                convertedText = String.format("%." + decimalPlace + "f " + getString(R.string.cmTextViewText), getValue);
            default:
                break;
        }
        return convertedText;
    }

    private String convertNumber(float getValue) {
        String convertedText = "";
        switch (dataUnit) { // 0 = inch; 1 = mm; 2 = cm
            case 0:
                convertedText = String.format("%." + decimalPlace + "f", getValue / 2.54);
                break;
            case 1:
                convertedText = String.format("%." + decimalPlace + "f", getValue * 10);
                break;
            case 2:
                convertedText = String.format("%." + decimalPlace + "f", getValue);
            default:
                break;
        }
        return convertedText;
    }

    // Data related
    private void printDataChip(ChipGroup getGroup) {
        getGroup.removeAllViews();
        for (int i = 0; i <= dataIndex; i++) {
            addDataChip(dataValue[i], getGroup);
        }
    }

    private void addDataChip(float getValue, ChipGroup getGroup) {
        final Chip newChip = new Chip(this);
        final String chipText = convertNumberWithUnit(getValue);
        final String equationText = convertNumber(getValue);
        newChip.setText(chipText);
        getGroup.addView(newChip);
        newChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(Calculator.this);
                Chip equationChip1 = findViewById(R.id.equationChip1);
                Chip equationChip3 = findViewById(R.id.equationChip3);
                if (equationChip1.getText() == getString(R.string.fiveEmptySpaceText)) {
                    equationChip1.setText(equationText);
                } else {
                    equationChip3.setText(equationText);
                }
                checkEquation();
            }
        });
    }

    // Custom value related
    public void setupHideKeyboardListener(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(Calculator.this);
                return false;
            }
        });
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e3) {
            e3.printStackTrace();
        }
    }

    public void addCustom(View view) {
        hideSoftKeyboard(Calculator.this);
        EditText editText = findViewById(R.id.customValueEditText);
        String customValue = String.valueOf(editText.getText());
        if (!testValidFloat(customValue)) {
            return;
        }
        Chip equationChip1 = findViewById(R.id.equationChip1);
        Chip equationChip3 = findViewById(R.id.equationChip3);
        if (equationChip1.getText() == getString(R.string.fiveEmptySpaceText)) {
            equationChip1.setText(customValue);
        } else {
            equationChip3.setText(customValue);
        }
        checkEquation();
    }

    private boolean testValidFloat(String getString) {
        try {
            Float.parseFloat(getString);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    // Add operators
    public void addAddition(View view) {
        hideSoftKeyboard(Calculator.this);
        Chip equationChip2 = findViewById(R.id.equationChip2);
        equationChip2.setText("+");
        checkEquation();
    }

    public void addSubtraction(View view) {
        hideSoftKeyboard(Calculator.this);
        Chip equationChip2 = findViewById(R.id.equationChip2);
        equationChip2.setText("-");
        checkEquation();
    }

    public void addMultiplication(View view) {
        hideSoftKeyboard(Calculator.this);
        Chip equationChip2 = findViewById(R.id.equationChip2);
        equationChip2.setText("×");
        checkEquation();
    }

    public void addDivision(View view) {
        hideSoftKeyboard(Calculator.this);
        Chip equationChip2 = findViewById(R.id.equationChip2);
        equationChip2.setText("÷");
        checkEquation();
    }

    // Calculate
    private void checkEquation() {
        final Chip equationChip1 = findViewById(R.id.equationChip1);
        final Chip equationChip2 = findViewById(R.id.equationChip2);
        final Chip equationChip3 = findViewById(R.id.equationChip3);
        final ChipGroup equationGroup = findViewById(R.id.equationChipGroup);
        if (equationChip1.getText() == getString(R.string.fiveEmptySpaceText) ||
                equationChip2.getText() == getString(R.string.oneEmptySpaceText) ||
                equationChip3.getText() == getString(R.string.fiveEmptySpaceText)) {
            return;
        }
        final BigDecimal num1 = new BigDecimal(equationChip1.getText().toString());
        final String operator = equationChip2.getText().toString();
        final BigDecimal num2 = new BigDecimal(equationChip3.getText().toString());
        equationChip1.setCloseIconVisible(false);
        equationChip2.setCloseIconVisible(false);
        equationChip3.setCloseIconVisible(false);
        ObjectAnimator animation = ObjectAnimator.ofFloat(equationGroup, "translationY",
                -findViewById(R.id.equationDivider).getY());
        animation.setDuration(300);
        int[] viewInt = {R.id.answerTextView, R.id.equalTextView, R.id.answerButton, R.id.copyAnswerButton};
        for (int i : viewInt) {
            findViewById(i).setVisibility(View.INVISIBLE);
        }
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                String equationText = num1 + getString(R.string.fiveEmptySpaceText) + getString(R.string.fiveEmptySpaceText) +
                        operator + getString(R.string.fiveEmptySpaceText) + getString(R.string.fiveEmptySpaceText) +
                        num2 + getString(R.string.fiveEmptySpaceText);
                TextView equationView = findViewById(R.id.answerTextView);
                equationView.setText(equationText);
                BigDecimal answer = BigDecimal.ZERO;
                try {
                    switch (operator) {
                        case "+":
                            answer = num1.add(num2);
                            break;
                        case "-":
                            answer = num1.subtract(num2);
                            break;
                        case "×":
                            answer = num1.multiply(num2);
                            break;
                        case "÷":
                            answer = num1.divide(num2, 4, RoundingMode.HALF_UP);
                            break;
                        default:
                            break;
                    }
                } catch (ArithmeticException e) {
                    Toast.makeText(getApplicationContext(), R.string.errorText, Toast.LENGTH_SHORT).show();
                    equationChip1.setText(R.string.fiveEmptySpaceText);
                    equationChip2.setText(R.string.oneEmptySpaceText);
                    equationChip3.setText(R.string.fiveEmptySpaceText);
                    equationChip1.setCloseIconVisible(true);
                    equationChip2.setCloseIconVisible(true);
                    equationChip3.setCloseIconVisible(true);
                    equationGroup.setY(findViewById(R.id.equationDivider).getY());
                    return;
                }
                answer = answer.stripTrailingZeros();
                Button answerButton = findViewById(R.id.answerButton);
                answerButton.setText(answer.toPlainString());
                int[] viewInt = {R.id.answerTextView, R.id.equalTextView, R.id.answerButton, R.id.copyAnswerButton};
                for (int i : viewInt) {
                    findViewById(i).setVisibility(View.VISIBLE);
                    equationChip1.setText(R.string.fiveEmptySpaceText);
                    equationChip2.setText(R.string.oneEmptySpaceText);
                    equationChip3.setText(R.string.fiveEmptySpaceText);
                    equationChip1.setCloseIconVisible(true);
                    equationChip2.setCloseIconVisible(true);
                    equationChip3.setCloseIconVisible(true);
                    equationGroup.setY(findViewById(R.id.equationDivider).getY());
                }
            }
        });
        animation.start();
    }

    public void sumAll(View view) {
        final TextView textView = findViewById(R.id.answerTextView);
        final Button answerButton = findViewById(R.id.answerButton);

        final View animeView = findViewById(R.id.animeScrollView);
        ChipGroup animeGroup = findViewById(R.id.animeChipGroup);
        float targetY = -animeView.getY() * 2f - 100f;
        printDataChip(animeGroup);
        animeView.setY(findViewById(R.id.dataScrollView).getY());
        animeView.setScaleY(1);
        animeView.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofFloat(animeView, "translationY", targetY);
        ObjectAnimator animationHeight = ObjectAnimator.ofFloat(animeView, "scaleY", 0);
        animation.setDuration(600);
        animationHeight.setDuration(600);
        int[] viewInt = {R.id.answerTextView, R.id.equalTextView, R.id.answerButton, R.id.copyAnswerButton};
        for (int i : viewInt) {
            findViewById(i).setVisibility(View.INVISIBLE);
        }
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                BigDecimal sum = BigDecimal.ZERO;
                String s;
                for (int i = 0; i < dataGroup.getChildCount(); i++) {
                    s = convertNumber(dataValue[i]);
                    sum = sum.add(new BigDecimal(s));
                }
                sum = sum.stripTrailingZeros();
                textView.setText(R.string.sumAllAnswerText);
                answerButton.setText(sum.toPlainString());
                int[] viewInt = {R.id.answerTextView, R.id.equalTextView, R.id.answerButton, R.id.copyAnswerButton};
                for (int i : viewInt) {
                    findViewById(i).setVisibility(View.VISIBLE);
                }
                animeView.setVisibility(View.INVISIBLE);
                animeView.setY(findViewById(R.id.dataScrollView).getY());
                animeView.setScaleY(1);
            }
        });
        animation.start();
        animationHeight.start();
    }

    // Others
    public void clearAll(View view) {
        Intent intent = new Intent();
        setResult(11, intent);  // Request 2 result 11 = clear all
        finish();
    }

    public void addAnswer(View view) {
        Button answerButton = findViewById(R.id.answerButton);
        String answerValue = String.valueOf(answerButton.getText());
        Chip equationChip1 = findViewById(R.id.equationChip1);
        Chip equationChip3 = findViewById(R.id.equationChip3);
        if (equationChip1.getText() == getString(R.string.fiveEmptySpaceText)) {
            equationChip1.setText(answerValue);
        } else {
            equationChip3.setText(answerValue);
        }
        checkEquation();
    }

    // Copy to clipboard
    public void copyDataToClip(View view) {
        hideSoftKeyboard(Calculator.this);
        StringBuilder dataText = new StringBuilder();
        for (int i = 0; i < dataGroup.getChildCount(); i++) {
            Chip chip = (Chip) dataGroup.getChildAt(i);
            dataText.append(chip.getText()).append("\n");
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("data text", dataText.toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(Calculator.this, R.string.copyClipToastText, Toast.LENGTH_SHORT).show();
    }

    public void copyAnswerToClip(View view) {
        hideSoftKeyboard(Calculator.this);
        Button answerButton = findViewById(R.id.answerButton);
        String answerText = answerButton.getText() + "\n";
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("data text", answerText);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(Calculator.this, R.string.copyClipToastText, Toast.LENGTH_SHORT).show();
    }
}
