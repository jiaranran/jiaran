package org.jeecg.modules.util;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * @Author yangxu
 * @Date 2019-08-08 10:53
 */
public class StringUtil {

    /**
     * 使用正则表达式来判断字符串中是否包含字母
     * @param str 待检验的字符串
     * @return 返回是否包含
     * true: 包含字母 ;false 不包含字母
     */
    public static boolean judgeContainsStr(String str) {
        String regex=".*[a-zA-Z]+.*";
        Matcher m= Pattern.compile(regex).matcher(str);
        return m.matches();
    }
    public static Date weekToDate(String week) {

//        String weekY = "201805";

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(week.substring(0, 4))); // 2018年
        cal.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(week.substring(4, 6))); // 设置为2018年的第5周
        cal.set(Calendar.DAY_OF_WEEK, 1); // 1表示周日，2表示周一，7表示周六
        Date date = cal.getTime();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
//        String d = sdf.format(date);
        System.out.println("当前时间：" + date);
        return date;
    }
}
