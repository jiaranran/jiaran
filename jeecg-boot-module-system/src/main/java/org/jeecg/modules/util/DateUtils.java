package org.jeecg.modules.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
//    日期所在周周日日期
    public static Date dateToWeek(String date,String fileName,int weekStart) {
//         date = "20200524";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date1 = null;
        try {
            date1 = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        // 设置周开始 比从周日开始
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if(weekStart==0){
            if(fileName==null||fileName==""){
                weekStart= calendar.get(Calendar.DAY_OF_WEEK);
            }else {
                int index = fileName.lastIndexOf(".");
                String substring = fileName.substring(index - 8, index);
                try {
                    Date date2 = sdf.parse(substring);
                    weekStart = getWeekNum(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }
        c.setFirstDayOfWeek(weekStart);
        // 设置时间
        c.setTime(date1);

        // 获取时间的年
        int year = c.get(Calendar.YEAR);
        // 获取时间的年的第几周
        int week = c.get(Calendar.WEEK_OF_YEAR);

        // 设置这周的周几 1是周日 2是周一 。。。
        c.set(Calendar.DAY_OF_WEEK, weekStart);

//        System.out.println(year + "" + week);

        Date date2 = c.getTime();
//        System.out.println("当前时间的周开始：" + sdf.format(date2));
        return date2;
    }
    public static int getWeekNum(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int num = c.get(Calendar.DAY_OF_WEEK);
        return num;
    }
//    7天后的日期
    public static Date getNextWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 7);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        String result = format.format(today);
//        System.out.println(result);
        return today;
    }
//   获取上周周开始日期， date 执行哪天的文件 ，默认date前一天是周结束日期,weekStart设置周几为周开始
    public static Date getPreWeek(Date date,int weekStart) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if(weekStart==0){
            weekStart= calendar.get(Calendar.DAY_OF_WEEK);
        }
        calendar.setFirstDayOfWeek(weekStart);
        calendar.set(Calendar.DAY_OF_WEEK, weekStart);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 7);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        String result = format.format(today);
//        System.out.println(result);
        return today;
    }
//    周几作为周开始
    public static int getWeekStart(Date date) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        /*calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) -7);
        Date today = calendar.getTime();*/
//        calendar.setTime(today);
        int w = calendar.get(Calendar.DAY_OF_WEEK)-1 ;

        System.out.println(weekDays[w]);
        return w;
    }
    //几周前的周日
    public static Date getProWeekDate(){
        Calendar cal = Calendar.getInstance();
        int n = -2;
        String monday;
        cal.add(Calendar.DATE, n*7);

//想周几，这里就传几Calendar.MONDAY（TUESDAY...）
        cal.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        Date preDate = cal.getTime();
        monday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        return preDate;
    }
    public static Date getDate(String fileName){
        Date parse;
        if(fileName!=null&&fileName!=""){
            int index = fileName.lastIndexOf(".");
            String substring = fileName.substring(index - 8, index);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            try {
                 parse = sdf.parse(substring);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }else {
            parse=new Date();
        }
        return getPreDate(parse);
    }
    public static Date getDate(String fileName,int weekStart){

        Date parse;
        if(fileName!=null&&fileName!=""){
            int index = fileName.lastIndexOf(".");
            String substring = fileName.substring(index - 8, index);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            try {
                parse = sdf.parse(substring);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }else {
            parse=new Date();
        }
        return getPreWeek(parse, weekStart);
    }
    public static Date getNextDate(Date date) {
        Calendar c = Calendar.getInstance();

        c.setTime(date);

        c.add(Calendar.DAY_OF_MONTH, 1);

        Date tomorrow   = c.getTime();//这是明天
        return tomorrow ;
    }
    public static Date getPreDate(Date date) {
        Calendar c = Calendar.getInstance();

        c.setTime(date);

        c.add(Calendar.DAY_OF_MONTH, -1);

        Date yesterday  = c.getTime();//这是昨天
        return yesterday;
    }
    public static void main(String[] args) throws ParseException {
        String date = "20200514";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date parse = sdf.parse(date);
        System.out.println(getWeekNum(parse));
    }
}
