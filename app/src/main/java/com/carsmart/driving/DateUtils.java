package com.carsmart.driving;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DateUtils {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.ms";
    public static final String DATE_FORMAT_2 = "MMddHHmmss";

    private static HashMap<String, SimpleDateFormat> formatMap = new HashMap<>();

    /**
     * 时间格式化
     */
    public static synchronized String formatDate(Date date, String format) {
        if (date == null) {
            return null;
        }

        SimpleDateFormat sdf = formatMap.get(format);

        if (sdf == null) {
            sdf = new SimpleDateFormat(format, Locale.getDefault());
            formatMap.put(format, sdf);
        }

        return sdf.format(date);
    }

    /**
     * 时间格式化
     */
    public static String formatDate(long time, String format) {
        return formatDate(new Date(time), format);
    }

}
