package itx.elastic.service.impl;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class ESUtils {

    private ESUtils() {
    }

    public static final String DATE_FORMAT = "yyyyMMdd'T'HHmmss.SSSZ";

    public static DateTimeFormatter getDefaultDateTimeFormatter() {
        return DateTimeFormatter.ofPattern(DATE_FORMAT);
    }

    public static ZonedDateTime fromString(String dateTimeWithTimeZone) {
        return ZonedDateTime.parse(dateTimeWithTimeZone, getDefaultDateTimeFormatter());
    }

    public static String toString(ZonedDateTime zonedDateTime) {
        return getDefaultDateTimeFormatter().format(zonedDateTime);
    }

    public static ZonedDateTime getNow() {
        return fromString(toString(ZonedDateTime.now()));
    }

}
