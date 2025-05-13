package com.ilhanozkan.libraryManagementSystem.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomDateTimeFormatter {
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    private CustomDateTimeFormatter() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Formats a LocalDateTime to a string using pattern "dd/MM/yyyy HH:mm:ss"
     * 
     * @param dateTime The LocalDateTime to format
     * @return The formatted date time string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        
        return dateTime.format(DATE_TIME_FORMATTER);
    }
    
    /**
     * Gets the DateTimeFormatter with pattern "dd/MM/yyyy HH:mm:ss"
     * 
     * @return The DateTimeFormatter
     */
    public static DateTimeFormatter getDateTimeFormatter() {
        return DATE_TIME_FORMATTER;
    }
} 