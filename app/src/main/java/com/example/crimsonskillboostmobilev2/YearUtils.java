package com.example.crimsonskillboostmobilev2;

public class YearUtils {

    public static String getYearText(String year) {
        if (year == null || year.isEmpty()) {
            return "Unknown Year";
        }

        // Check if the year is already in textual format
        switch (year) {
            case "First Year":
            case "Second Year":
            case "Third Year":
            case "Fourth Year":
                return year; // Return as is
            default:
                try {
                    int numericYear = Integer.parseInt(year);
                    return getYearText(numericYear); // Convert numeric year to text
                } catch (NumberFormatException e) {
                    return "Unknown Year"; // Handle invalid formats
                }
        }
    }

    public static String getYearText(int year) {
        switch (year) {
            case 1:
                return "First Year";
            case 2:
                return "Second Year";
            case 3:
                return "Third Year";
            case 4:
                return "Fourth Year";
            default:
                return "Unknown Year";
        }
    }
}