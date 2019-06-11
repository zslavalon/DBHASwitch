package com.dtstack.dbhaswitch;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class test {


    private static final DateTimeFormatter getNowTime = DateTimeFormatter.ofPattern("HH:mm");

    public test() {
        LocalDateTime setTime = LocalDateTime.now();
        String setTimeDayStr = getNowTime.format(setTime);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        String setTimeStartStr = "00:00";
        String setTimeEndStr = "23:59";

        try {
            Date beginTime = sdf.parse(setTimeStartStr);
            Date endTime = sdf.parse(setTimeDayStr);
            System.out.println(endTime.compareTo(beginTime));
            System.out.println(setTimeDayStr);
            System.out.println(endTime);
        } catch (Exception e) {

        }
    }

    public static void main(String[] args) {

        int i = 2;
        if (i != 0 && i != 1 || i == 2) {
            System.out.println("111");
        }
    }
}
