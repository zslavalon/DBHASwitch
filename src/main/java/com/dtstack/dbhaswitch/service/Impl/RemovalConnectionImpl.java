package com.dtstack.dbhaswitch.service.Impl;

import com.dtstack.dbhaswitch.mapper.RemovalDataDao;
import com.dtstack.dbhaswitch.model.RemovalData;
import com.dtstack.dbhaswitch.service.RemovalConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;

@Service
public class RemovalConnectionImpl implements RemovalConnectionService {

    @Autowired
    private RemovalDataDao removalDataDao;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Connection getMasterConnection(Long rdsId) {

        RemovalData removalData = new RemovalData();
        removalData.setRdsId(rdsId);
        removalData.setJudgeMaster(1);
        removalData.setIsDelete("N");
        removalData = removalDataDao.selectOne(removalData);

        try {
            String driver = "com.mysql.jdbc.Driver";
            String jdbcUrl = "jdbc:mysql://" + removalData.getIp() + ":" + removalData.getPort() + "?useUnicode=true&characterEncoding=gbk&zeroDateTimeBehavior=convertToNull";
            String user = removalData.getUserName();
            String password = removalData.getPassWord();
            logger.info("lead master properties data,urlRds={},username={}", jdbcUrl, user);
            Class.forName(driver);
            return DriverManager.getConnection(jdbcUrl, user, password);
        } catch (Exception e) {
            logger.error("create connection failed", e);
        }
        return null;
    }

    @Override
    public Connection getSlaveConnection(Long rdsId) {
        RemovalData removalData = new RemovalData();
        removalData.setRdsId(rdsId);
        removalData.setJudgeMaster(2);
        removalData.setIsDelete("N");
        removalData = removalDataDao.selectOne(removalData);
        try {
            String driver = "com.mysql.jdbc.Driver";
            String jdbcUrl = "jdbc:mysql://" + removalData.getIp() + ":" + removalData.getPort() + "?useUnicode=true&characterEncoding=gbk&zeroDateTimeBehavior=convertToNull";
            String user = removalData.getUserName();
            String password = removalData.getPassWord();
            logger.info("lead master properties data,urlRds={},username={}", jdbcUrl, user);
            Class.forName(driver);
            return DriverManager.getConnection(jdbcUrl, user, password);
        } catch (Exception e) {
            logger.error("mysql slave connection going wrong", e);
            return null;
        }
    }
}
