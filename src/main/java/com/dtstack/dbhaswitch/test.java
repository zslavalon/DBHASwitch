package com.dtstack.dbhaswitch;

import org.apache.catalina.LifecycleState;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class test {


    private static final DateTimeFormatter getNowTime = DateTimeFormatter.ofPattern("HH:mm");
///Library/Java/JavaVirtualMachines/jdk1.8.0_211.jdk/Contents/Home/jre/lib/rt.jar!/sun/reflect/Reflection.class
    public test() {
        LocalDateTime setTime = LocalDateTime.now();
        String setTimeDayStr = getNowTime.format(setTime);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        String setTimeStartStr = "00:00";
        String setTimeEndStr = "23:59";
        List<?> d = new ArrayList<>();
        try {
            Date beginTime = sdf.parse(setTimeStartStr);
            Date endTime = sdf.parse(setTimeDayStr);
            System.out.println(endTime.compareTo(beginTime));
            System.out.println(setTimeDayStr);
            System.out.println(endTime);
        } catch (Exception e) {

        }
    }


}
