package com.dtstack.dbhaswitch.utils;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConversionUtils {

    public String listToString(List log) {
        StringBuffer stringBuffer = new StringBuffer();
        String retStr;
        for (Object object : log) {
            String str = String.valueOf(object);
            stringBuffer.append(str);
        }
        retStr = stringBuffer.toString();
        return retStr;
    }
}
