package com.example.numberconverter;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MainActivity extends AppCompatActivity {

    private EditText inputNumber;
    private Spinner baseSpinner;
    private TextView resultBinary, resultOctal, resultHex, resultDecimal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputNumber = findViewById(R.id.input_number);
        baseSpinner = findViewById(R.id.base_spinner);
        resultBinary = findViewById(R.id.result_binary);
        resultOctal = findViewById(R.id.result_octal);
        resultHex = findViewById(R.id.result_hex);
        resultDecimal = findViewById(R.id.result_decimal);
        Button convertButton = findViewById(R.id.convert_button);

        String[] bases = {"Decimal", "Binary", "Octal", "Hexadecimal"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bases);
        baseSpinner.setAdapter(adapter);

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = inputNumber.getText().toString().trim();
                if (!input.isEmpty()) {
                    int base = baseSpinner.getSelectedItemPosition();
                    convertNumber(input, base);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void convertNumber(String input, int base) {
        try {
            BigDecimal decimalValue = BigDecimal.ZERO;

            if (!isValidInput(input, base)) {
                Toast.makeText(MainActivity.this, "Invalid number for the selected base", Toast.LENGTH_SHORT).show();
                clearResults();
                return;
            }

            switch (base) {
                case 0:
                    decimalValue = new BigDecimal(input);
                    break;
                case 1:
                    decimalValue = binaryToDecimal(input);
                    break;
                case 2:
                    decimalValue = octalToDecimal(input);
                    break;
                case 3:
                    decimalValue = hexToDecimal(input);
                    break;
                default:
                    throw new NumberFormatException("Invalid base");
            }

            resultDecimal.setText("Decimal: " + decimalValue.stripTrailingZeros().toPlainString());
            resultBinary.setText("Binary: " + decimalToBinary(decimalValue));
            resultOctal.setText("Octal: " + decimalToOctal(decimalValue));
            resultHex.setText("Hexadecimal: " + decimalToHex(decimalValue));

        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity.this, "Invalid number for the selected base", Toast.LENGTH_SHORT).show();
            clearResults();
        }
    }

    private String decimalToBinary(BigDecimal decimal) {
        BigInteger integerPart = decimal.toBigInteger();
        BigDecimal fractionalPart = decimal.subtract(new BigDecimal(integerPart));
        StringBuilder binaryResult = new StringBuilder(integerPart.toString(2));

        if (fractionalPart.compareTo(BigDecimal.ZERO) > 0) {
            binaryResult.append(".");
            while (fractionalPart.compareTo(BigDecimal.ZERO) > 0) {
                fractionalPart = fractionalPart.multiply(BigDecimal.valueOf(2));
                BigInteger fractionBit = fractionalPart.toBigInteger();
                binaryResult.append(fractionBit);
                fractionalPart = fractionalPart.subtract(new BigDecimal(fractionBit));
            }
        }
        return binaryResult.toString();
    }

    private String decimalToOctal(BigDecimal decimal) {
        BigInteger integerPart = decimal.toBigInteger();
        BigDecimal fractionalPart = decimal.subtract(new BigDecimal(integerPart));
        StringBuilder octalResult = new StringBuilder(integerPart.toString(8));

        if (fractionalPart.compareTo(BigDecimal.ZERO) > 0) {
            octalResult.append(".");
            while (fractionalPart.compareTo(BigDecimal.ZERO) > 0) {
                fractionalPart = fractionalPart.multiply(BigDecimal.valueOf(8));
                BigInteger fractionBit = fractionalPart.toBigInteger();
                octalResult.append(fractionBit);
                fractionalPart = fractionalPart.subtract(new BigDecimal(fractionBit));
            }
        }
        return octalResult.toString();
    }

    private String decimalToHex(BigDecimal decimal) {
        BigInteger integerPart = decimal.toBigInteger();
        BigDecimal fractionalPart = decimal.subtract(new BigDecimal(integerPart));
        StringBuilder hexResult = new StringBuilder(integerPart.toString(16).toUpperCase());

        if (fractionalPart.compareTo(BigDecimal.ZERO) > 0) {
            hexResult.append(".");
            while (fractionalPart.compareTo(BigDecimal.ZERO) > 0) {
                fractionalPart = fractionalPart.multiply(BigDecimal.valueOf(16));
                BigInteger fractionBit = fractionalPart.toBigInteger();
                hexResult.append(fractionBit.toString(16).toUpperCase());
                fractionalPart = fractionalPart.subtract(new BigDecimal(fractionBit));
            }
        }
        return hexResult.toString();
    }

    private BigDecimal binaryToDecimal(String binary) {
        String[] parts = binary.split("\\.");
        BigDecimal decimal = new BigDecimal(new BigInteger(parts[0], 2));
        if (parts.length > 1) {
            BigDecimal fractional = BigDecimal.ZERO;
            for (int i = 0; i < parts[1].length(); i++) {
                fractional = fractional.add(new BigDecimal(Character.getNumericValue(parts[1].charAt(i)))
                        .divide(BigDecimal.valueOf(Math.pow(2, i + 1))));
            }
            decimal = decimal.add(fractional);
        }
        return decimal;
    }

    private BigDecimal octalToDecimal(String octal) {
        String[] parts = octal.split("\\.");
        BigDecimal decimal = new BigDecimal(new BigInteger(parts[0], 8));
        if (parts.length > 1) {
            BigDecimal fractional = BigDecimal.ZERO;
            for (int i = 0; i < parts[1].length(); i++) {
                fractional = fractional.add(new BigDecimal(Character.getNumericValue(parts[1].charAt(i)))
                        .divide(BigDecimal.valueOf(Math.pow(8, i + 1))));
            }
            decimal = decimal.add(fractional);
        }
        return decimal;
    }

    private BigDecimal hexToDecimal(String hex) {
        String[] parts = hex.split("\\.");
        BigDecimal decimal = new BigDecimal(new BigInteger(parts[0], 16));
        if (parts.length > 1) {
            BigDecimal fractional = BigDecimal.ZERO;
            for (int i = 0; i < parts[1].length(); i++) {
                fractional = fractional.add(new BigDecimal(Character.digit(parts[1].charAt(i), 16))
                        .divide(BigDecimal.valueOf(Math.pow(16, i + 1))));
            }
            decimal = decimal.add(fractional);
        }
        return decimal;
    }

    private boolean isValidInput(String input, int base) {
        switch (base) {
            case 0:
                return input.matches("\\d+(\\.\\d+)?");
            case 1:
                return input.matches("[01]+(\\.[01]+)?");
            case 2:
                return input.matches("[0-7]+(\\.[0-7]+)?");
            case 3:
                return input.matches("[0-9A-Fa-f]+(\\.[0-9A-Fa-f]+)?");
            default:
                return false;
        }
    }

    private void clearResults() {
        resultDecimal.setText("Decimal: ");
        resultBinary.setText("Binary: ");
        resultOctal.setText("Octal: ");
        resultHex.setText("Hexadecimal: ");
    }
}