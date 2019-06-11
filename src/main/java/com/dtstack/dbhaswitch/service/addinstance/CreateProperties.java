package com.dtstack.dbhaswitch.service.addinstance;

import com.dtstack.dbhaswitch.model.Instance;
import com.dtstack.dbhaswitch.utils.XmlFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;

@Component
public class CreateProperties {

    /**
     * 创建properties文件
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    public boolean createProperties(Instance instance) {
        boolean bool = false;
//        String properties_path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String properties_path = XmlFileUtil.getWebPath();

        //检查文件夹是否存在
        File folder = new File(properties_path + "/config");
        logger.info("config folder = {}", folder);
        if (!folder.exists() && !folder.isDirectory()) {
            logger.info("Folder does not exist");
            folder.mkdirs();
            logger.info("Folder does not exist,create new config folder");
        }
        //检查实例的Properties是否存在

        String db_name = instance.getRdsCode() + "_" + instance.getRdsId() + ".properties";
        File file = new File(properties_path + "/config", db_name);
        if (!file.exists()) {
            try {
                logger.info("properties does not exist,create a new properties,name = {}", db_name);
                bool = file.createNewFile();
                return bool;
            } catch (IOException e) {
                logger.error("create properties folder failed", e);
            }
        } else {
            logger.warn("{} folder is exist,no need to create", db_name);
        }
        return bool;
    }

    public void updateProperties(Instance instance) {

        String db_name = instance.getRdsCode() + "_" + instance.getRdsId() + ".properties";
        Boolean bool = createProperties(instance);
//        String properties_path =  Thread.currentThread().getContextClassLoader().getResource("").getPath()+"/config";
        String properties_path = XmlFileUtil.getWebPathConfig();
        Properties props = new Properties();

        if (bool) {
            logger.info("update the properties data,ip={},port={},username={},password={},rdsId={}", instance.getIp(), instance.getPort(), instance.getUserName(), instance.getPassWord(), instance.getRdsId());
            try {
                FileInputStream fis = new FileInputStream(properties_path + "/" + db_name);
                props.load(fis);
                //Properties数据更新
                props.setProperty("spring.datasource.url", "jdbc:mysql://" + instance.getIp() + ":" + instance.getPort() + "/rds_ha?useUnicode=true&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false");
                props.setProperty("spring.datasource.username", instance.getUserName());
                props.setProperty("spring.datasource.password", instance.getPassWord());
                props.setProperty("spring.datasource.driver-class-name", "com.mysql.jdbc.Driver");
                props.setProperty("Instance_ID", instance.getRdsCode());

                props.setProperty("rdsId", String.valueOf(instance.getRdsId()));
                props.setProperty("whether_delete", "No");
                props.store(new FileOutputStream(properties_path + "/" + db_name), "create properties");
                fis.close();
                logger.info("update properties success");
            } catch (Exception e) {
                logger.error("update properties failed", e);
            }
        }
    }

    public void updateSwitchProperties(String db_name) {
//        String properties_path = Thread.currentThread().getContextClassLoader().getResource("").getPath()+"/config";
        String properties_path = XmlFileUtil.getWebPathConfig();
        Properties properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream(properties_path + "/" + db_name);
            properties.load(fis);
            //Properties数据更新。对应是发生异常倒换的场景，需要更新配置文件的主备数据
            String judgeMaster = properties.getProperty("judgeMaster");

            if (judgeMaster == "true" || judgeMaster.equals("true")) {
                properties.setProperty("judgeMaster", "false");
            } else if (judgeMaster == "false" || judgeMaster.equals("false")) {
                properties.setProperty("judgeMaster", "true");
            }
            properties.store(new FileOutputStream(properties_path + "/" + db_name), "update properties");
            fis.close();
            logger.info("update switch properties success");
        } catch (Exception e) {
            logger.error("update switch properties failed", e);
        }
    }

    public void updateDeleteProperties(String db_name) {
//        String properties_path = Thread.currentThread().getContextClassLoader().getResource("").getPath()+"/config";
        String properties_path = XmlFileUtil.getWebPathConfig();
        Properties properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream(properties_path + "/" + db_name);
            properties.load(fis);
            //Properties数据更新。对应是删除实例的场景，需要更新配置文件的主备数据
            String judgeDelete = properties.getProperty("whether_delete");
            if (judgeDelete.equals("No")) {
                properties.setProperty("whether_delete", "Yes");
            }
            properties.store(new FileOutputStream(properties_path + "/" + db_name), "update properties");
            fis.close();
            logger.info("update delete properties success");
        } catch (Exception e) {
            logger.error("update delete properties failed", e);
        }
    }
}
