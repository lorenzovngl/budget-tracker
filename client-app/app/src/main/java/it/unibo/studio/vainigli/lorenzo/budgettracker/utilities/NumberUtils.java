package it.unibo.studio.vainigli.lorenzo.budgettracker.utilities;

import java.text.NumberFormat;
import java.util.Currency;

public class NumberUtils {

    public static String doubleToCurrency(double value, boolean simbol){
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        Currency currency = Currency.getInstance("EUR");
        format.setCurrency(currency);
        if (simbol){
            return format.format(value) + " €";
        } else {
            return format.format(value);
        }
    }

    public static String doubleToPercent(double value, int digits){
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(digits);
        format.setMinimumFractionDigits(digits);
        return format.format(value*100) + "%";
    }

    public static String setDecimals(double value, int digits){
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(digits);
        format.setMinimumFractionDigits(digits);
        if (digits == 0){
            return format.format(value).replaceAll(",", "");
        } else {
            return format.format(value);
        }
    }

    public static String bytesToKB(long bytes){
        return setDecimals((double) (bytes / 1024), 0) + " KB";
    }

}
