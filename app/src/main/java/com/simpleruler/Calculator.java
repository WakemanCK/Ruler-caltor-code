package com.simpleruler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.simpleruler.MainActivity.decimalPlace;

public class Calculator extends AppCompatActivity {
    float[] dataValue;
    String[] dataString;
    int dataUnit = 2; // 0 = inch; 1 = mm; 2 = cm
    int dataIndex = 0;
    String eqString = "";
    String eqSubstring = "";
    String eqNum1 = "";
    String eqNum2 = "";
    String eqOperator = "";
    boolean eqEnteringNum1 = true;
    boolean justPressedEqual = false;
    boolean changingOperator = false;
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
        dataString = new String[dataIndex + 1];
        showList(dataValue);
        ListView dataList = findViewById(R.id.dataListView);
        dataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (eqEnteringNum1) {
                    eqNum1 = convertNumber(dataValue[position]);
                    answerView.setText(eqNum1);
                } else {
                    eqNum2 = convertNumber(dataValue[position]);
                    answerView.setText(eqNum2);
                }
            }
        });
    }// end of OnCreate

    private void highLight(int getIndex) {
        RadioGroup radioGroup = findViewById(R.id.unitRadioGroup);
        for (int i = 0; i < 3; i++) {
            if (i == getIndex) {
                radioGroup.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.colorList));
            } else {
                radioGroup.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.colorListDark));
            }
        }
    }

    private void showList(float[] data) {
        for (int i = 0; i <= dataIndex; i++) {
            if (data[i] >= 0) {
                dataString[i] = convertNumberWithUnit(data[i]);
            } else {
                dataString[i] = "";
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, dataString);
        ListView listView = findViewById(R.id.dataListView);
        listView.setAdapter(adapter);
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
    }

    public void delete(View view) {
        if (eqEnteringNum1) {
            if (eqNum1.length() < 2) {
                eqNum1 = "";
                answerView.setText("0");
            } else {
                eqNum1 = eqNum1.substring(0, eqNum1.length() - 1);
                showAnswer(eqNum1);
            }
        } else {
            if (eqNum2.length() < 2) {
                eqNum2 = "";
                answerView.setText("0");
            } else {
                eqNum2 = eqNum2.substring(0, eqNum2.length() - 1);
                showAnswer(eqNum2);
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

    // Calculate
    private void addNumber(String getNumber) {
        changingOperator = false;
        answerView.setTextSize(50);
        if (justPressedEqual) {
            eqNum1 = "";
            justPressedEqual = false;
        }
        if (eqEnteringNum1) {
            if (eqNum1.length() < 10) {
                eqNum1 = eqNum1 + getNumber;
                answerView.setText(eqNum1);
            } else {
                Toast.makeText(this, getString(R.string.exceedMaxErrText), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (eqNum2.length() < 10) {
                eqNum2 = eqNum2 + getNumber;
                answerView.setText(eqNum2);
            } else {
                Toast.makeText(this, getString(R.string.exceedMaxErrText), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showEquation(String stringToShow) {
        if (stringToShow.length() > 44) {
            Toast.makeText(this, getString(R.string.expressionTooLongText), Toast.LENGTH_SHORT).show();
            eqString = "";
            eqSubstring = "";
            eqNum1 = "";
            eqNum2 = "";
            eqOperator = "";
            eqEnteringNum1 = true;
            equationView.setTextSize(50);
            equationView.setText(getString(R.string.errorText));
            return;
        }
        if (stringToShow.length() > 11) {
            equationView.setTextSize(25);
        } else {
            equationView.setTextSize(50);
        }
        equationView.setText(stringToShow);
    }

    private void showAnswer(String stringToShow) {
        if (stringToShow.length() > 44) {
            Toast.makeText(this, getString(R.string.expressionTooLongText), Toast.LENGTH_SHORT).show();
            eqString = "";
            eqSubstring = "";
            eqNum1 = "";
            eqNum2 = "";
            eqOperator = "";
            eqEnteringNum1 = true;
            answerView.setTextSize(50);
            answerView.setText(getString(R.string.errorText));
            return;
        }
        if (stringToShow.length() > 11) {
            answerView.setTextSize(25);
        } else {
            answerView.setTextSize(50);
        }
        answerView.setText(stringToShow);
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
            showEquation(eqString);
            eqEnteringNum1 = false;
            eqNum2 = "";
            eqSubstring = "";
            answerView.setText("0");
            return;
        }
        // Check changing operator
        if (eqNum2.equals("")) {
            if (eqSubstring.equals("")) {
                eqEnteringNum1 = true;
                checkEquation(getOperator);
                return;
            } else {
                eqSubstring = eqSubstring.substring(0, eqSubstring.length() - 1);
                eqNum2 = eqSubstring;
                eqSubstring = "";
                changingOperator = true;
                checkEquation(getOperator);
                return;
            }
        }
        // Check not divided by zero
        if (!changingOperator) {
            BigDecimal tempNum2 = new BigDecimal(eqNum2).stripTrailingZeros();
            if ((eqString.charAt(eqString.length() - 1) == '÷') && (tempNum2.equals(BigDecimal.ZERO))) {
                answerView.setText(getString(R.string.errorText));
                Toast.makeText(this, getString(R.string.dividedByZeroErrText), Toast.LENGTH_SHORT).show();
                eqNum2 = "";
                return;
            }
        }
        // Check whether need to do multiplication or division BEFORE addition or subtraction
        if ((eqOperator.equals("+") || eqOperator.equals("-")) && (getOperator.equals("×") || getOperator.equals("÷"))) {
            if (eqNum2.equals("")) {
                eqSubstring = eqSubstring.substring(0, eqSubstring.length() - 1) + getOperator;
            } else {
                eqSubstring = eqSubstring + eqNum2 + getOperator;
            }
            eqString = eqNum1 + eqOperator + eqSubstring;
            showEquation(eqString);
            eqNum2 = "";
            answerView.setText("0");
            return;
        }
        // Start actual calculation, × or ÷ first
        if (eqOperator.equals("×") || eqOperator.equals("÷")) {
            eqString = eqNum1 + eqOperator + eqSubstring + eqNum2;
            BigDecimal answer = calculateMultiplication(eqString);
            eqNum1 = answer.stripTrailingZeros().toPlainString();
            if (eqNum1.length() > 12) {
                eqNum1 = answer.stripTrailingZeros().toString();
            }
            eqOperator = getOperator;
            eqString = eqNum1 + getOperator;
            showEquation(eqString);
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
        eqNum1 = answer.stripTrailingZeros().toPlainString();
        if (eqNum1.length() > 12) {
            eqNum1 = answer.stripTrailingZeros().toString();
        }
        eqOperator = getOperator;
        eqSubstring = "";
        eqNum2 = "";
        eqString = eqNum1 + eqOperator;
        showEquation(eqString);
        answerView.setText("0");
    }

    private void equalPressed() {
        if (eqEnteringNum1) {
            return;
        }
        // Check changing operator
        if (eqNum2.equals("")) {
            if (eqSubstring.equals("")) {
                return;
            }
            eqSubstring = eqSubstring.substring(0, eqSubstring.length() - 1);
            changingOperator = true;
        }
        // Check not divided by zero
        if (!changingOperator) {
            BigDecimal tempNum2 = new BigDecimal(eqNum2).stripTrailingZeros();
            if ((eqString.charAt(eqString.length() - 1) == '÷') && (tempNum2.equals(BigDecimal.ZERO))) {
                answerView.setText(getString(R.string.errorText));
                Toast.makeText(this, getString(R.string.dividedByZeroErrText), Toast.LENGTH_SHORT).show();
                eqNum2 = "";
                return;
            }
        }
        // Start calculation
        BigDecimal answer;
        eqString = eqNum1 + eqOperator + eqSubstring + eqNum2;
        showEquation(eqString + "=");
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
        eqNum1 = answer.stripTrailingZeros().toPlainString();
        if (eqNum1.length() > 12) {
            eqNum1 = answer.stripTrailingZeros().toString();
        }
        eqOperator = "";
        eqSubstring = "";
        eqNum2 = "";
        eqEnteringNum1 = true;
        justPressedEqual = true;
        showAnswer(eqNum1);
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
        equationView.setText(R.string.sumAllAnswerText);
        showAnswer(eqNum1);
    }

    // Copy to clipboard
    public void copyDataToClip(View view) {
        StringBuilder dataText = new StringBuilder();
        for (int i = 0; i <= dataIndex; i++) {
            dataText.append(dataString[i]).append("\n");
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("data text", dataText.toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(Calculator.this, R.string.copyClipToastText, Toast.LENGTH_SHORT).show();
    }

    public void copyAnswerToClip(View view) {
        String answerText = answerView.getText() + "\n";
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("data text", answerText);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(Calculator.this, R.string.copyClipToastText, Toast.LENGTH_SHORT).show();
    }

}
