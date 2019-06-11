package com.dtstack.dbhaswitch.service.Impl;

import com.dtstack.dbhaswitch.service.InfoService;
import com.dtstack.dbhaswitch.service.SendHttpRequestService;
import com.dtstack.dbhaswitch.service.SlaveStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SlaveStatusImpl implements SlaveStatusService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SendHttpRequestService sendHttpRequestService;

    @Autowired
    private InfoService infoService;

    List list = new ArrayList();

    private int setTime = 0;

    @Override
    public List getList() {
        return list;
    }

    @Override
    public void SlaveReadOnly(Long rdsId) throws SQLException {
        Connection connection = infoService.setConnectionSlave(rdsId);
        Statement statement = connection.createStatement();
        try {
            statement.execute("set global read_only = 1");
            logger.info("set slave read_only success!");
        } catch (Exception e) {
            logger.error("set slave read_only failed", e);
        }
        statement.close();
        connection.close();
    }

    @Override
    public void SlaveStatus(Connection connection, Long rdsId) throws SQLException {
        try {
            if (!connection.isClosed()) {
                System.out.println("Succeeded connecting to the slave Database!");
                logger.info("Succeeded connecting to the slave Database ! connection = {}", connection);
            }
        } catch (Exception e) {
            logger.error("连接数据库失败", e);
            return;
        }
        if (setTime >= 5) {
            logger.error("slave Instance can not connect to the master,retry over 1 minutes,break down slave Instance");
            //备实例IO异常
            sendHttpRequestService.sendSlaveIoError(rdsId, 1, 2);
            return;
        }
        Statement statement = connection.createStatement();
        //延时或循环执行的内容
        try {
            ResultSet resultSetIo = statement.executeQuery("show slave status;");
            while (resultSetIo.next()) {
                String getIoValue = resultSetIo.getString("Slave_IO_Running");
                String getSqlValue = resultSetIo.getString("Slave_SQL_Running");

                if (!getIoValue.equals("Yes") || !getSqlValue.equals("Yes")) {
                    logger.warn("Slave Instance synchronization has exception,Slave_IO_Running = {},Slave_SQL_Running = {}", getIoValue, getSqlValue);
                    logger.warn("slave Instance can not connect to the master ,retry time = {}", setTime);
                    setTime++;
                    SlaveStatus(connection, rdsId);
                }
            }
            if (setTime > 0) {
                //备实例IO恢复正常
                logger.warn("send Slave Io Error,running status is Semi-sync");
                String rplSql = "show status like '%Rpl_semi_sync_master_status%';";
                ResultSet resultSetRpl = statement.executeQuery(rplSql);
                while (resultSetRpl.next()) {
                    String getResult = resultSetRpl.getString("Value");
                    if (getResult == "OFF" || getResult.equals("OFF")) {
                        logger.warn("send Slave Io Error,running status is asynchronous");
                        sendHttpRequestService.sendSlaveIoError(rdsId, 1, 1);
                    } else {
                        sendHttpRequestService.sendSlaveIoError(rdsId, 2, 1);
                    }
                }
                setTime = 0;
            }
        } catch (Exception e) {
            logger.error("slave status cause error,", e);
        } finally {
            statement.close();
            connection.close();
        }
    }

    @Override
    public synchronized boolean slaveTimeStatus(Long rdsId) throws SQLException {
        Connection connection = infoService.setConnectionSlave(rdsId);
        if (!connection.isClosed()) {
            logger.info("Succeeded connecting to the Slave Database!");
            list.add(new Date() + ",Succeeded connecting to the Slave Database!");
        }

        Statement statement = connection.createStatement();
        String getTime = "select time from heartbeat;";
        ResultSet rs = statement.executeQuery(getTime);
        logger.info("get slave database date_time in table");
        while (rs.next()) {
            String s = rs.getString("time");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date db_time = sdf.parse(s);
                Date system_time = new Date();
                long time = system_time.getTime() - db_time.getTime();
                long exTime = db_time.getTime() - system_time.getTime();
                logger.info("Master Abnormal Switch System time={},SimpleDateFormat time ={}", system_time.getTime(), system_time);
                list.add(new Date() + ",Master Abnormal Switch System time = " + system_time + "。");
                logger.warn("Master Abnormal Switch Slave database IO Syn time={},SimpleDateFormat time ={}", db_time.getTime(), db_time);
                list.add(new Date() + ",Master Abnormal Switch Slave database IO Syn time =" + db_time + "。");
                long minute = time / (1000 * 60);
                long exMinute = exTime / (1000 * 60);
                if (minute > 3 || exMinute > 3) {
                    /**无法倒换*/
                    logger.error("Data Synchronism time more than 60 second，can not switch,failed.time={}", minute);
                    list.add(new Date() + ",Data Synchronism time more than 60 second，can not switch,failed.time = " + minute + "。");
                    statement.close();
                    connection.close();
                    return false;
                }
                logger.info("Data Synchronism time less than 60 second,begin to switch,time={}", minute);
                list.add(new Date() + ",Data Synchronism time less than 60 second,begin to switch,time = " + minute + "。");
            } catch (Exception e) {
                logger.error("Handle timestamp exceptions", e);
                list.add(new Date() + ",Handle timestamp exceptions," + e);
                statement.close();
                connection.close();
                return false;
            }
        }
        statement.close();
        connection.close();
        return true;
    }

    private boolean doSwitch(Long rdsId) throws SQLException {
        if (slaveTimeStatus(rdsId) == true) {
            /**倒换逻辑处理开始*/
            makeSlaveReadOnly(rdsId);
            return true;
        } else {
            return false;
        }

    }

    private void makeSlaveReadOnly(Long rdsId) throws SQLException {
        Connection connection = infoService.setConnectionSlave(rdsId);
        Statement statement = connection.createStatement();
        logger.info("make slave database read_only off");
        statement.execute("set global read_only=0;");
        list.add(new Date() + ",make slave database read_only off success。");
        statement.close();
        connection.close();
    }

    @Override
    public boolean isGetStatus(Long rdsId) throws SQLException {
        return doSwitch(rdsId);
    }

    @Override
    public boolean switchForUpdate(Connection connection) throws SQLException {

        if (!connection.isClosed()) {
            System.out.println("Succeeded connecting to the Database!");
        }

        Statement statement = connection.createStatement();
        ResultSet resultSetIo = statement.executeQuery("show slave status;");

        while (resultSetIo.next()) {
            String getIoValue = resultSetIo.getString("Slave_IO_Running");
            String getSqlValue = resultSetIo.getString("Slave_SQL_Running");
            String getSecondBehind = resultSetIo.getString("Seconds_Behind_Master");
            logger.warn("slave database status,IO_Thread={}", getIoValue);
            logger.warn("slave database status,SQL_Thread={}", getSqlValue);
            if (!getSqlValue.equals("Yes") || !getIoValue.equals("Yes")) {
                return false;
            } else if (Integer.valueOf(getSecondBehind) > 60) {
                return false;
            }
        }
        statement.close();
        return true;
    }
}
