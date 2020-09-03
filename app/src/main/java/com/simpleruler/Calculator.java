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
    String eqString = "0";
    String eqSubstring, eqNum1String, eqNum2String, eqOperator;
    BigDecimal eqNum1, eqNum2;
    boolean eqEnteringNum1 = true;
    boolean eqNumHasDot = false;
    TextView equationView, answerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        // Load Ad
        FrameLayout adContainerView = findViewById(R.id.adFrameLayout);
        Display display = getWindowManager().getDefaultDisplay();
        GMS gmsAd = new GMS();
        gmsAd.init(this, adContainerView, display);

        // Init and unit selection
        final TextView equationView = findViewById(R.id.equationTextView);
        final TextView answerView = findViewById(R.id.answerTextView);
        final RadioGroup unitGroup = findViewById(R.id.unitRadioGroup);
        highLight(dataUnit);
        unitGroup.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int childIndex) {
                        dataUnit = radioGroup.indexOfChild(findViewById(childIndex));
                        highLight(dataUnit);
                       
                        // debug renew dataList
                    }
                }
        );
        // Get data
        Intent intent = getIntent();
        dataValue = intent.getFloatArrayExtra(MainActivity.EXTRA_VALUE);
        dataIndex = intent.getIntExtra(MainActivity.EXTRA_INDEX, 0);
        
        // debug fill datalistview
        
    }// end of OnCreate

    private void highLight(int getIndex) {
        RadioGroup radioGroup = findViewById(R.id.unitRadioGroup);
        for (int i = 0; i < 3; i++) {
            if (i == getIndex) {
                radioGroup.getChildAt(i).setBackgroundColor(getColor(R.color.colorList));
            } else {
                radioGroup.getChildAt(i).setBackgroundColor(getColor(R.color.colorListDark));
            }
        }
    }

    // Change units
 /*   private float convertInchMMCM(int getOld, int getNew, float getValue) {
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
    } */

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

/*    private String convertNumber(float getValue) {
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
    } */

    // Data related
/*    private void printDataList(ChipGroup getGroup) {
        getGroup.removeAllViews();
        for (int i = 0; i <= dataIndex; i++) {
            addDataItem(dataValue[i], getGroup);
        }
    } */

/*    private void addDataItem(float getValue, ChipGroup getGroup) {
        final Chip newChip = new Chip(this);
        final String chipText = convertNumberWithUnit(getValue);
        final String equationText = convertNumber(getValue);
        newChip.setText(chipText);
        getGroup.addView(newChip);
        newChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
    }  */


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
              checkEquation("+");
    }

    public void addSubtraction(View view) {
        checkEquation("-");
    }

    public void addMultiplication(View view) {
        checkEquation("×");
    }

    public void addDivision(View view) {
        checkEquation("÷");
    }
        
    public void addEqual(View view) {
        checkEquation("=");
    }
    
    public void addZero(View view) {
        addNumber("0");
    }
    
    public void addOne(View view) {
        addNumber("1");
    }
    
    public void addTwo(View view) {
        addNumber("2");
    }
        
    public void addThree(View view) {
        addNumber("3");
    }
        
    public void addFour(View view) {
        addNumber("4");
    }
        
    public void addFive(View view) {
        addNumber("5");
    }
        
    public void addSix(View view) {
        addNumber("6");
    }
        
    public void addSeven(View view) {
        addNumber("7");
    }
        
    public void addEight(View view) {
        addNumber("8");
    }
        
    public void addNine(View view) {
        addNumber("9");
    }
        
    public void addDot(View view) {
        if (!eqNumHasDot) {
            addNumber(".");
            eqNumHasDot = true;
        }
    }
    
    // Calculate
    private void addNumber(String getString) {
        if (eqEnteringNum1) {
            eqNum1String = eqNum1String + getString;
            equationView.setText(eqNum1String);
        } else {
            eqSubstring = eqSubstring + getString;
            String s = eqNum1 + eqOperator + eqSubstring;
            equationView.setText(s);
    }
    
    private void checkEquation(String getString) {
      eqNumHasDot = false;
        if (eqEnteringNum1) {
            eqOperator = getString;
            String s = eqNum1 + eqOperator;
            equationView.setText(s);
            eqEnteringNum1 = false;
        } else {
        if 
        
        
        
        
        
        
        // old code below
        final BigDecimal num1 = new BigDecimal(equationChip1.getText().toString());
        final String operator = equationChip2.getText().toString();
        final BigDecimal num2 = new BigDecimal(equationChip3.getText().toString());
        
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
