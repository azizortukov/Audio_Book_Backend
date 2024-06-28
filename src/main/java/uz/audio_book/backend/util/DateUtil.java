package uz.audio_book.backend.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {

    private static final String DATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static boolean isValidFormat(String dateStr) {
        Pattern pattern = Pattern.compile(DATE_PATTERN);
        Matcher matcher = pattern.matcher(dateStr);
        return matcher.matches();
    }

    public static LocalDate parse(String date) {
        return LocalDate.parse(date, formatter);
    }
}
