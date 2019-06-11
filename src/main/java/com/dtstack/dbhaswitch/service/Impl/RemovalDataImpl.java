package com.dtstack.dbhaswitch.service.Impl;

import com.dtstack.dbhaswitch.mapper.InstanceDao;
import com.dtstack.dbhaswitch.mapper.RemovalDataDao;
import com.dtstack.dbhaswitch.model.Instance;
import com.dtstack.dbhaswitch.model.RemovalData;
import com.dtstack.dbhaswitch.service.RemovalConnectionService;
import com.dtstack.dbhaswitch.service.RemovalDataService;
import com.dtstack.dbhaswitch.service.SendHttpRequestService;
import com.dtstack.dbhaswitch.service.SlaveStatusService;
import com.dtstack.dbhaswitch.service.http.ResultModel;
import com.dtstack.dbhaswitch.service.http.ResultTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Service
public class RemovalDataImpl implements RemovalDataService {

    @Autowired
    private RemovalDataDao removalDataDao;

    @Autowired
    private RemovalConnectionService removalConnectionService;

    @Autowired
    private SendHttpRequestService sendHttpRequestService;

    @Autowired
    private SlaveStatusService slaveStatusService;

    @Autowired
    private InstanceDao instanceDao;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public ResultModel updateDataBase(String updateId) {
        try {
            if (testDbSynStatus(updateId)) {
                //更新数据
                deleteRemovalData(updateId);
                updateInstanceData(updateId);
                logger.info("update and removal Instance success,updateId = {}", updateId);
                return ResultTools.result(0, null);
            } else {
                deleteRemovalData(updateId);
                logger.info("update and removal Instance failed,updateId = {}", updateId);
                return ResultTools.result(500, "GTID is more than 60 seconds");
            }
        } catch (Exception e) {
            try {
                rollBack(updateId);
            } catch (Exception ex) {
                logger.error("connect the database cause error,", e);
            }
            deleteRemovalData(updateId);
            logger.error("update and removal Instance failed,updateId = {}", updateId);
            return ResultTools.result(500, "connect to the database cause an error");
        }
    }

    private boolean testDbSynStatus(String updateId) throws SQLException {
        boolean flag = false;
        RemovalData removalDataMaster = new RemovalData();
        removalDataMaster.setUpdateId(updateId);
        removalDataMaster.setDbStatus(1);
        removalDataMaster.setIsDelete("N");
        removalDataMaster = removalDataDao.selectOne(removalDataMaster);

        Connection connectionMaster = removalConnectionService.getMasterConnection(removalDataMaster.getRdsId());

        RemovalData removalDataSlave = new RemovalData();
        removalDataSlave.setUpdateId(updateId);
        removalDataSlave.setDbStatus(3);
        removalDataSlave.setIsDelete("N");
        removalDataSlave = removalDataDao.selectOne(removalDataSlave);

        Connection connectionNewMaster = removalConnectionService.getMasterConnection(removalDataSlave.getRdsId());

        boolean getBehindMaster = slaveStatusService.switchForUpdate(connectionNewMaster);

        if (getBehindMaster) {
            String masterReadonly = "set global read_only = 1";
            Statement statement = connectionMaster.createStatement();
            statement.execute(masterReadonly);
            statement.close();
            logger.info("set slave read_only success!");
        } else {
            logger.error("Second Behind Master more than 60s");
            return false;
        }

        Statement statement = connectionMaster.createStatement();
        ResultSet resultSetIo = statement.executeQuery("show global variables where variable_name = 'gtid_executed';");

        while (resultSetIo.next()) {
            String getValue = resultSetIo.getString("Value");
            if (getValue != null) {
                flag = getGTID(getValue, updateId);
            } else {
                logger.error("gtid_executed is null,return.rdsId = {}", removalDataMaster.getRdsId());
                return false;
            }
        }
        statement.close();
        connectionMaster.close();
        connectionNewMaster.close();
        return flag;
    }

