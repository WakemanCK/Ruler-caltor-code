package com.simpleruler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
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
    String eqString = "";
    String eqSubstring = "";
    String eqNum1 = "";
    String eqNum2 = "";
    String eqOperator = "";
    boolean eqEnteringNum1 = true;
  //  boolean eqNumHasDot = false;
    boolean justPressedEqual = false;
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
        equationView = findViewById(R.id.equationTextView);
        answerView = findViewById(R.id.answerTextView);
        answerView.setText("0");
        final RadioGroup unitGroup = findViewById(R.id.unitRadioGroup);
        highLight(dataUnit);
        unitGroup.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int childIndex) {
                        dataUnit = radioGroup.indexOfChild(findViewById(childIndex));
                        highLight(dataUnit);
                        showList(dataValue);
                    }
                }
        );
        // Get data
        Intent intent = getIntent();
        dataValue = intent.getFloatArrayExtra(MainActivity.EXTRA_VALUE);
        dataIndex = intent.getIntExtra(MainActivity.EXTRA_INDEX, 0);
        showList(dataValue);
        ListView dataList = (ListView) findViewById(R.id.dataListView);
        dataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (eqEnteringNum1) {
                    eqNum1 = convertNumber(dataValue[position]);
                    answerView.setText(eqNum1);
           //         eqNumHasDot = eqNum1.contains(".");
                } else {
                    eqNum2 = convertNumber(dataValue[position]);
                    answerView.setText(eqNum2);
              //      eqNumHasDot = eqNum2.contains(".");
                }
            }
        });
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

    private void showList(float[] data) {
        String[] dataString = new String[dataIndex + 1];
        //int j = 0;
        for (int i = 0; i <= dataIndex; i++) {
            if (data[i] >= 0) {
                dataString[i] = convertNumberWithUnit(data[i]);
                //j++;
            } else {
                dataString[i] = "";
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, dataString);
        ListView listView = (ListView) findViewById(R.id.dataListView);
        listView.setAdapter(adapter);
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

    // Buttons
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
        equalPressed();
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
        if (eqEnteringNum1) {
            if (!eqNum1.contains(".")) {
                addNumber(".");
            }
        } else {
            if (!eqNum2.contains(".")) {
                addNumber(".");
            }
        }
        //if (!eqNumHasDot) {
          //  addNumber(".");
            //eqNumHasDot = true;
        //}
    }

    public void delete(View view) {
        if (eqEnteringNum1) {
            if (eqNum1.length() < 2) {
                eqNum1 = "";
                answerView.setText("0");
       //         eqNumHasDot = false;
            } else {
                eqNum1 = eqNum1.substring(0, eqNum1.length() - 1);
                answerView.setText(eqNum1);
         //       eqNumHasDot = eqNum1.contains(".");
            }
        } else {
            if (eqNum2.length() < 2) {
                eqNum2 = "";
                answerView.setText("0");
        //        eqNumHasDot = false;
            } else {
                eqNum2 = eqNum2.substring(0, eqNum2.length() - 1);
                answerView.setText(eqNum2);
          //      eqNumHasDot = eqNum2.contains(".");
            }
        }
    }

    public void clearEquation(View view) {
        eqString = "";
        eqSubstring = "";
        eqNum1 = "";
        eqNum2 = "";
        eqOperator = "";
        eqEnteringNum1 = true;
        equationView.setText("");
        answerView.setText("0");
    }
   
    public void showExceedMaxErr() {
        eqString = "";
        eqSubstring = "";
        eqNum1 = "";
        eqNum2 = "";
        eqOperator = "";
        eqEnteringNum1 = true;
        equationView.setText(getString(R.string.exceedMaxErrText);
        answerView.setText("0");
    }

    // Calculate
    private void addNumber(String getNumber) {
        if (justPressedEqual) {
            eqNum1 = "";
            justPressedEqual = false;
        }
        if (eqEnteringNum1) {
            if (eqNum1.length() < 11) {
            eqNum1 = eqNum1 + getNumber;
            answerView.setText(eqNum1);
            }
        } else {
            if (eqNum2.length() < 11) {
            eqNum2 = eqNum2 + getNumber;
            answerView.setText(eqNum2);
            }
        }
    }

    private void checkEquation(String getOperator) {
        justPressedEqual = false;
        // Check entering Num1
        if (eqEnteringNum1) {
            eqOperator = getOperator;
            if (eqNum1.equals("")) {
                eqNum1 = "0";
            }
            eqString = eqNum1 + eqOperator;
            equationView.setText(eqString);
            eqEnteringNum1 = false;
            eqNum2 = "";
            answerView.setText("0");
            return;
        }
        // Check not divided by zero
        BigDecimal tempNum2 = new BigDecimal(eqNum2).stripTrailingZeros();
        if ((eqString.charAt(eqString.length()) == "÷") && (tempNum2.equals(BigDecimal.ZERO))) {
            answerView.setText(getString(R.string.dividedByZeroErrText));
            eqNum2 = "";
            return;
        // Check whether need to do multiplication or division BEFORE addition or subtraction
        if ((eqOperator.equals("+") || eqOperator.equals("-")) && (getOperator.equals("×") || getOperator.equals("÷"))) {
            if (eqNum2.equals("")) {
                eqSubstring = eqSubstring.substring(0, eqSubstring.length() - 1) + getOperator;
            } else {
                eqSubstring = eqSubstring + eqNum2 + getOperator;
            }
            eqString = eqNum1 + eqOperator + eqSubstring;
            equationView.setText(eqString);
            eqNum2 = "";
            answerView.setText("0");
            return;
        }
        // Start actual calculation, × or ÷ first
        if (eqOperator.equals("×") || eqOperator.equals("÷")) {
            eqString = eqNum1 + eqOperator + eqSubstring + eqNum2;
            BigDecimal answer = calculateMultiplication(eqString);
            if (answer.compareTo(9999999999999) == -1) {
                    showExceedMaxErr();
                  return;
            }
            eqNum1 = String.valueOf(answer);
            eqOperator = getOperator;
            eqString = eqNum1 + getOperator;
            equationView.setText(eqString);
            eqNum2 = "";
            answerView.setText("0");
            return;
        }
        // + or - below
        BigDecimal subStringAns;
        eqSubstring = eqSubstring + eqNum2;
        subStringAns = calculateMultiplication(eqSubstring);
        BigDecimal answer = new BigDecimal(eqNum1);
        if (eqOperator.equals("+")) {
            answer = answer.add(subStringAns);
        }
        if (eqOperator.equals("-")) {
            answer = answer.subtract(subStringAns);
        }
            if (answer.compareTo(9999999999999) == -1) {
                    showExceedMaxErr();
                  return;
                                       }
        eqNum1 = String.valueOf(answer);
        eqOperator = getOperator;
        eqSubstring = "";
        eqNum2 = "";
        eqString = eqNum1 + eqOperator;
        equationView.setText(eqString);
        answerView.setText("0");
    }

    private void equalPressed() {
        if (eqEnteringNum1) {
            return;
        }
        BigDecimal answer;
        eqString = eqNum1 + eqOperator + eqSubstring + eqNum2;
        equationView.setText(eqString + "=");
        if (eqOperator.equals("×") || eqOperator.equals("÷")) {
            answer = calculateMultiplication(eqString);
        } else {  // + or - below
            BigDecimal subStringAns;
            eqSubstring = eqSubstring + eqNum2;
            subStringAns = calculateMultiplication(eqSubstring);
            answer = new BigDecimal(eqNum1);
            if (eqOperator.equals("+")) {
                answer = answer.add(subStringAns);
            }
            if (eqOperator.equals("-")) {
                answer = answer.subtract(subStringAns);
            }
        }
        eqNum1 = String.valueOf(answer);
        eqOperator = "";
        eqSubstring = "";
        eqNum2 = "";
        eqEnteringNum1 = true;
        justPressedEqual = true;
     //   eqNumHasDot = false;
        answerView.setText(eqNum1);
    }

    private BigDecimal calculateMultiplication(String getEqString) {
        int maxPart = getEqString.length() / 2 + 1;
        BigDecimal[] number = new BigDecimal[maxPart];
        boolean[] isMultiply = new boolean[maxPart];
        int count = 0;
        int startIndex = 0;
        for (int index = 1; index < getEqString.length() - 1; index++) {
            if (getEqString.charAt(index) == '×') {
                number[count] = new BigDecimal(getEqString.substring(startIndex, index));
                isMultiply[count] = true;
                startIndex = index + 1;
                count++;
            }
            if (getEqString.charAt(index) == '÷') {
                number[count] = new BigDecimal(getEqString.substring(startIndex, index));
                isMultiply[count] = false;
                startIndex = index + 1;
                count++;
            }
        }
        number[count] = new BigDecimal(getEqString.substring(startIndex));
        BigDecimal answer = number[0];
        for (int i = 0; i < count; i++) {
            if (isMultiply[i]) {
                answer = answer.multiply(number[i + 1]);
            } else {
                answer = answer.divide(number[i + 1], 10, RoundingMode.HALF_UP);
            }
        }
        return answer;
    }

    public void sumAll(View view) {
        BigDecimal sum = BigDecimal.ZERO;
        String s;
        for (int i = 0; i <= dataIndex; i++) {
            s = convertNumber(dataValue[i]);
            sum = sum.add(new BigDecimal(s));
        }
        sum = sum.stripTrailingZeros();
        eqNum1 = sum.toPlainString();
        eqOperator = "";
        eqString = "";
        eqSubstring = "";
        eqNum2 = "";
        eqEnteringNum1 = true;
   //     eqNumHasDot = eqNum1.contains(".");
        equationView.setText(R.string.sumAllAnswerText);
        answerView.setText(eqNum1);
    }
        

/*
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
        Button answerButton = findViewById(R.id.answerButton);
        String answerText = answerButton.getText() + "\n";
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("data text", answerText);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(Calculator.this, R.string.copyClipToastText, Toast.LENGTH_SHORT).show();
    }

 */
}
