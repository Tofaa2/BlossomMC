package net.blossom.utils;

import java.text.SimpleDateFormat;
import java.time.Duration;

public final class DateUtils {

    private DateUtils() {}
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final long TWO_HOURS = Duration.ofHours(2).toMillis();

    public static String convertTime(long time) {
        return DATE_FORMAT.format(time - TWO_HOURS);
    }

}
