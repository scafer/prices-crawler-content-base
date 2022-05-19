package io.github.scafer.prices.crawler.content.domain.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeUtils {

    public static final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

    public static SimpleDateFormat getSimpleDateTimeFormat() {
        return new SimpleDateFormat(DATE_TIME_FORMAT);
    }

    public static DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    }

    public static String getCurrentDateTimeString() {
        var now = LocalDateTime.now();
        return getDateTimeFormatter().format(now);
    }
}
