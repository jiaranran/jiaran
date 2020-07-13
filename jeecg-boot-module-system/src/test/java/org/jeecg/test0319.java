package org.jeecg;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class test0319 {
    public static void test(){
        String filename="wenjian.csv";

        System.out.println(filename.substring(0,filename.lastIndexOf(".") )+".log");
    }

    public static void main(String[] args) {

        BigDecimal   b   =   new BigDecimal((double)1/3);
        double   f1   =   b.setScale(4,   BigDecimal.ROUND_HALF_UP).doubleValue();
        System.out.println(f1);
    }
}
