package com.wallet.app.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatter {
    private static final DecimalFormat BTC_FORMAT = new DecimalFormat("#,##0.00000000");
    private static final NumberFormat USD_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);
    
    /**
     * Format BTC amount with 8 decimal places
     * @param amount The BTC amount
     * @return Formatted string with BTC suffix
     */
    public static String formatBTC(double amount) {
        return BTC_FORMAT.format(amount) + " BTC";
    }
    
    /**
     * Format USD amount with currency symbol
     * @param amount The USD amount
     * @return Formatted USD string
     */
    public static String formatUSD(double amount) {
        return USD_FORMAT.format(amount);
    }
    
    /**
     * Format BTC amount to display (handles negative values)
     * @param amount The BTC amount
     * @return Formatted string
     */
    public static String formatBTCAmount(double amount) {
        String sign = amount >= 0 ? "+" : "";
        return sign + BTC_FORMAT.format(amount) + " BTC";
    }
}
