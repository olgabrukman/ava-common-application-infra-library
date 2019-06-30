package time;

import resource.MessageApi;
import validation.EnumValidation;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static SimpleDateFormat getDateFormat(boolean withMilliseconds) throws Exception {
        if (withMilliseconds) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static String getTimeCurrent(boolean withMilliseconds) throws Exception {
        return getTime(System.currentTimeMillis(), withMilliseconds);
    }

    public static String getTime(long time, boolean withMilliseconds) throws Exception {
        SimpleDateFormat dateFormat = getDateFormat(withMilliseconds);
        return dateFormat.format(new Date(time));
    }

    public static String getForGuiOrCli(long time, boolean isForGui) throws Exception {
        if (isForGui) {
            return Long.toString(time);
        }

        return getTime(time, false);
    }

    public static long parseTimePeriod(String timePeriod) throws Exception {
        timePeriod = timePeriod.trim();
        String parts[] = timePeriod.split(" ");
        if (parts.length != 2) {
            // invalid format
            throw MessageApi.getException("app00759",
                    "TIME", timePeriod);
        }
        long amount = Long.parseLong(parts[0]);
        String unit = parts[1].trim();
        unit = unit.toUpperCase();
        if (unit.endsWith("S")) {
            unit = unit.substring(0, unit.length() - 1);
        }
        TimeUnit timeUnit = EnumValidation.validateEnumAndGet("time unit", TimeUnit.class, unit);
        return amount * timeUnit.getTime();
    }

    static public String getUptime(long milliseconds) throws Exception {
        long seconds = milliseconds / 1000;
        if (seconds < 0) {
            seconds = 0;
        }
        long minutes = seconds / 60;
        seconds = seconds - minutes * 60;
        long hours = minutes / 60;
        minutes = minutes - hours * 60;
        long days = hours / 24;
        hours = hours - days * 24;
        StringBuilder result = new StringBuilder();
        if (days > 0) {
            result.append(Long.toString(days));
            result.append(" days ");
        }
        if (hours > 0) {
            result.append(Long.toString(hours));
            result.append(" hours ");
        }
        if (minutes > 0) {
            result.append(Long.toString(minutes));
            result.append(" minutes ");
        }
        if (seconds > 0) {
            result.append(Long.toString(seconds));
            result.append(" seconds");
        }
        return result.toString();
    }

}