    private boolean getGTID(String getValue, String updateId) throws SQLException {

        RemovalData removalData = new RemovalData();
        removalData.setUpdateId(updateId);
        removalData.setDbStatus(3);
        removalData.setIsDelete("N");
        removalData = removalDataDao.selectOne(removalData);

        Connection connectionSlave = removalConnectionService.getMasterConnection(removalData.getRdsId());

        Statement statement = connectionSlave.createStatement();

        String setFirstData = "SELECT WAIT_FOR_EXECUTED_GTID_SET ";

        String setLastData = " ('" + getValue + "', 30) ; ";

        ResultSet resultSetIo = statement.executeQuery(setFirstData + setLastData);

        while (resultSetIo.next()) {
            Integer getData = resultSetIo.getInt(1);
            if (getData == 1) {
                rollBack(updateId);
                return false;
            } else if (getData == 0) {
                String setReadOnlyOff = "set global read_only = 0";
                try {
                    statement.execute(setReadOnlyOff);
                    setSlaveReadOnly(updateId);
                    sendHttpRequestService.sendAddRemovalDns(removalData.getUpdateId());
                    statement.close();
                    connectionSlave.close();
                    return true;
                } catch (Exception e) {
                    logger.error("send New dns error,", e);
                }
                statement.close();
                connectionSlave.close();
            }
        }
        return false;
    }

    private void setSlaveReadOnly(String updateId) throws SQLException {
        RemovalData removalData = new RemovalData();
        removalData.setUpdateId(updateId);
        removalData.setDbStatus(4);
        removalData.setIsDelete("N");
        removalData = removalDataDao.selectOne(removalData);

        Connection connectionSlave = removalConnectionService.getSlaveConnection(removalData.getRdsId());

        Statement statement = connectionSlave.createStatement();
        String setReadOnlyOn = "set global read_only = 1";
        statement.execute(setReadOnlyOn);
    }

    private void deleteRemovalData(String updateId) {
        RemovalData removalData = new RemovalData();
        removalData.setUpdateId(updateId);
        removalData.setIsDelete("N");
        List<RemovalData> removalDataList = removalDataDao.select(removalData);
        for (RemovalData removalDataTmp : removalDataList) {
            removalDataTmp.setIsDelete("Y");
            removalDataDao.update(removalDataTmp);
            if (removalDataTmp.getDbStatus() == 1) {
                Instance instance = new Instance();
                instance.setRdsId(removalDataTmp.getRdsId());
                List<Instance> instanceList = instanceDao.select(instance);
                for (Instance instanceTmp : instanceList) {
                    instanceTmp.setIsDelete("Y");
                    instanceDao.update(instanceTmp);
                }
            }
        }
    }

    private void updateInstanceData(String updateId) {
        RemovalData removalData = new RemovalData();
        removalData.setDbStatus(3);
        removalData.setUpdateId(updateId);
        removalData = removalDataDao.selectOne(removalData);
        Long rdsId = removalData.getRdsId();
        if (rdsId == null) {
            return;
        }
        Instance instance = new Instance();
        instance.setRdsId(rdsId);
        List<Instance> instanceList = instanceDao.select(instance);
        List<String> getIp = new ArrayList<>();
        for (Instance instanceTmp : instanceList) {
            getIp.add(instanceTmp.getIp());
        }
        for (Instance instanceTmp : instanceList) {
            instanceTmp.setRdsUrl(removalData.getRdsUrl());
            if (instanceTmp.getIp() == getIp.get(0)) {
                instanceTmp.setBrotherIp(getIp.get(1));
            } else {
                instanceTmp.setBrotherIp(getIp.get(0));
            }
            instanceTmp.setRdsStatus(0);
            instanceDao.update(instanceTmp);
        }
    }

    private void rollBack(String updateId) throws SQLException {
        RemovalData removalData = new RemovalData();
        removalData.setDbStatus(1);
        removalData.setUpdateId(updateId);
        removalData.setIsDelete("N");
        removalData = removalDataDao.selectOne(removalData);

        Connection connectionMaster = removalConnectionService.getMasterConnection(removalData.getRdsId());

        String masterReadonly = "set global read_only = 0";
        Statement statement = connectionMaster.createStatement();
        statement.execute(masterReadonly);
        logger.info("roll back ,set master read_only off success!");
        statement.close();
        connectionMaster.close();
    }
}
