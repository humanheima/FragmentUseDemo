package com.hm.fragmentusedemo.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by dumingwei on 2018/1/15 0015.
 */

public class DateUtils {

    private static SimpleDateFormat YMD_HM = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

    public static String getYmdHm(long millis) {
        return YMD_HM.format(millis * 1000);
    }

}
