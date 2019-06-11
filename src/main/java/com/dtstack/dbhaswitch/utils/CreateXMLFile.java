package com.dtstack.dbhaswitch.utils;


import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;


public class CreateXMLFile {

    public static void createXml_mysql() {


        try {
            // 1、创建document对象
            Document document = DocumentHelper.createDocument();

            // 2、创建根节点configuration
            Element configuration = document.addElement("configuration");

            // 3、生成子节点及子节点内容
            Element environments = configuration.addElement("environments");
            environments.addAttribute("default", "development");

            // 3、生成子节点及子节点内容
            Element environment = configuration.addElement("environment");
            environment.addAttribute("id", "development");

            Element transactionManager = environment.addElement("transactionManager");
            transactionManager.addAttribute("type", "JDBC");

            Element dataSource = environment.addElement("dataSource");
            dataSource.addAttribute("type", "POOLED");

            Element property1 = dataSource.addElement("property");
            property1.addAttribute("name", "driver");
            property1.addAttribute("value", "com.mysql.jdbc.Driver");

            Element property2 = dataSource.addElement("property");
            property2.addAttribute("name", "urlRds");
            property2.addAttribute("value", "jdbc:mysql://localhost:3306");
            /**
             *
             * 这里要改啊，端口要改成动态的
             * */

            Element property3 = dataSource.addElement("property");
            property3.addAttribute("name", "username");
            property3.addAttribute("value", "root");

            /**
             *
             * 这里也要改啊，用户名以及下面的密码要改成动态的
             * */

            Element property4 = dataSource.addElement("property");
            property4.addAttribute("name", "password");
            property4.addAttribute("value", "root");

            Element mappers = configuration.addElement("mappers");
            Element mapper = mappers.addElement("mapper");
            /**
             * 映射的mapper写进去，里面是需要执行的sql语句
             * */
            mapper.addAttribute("urlRds", "");

            // 5、设置生成xml的格式
            OutputFormat format = OutputFormat.createPrettyPrint();
            // 设置编码格式
            format.setEncoding("UTF-8");


            String path = XmlFileUtil.getPath_pkg("mysql");
            // 6、生成xml文件
            File file = new File(path, "rss.xml");
            /**
             * 这里也要改啊，生成的xml文件名
             * */

            XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
            // 设置是否转义，默认使用转义字符
            writer.setEscapeText(false);
            writer.write(document);
            writer.close();
            System.out.println("生成xml成功");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
