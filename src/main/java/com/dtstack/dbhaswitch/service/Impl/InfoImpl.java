package com.dtstack.dbhaswitch.service.Impl;

import com.dtstack.dbhaswitch.mapper.InstanceDao;
import com.dtstack.dbhaswitch.model.Instance;
import com.dtstack.dbhaswitch.service.InfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;

@Service
public class InfoImpl implements InfoService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private InstanceDao instanceDao;

    @Override
    public Connection setConnectionMaster(Long rdsId) {
        try {
            Instance instanceMaster = new Instance();
            instanceMaster.setRdsId(rdsId);
            instanceMaster.setJudgeMaster(1);
            instanceMaster = instanceDao.selectOne(instanceMaster);
            String driver = "com.mysql.jdbc.Driver";
            String jdbcUrl = "jdbc:mysql://" + instanceMaster.getIp() + ":" + instanceMaster.getPort() + "/rds_ha" + "?useUnicode=true&characterEncoding=gbk&zeroDateTimeBehavior=convertToNull";
            String user = instanceMaster.getUserName();
            String password = instanceMaster.getPassWord();
            logger.info("lead master data,urlRds={},username={}", jdbcUrl, user);
            Class.forName(driver);
            return DriverManager.getConnection(jdbcUrl, user, password);
        } catch (Exception e) {
            logger.error("mysql master connection going wrong,rdsId = {} ,", rdsId, e);
            return null;
        }
    }

    @Override
    public Connection setConnectionSlave(Long rdsId) {
        try {
            Instance instanceSlave = new Instance();
            instanceSlave.setRdsId(rdsId);
            instanceSlave.setJudgeMaster(2);
            instanceSlave = instanceDao.selectOne(instanceSlave);
            String driver = "com.mysql.jdbc.Driver";
            String jdbcUrl = "jdbc:mysql://" + instanceSlave.getIp() + ":" + instanceSlave.getPort() + "/rds_ha" + "?useUnicode=true&characterEncoding=gbk&zeroDateTimeBehavior=convertToNull";
            String user = instanceSlave.getUserName();
            String password = instanceSlave.getPassWord();
            logger.info("lead slave data,urlRds={},username={}", jdbcUrl, user);
            Class.forName(driver);
            return DriverManager.getConnection(jdbcUrl, user, password);
        } catch (Exception e) {
            logger.error("mysql slave connection going wrong,rdsId = {} ,", rdsId, e.getMessage());
            return null;
        }
    }
}
