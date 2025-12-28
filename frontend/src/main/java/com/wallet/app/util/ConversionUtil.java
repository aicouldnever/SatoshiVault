package com.wallet.app.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Utility class for converting between backend and frontend data formats.
 * Handles satoshis <-> BTC conversion and timestamp <-> LocalDateTime mapping.
 */
public class ConversionUtil {
    
    private static final long SATOSHIS_PER_BTC = 100_000_000L;
    
    /**
     * Convert satoshis to BTC
     * @param satoshis Amount in satoshis
     * @return Amount in BTC
     */
    public static double satoshisToBTC(long satoshis) {
        return satoshis / (double) SATOSHIS_PER_BTC;
    }
    
    /**
     * Convert BTC to satoshis
     * @param btc Amount in BTC
     * @return Amount in satoshis
     */
    public static long btcToSatoshis(double btc) {
        return (long) (btc * SATOSHIS_PER_BTC);
    }
    
    /**
     * Convert Unix timestamp (seconds) to LocalDateTime
     * @param timestamp Unix timestamp in seconds
     * @return LocalDateTime object
     */
    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    }
    
    /**
     * Convert Unix timestamp (milliseconds) to LocalDateTime
     * @param timestampMillis Unix timestamp in milliseconds
     * @return LocalDateTime object
     */
    public static LocalDateTime timestampMillisToLocalDateTime(long timestampMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampMillis), ZoneId.systemDefault());
    }
    
    /**
     * Convert LocalDateTime to Unix timestamp (seconds)
     * @param dateTime LocalDateTime object
     * @return Unix timestamp in seconds
     */
    public static long localDateTimeToTimestamp(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }
    
    /**
     * Format satoshis as BTC string with 8 decimal places
     * @param satoshis Amount in satoshis
     * @return Formatted string (e.g., "0.00100000 BTC")
     */
    public static String formatSatoshisAsBTC(long satoshis) {
        return String.format("%.8f BTC", satoshisToBTC(satoshis));
    }
}
