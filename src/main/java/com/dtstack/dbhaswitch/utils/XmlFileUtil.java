package com.dtstack.dbhaswitch.utils;

import java.io.File;
import java.io.ByteArrayInputStream;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

@Component
public class XmlFileUtil {


    public static String getPath_pkg(String pkgname) {

        /**
         * 查找package路径
         * */
        try {

            String path = Thread.currentThread().getContextClassLoader().getResource(pkgname).getPath();
            System.out.println("pkg:" + path);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getWebPath() {
        String path;
        path = "/opt/DBHA";
        return path;
    }

    public static String getWebPathConfig() {
        String path;
        path = "/opt/DBHA/config";
        return path;
    }

    public static void inputXml(String path) throws Exception {
        /**
         * 读取XML文件
         * */
        String encoding = "UTF-8";
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("要读取的文件不存在");
        }
        SAXReader reader = new SAXReader();
        String xml = "";
        Document fileDocument = reader.read(file); //获取xml文件
        Document document = reader.read(new ByteArrayInputStream(xml.getBytes("utf-8")));//读取xml字符串，注意这里要转成输入流
    }
}
