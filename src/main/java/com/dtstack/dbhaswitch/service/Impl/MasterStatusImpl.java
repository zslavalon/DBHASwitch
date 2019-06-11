package com.dtstack.dbhaswitch.service.Impl;

import com.dtstack.dbhaswitch.mapper.InstanceDao;
import com.dtstack.dbhaswitch.mapper.SwitchStatusDao;
import com.dtstack.dbhaswitch.model.Instance;
import com.dtstack.dbhaswitch.model.SwitchStatus;
import com.dtstack.dbhaswitch.service.*;
import com.dtstack.dbhaswitch.service.http.ResultModel;
import com.dtstack.dbhaswitch.service.http.ResultTools;
import com.dtstack.dbhaswitch.utils.ConversionUtils;
import com.dtstack.dbhaswitch.utils.TimeTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.Date;

@Service
public class MasterStatusImpl implements MasterStatusService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DbhaService dbhaService;

    @Autowired
    private InfoService infoService;

    @Autowired
    private ConversionUtils conversionUtils;

    @Autowired
    private SendHttpRequestService sendHttpRequestService;

    @Autowired
    private SwitchStatusDao switchStatusDao;

    @Autowired
    private InstanceDao instanceDao;

    @Autowired
    private SlaveStatusService slaveStatusService;

    List list = new ArrayList();

    @Override
    public synchronized void MasterStatus(Connection connection) throws SQLException {

        if (!connection.isClosed()) {
            System.out.println("Succeeded connecting to the master Database!");
            logger.info("Succeeded connecting to the master Database!connection = {}", connection);
        }
        PreparedStatement preparedStatement = connection.prepareStatement("update heartbeat set time=now(),dts=unix_timestamp(now());");
        preparedStatement.execute();
        preparedStatement.close();
        connection.close();
    }

    @Override
    public synchronized ResultModel forceSwitch(Long rdsId) {
        SwitchStatus switchStatus = new SwitchStatus();
        Integer getMark = 0;
        try {
            Instance instance = new Instance();
            instance.setRdsId(rdsId);
            instance.setJudgeMaster(1);
            instance = instanceDao.selectOne(instance);
            switchStatus = dbhaService.instanceValuation(instance);
            MakeMasterReadonlyOn(rdsId);
            MakeSlaveReadOnlyOff(rdsId);
            dbhaService.updateMasterAndSlave(rdsId);
            getMark = getMark + sendHttpRequestService.sendUpdateDns(rdsId);
            getMark = getMark + sendHttpRequestService.sendNewMaster(rdsId);
            if (getMark > 0) {
                rollBack(rdsId);
                switchStatus.setLogStr(conversionUtils.listToString(list));
                switchStatus.setSwitchResult("失败");
                switchStatus.setSwitchType("手动倒换");
                switchStatus.setSchedule("90%");
                switchStatus.setSwitchTimeEnd(TimeTransform.getTime());
                switchStatusDao.insert(switchStatus);
                return ResultTools.result(500, "update DNS or database failed");
            }
            switchStatus.setSwitchType("手动倒换");
            switchStatus.setSwitchResult("成功");
            switchStatus.setSchedule("100%");
            switchStatus.setSwitchTimeEnd(TimeTransform.getTime());
            switchStatus.setLogStr(conversionUtils.listToString(list));

            switchStatusDao.insert(switchStatus);

            logger.info("Manual Switch success");
            return ResultTools.result(0, "Force switch success");

        } catch (Exception e) {
            rollBack(rdsId);
            logger.error("Manual Switch,switch failed", e);
            switchStatus.setLogStr(conversionUtils.listToString(list));
            switchStatus.setSwitchResult("失败");
            switchStatus.setSwitchType("手动倒换");
            switchStatus.setSchedule("50%");
            switchStatus.setSwitchTimeEnd(TimeTransform.getTime());
            switchStatusDao.insert(switchStatus);
            return ResultTools.result(500, "switch instance failed" + e);
        }
    }


    @Override
    public synchronized ResultModel manualSwtich(Long rdsId) {
        SwitchStatus switchStatus = new SwitchStatus();
        Integer getMark = 0;
        try {
            Instance instance = new Instance();
            instance.setRdsId(rdsId);
            instance.setJudgeMaster(1);
            instance = instanceDao.selectOne(instance);
            boolean getFlag = slaveStatusService.slaveTimeStatus(rdsId);
            list.addAll(slaveStatusService.getList());
            switchStatus = dbhaService.instanceValuation(instance);
            if (!getFlag) {
                switchStatus.setLogStr(conversionUtils.listToString(list));
                switchStatus.setSwitchResult("失败");
                switchStatus.setSwitchType("手动倒换");
                switchStatus.setSchedule("10%");
                switchStatus.setSwitchTimeEnd(TimeTransform.getTime());
                switchStatusDao.insert(switchStatus);
                return ResultTools.result(500, "slave database behind master over 180 second");
            }
            MakeMasterReadonlyOn(rdsId);
            MakeSlaveReadOnlyOff(rdsId);
            dbhaService.updateMasterAndSlave(rdsId);
            getMark = getMark + sendHttpRequestService.sendUpdateDns(rdsId);
            getMark = getMark + sendHttpRequestService.sendNewMaster(rdsId);
            if (getMark > 0) {
                rollBack(rdsId);
                switchStatus.setLogStr(conversionUtils.listToString(list));
                switchStatus.setSwitchResult("失败");
                switchStatus.setSwitchType("手动倒换");
                switchStatus.setSchedule("90%");
                switchStatus.setSwitchTimeEnd(TimeTransform.getTime());
                switchStatusDao.insert(switchStatus);
                return ResultTools.result(500, "update DNS or database failed");
            }
            switchStatus.setSwitchType("手动倒换");
            switchStatus.setSwitchResult("成功");
            switchStatus.setSchedule("100%");
            switchStatus.setSwitchTimeEnd(TimeTransform.getTime());
            switchStatus.setLogStr(conversionUtils.listToString(list));
            switchStatusDao.insert(switchStatus);
            logger.info("Manual Switch success");
            return ResultTools.result(0, "Hand Switch success");

        } catch (Exception e) {
            switchFailed(rdsId);
            logger.error("Manual Switch,switch failed", e);
            switchStatus.setLogStr(conversionUtils.listToString(list));
            switchStatus.setSwitchResult("失败");
            switchStatus.setSwitchType("手动倒换");
            switchStatus.setSchedule("50%");
            switchStatus.setSwitchTimeEnd(TimeTransform.getTime());
            switchStatusDao.insert(switchStatus);
            return ResultTools.result(500, "switch instance failed" + e.getMessage());
        }
    }

    private synchronized void MakeMasterReadonlyOn(Long rdsId) throws SQLException {

        Connection connectionMater = infoService.setConnectionMaster(rdsId);
        String masterReadonly = "set global read_only = 1";
        Statement statement = connectionMater.createStatement();
        statement.execute(masterReadonly);
        logger.info("Manual Switch,make master instance read_only on success");
        list.add(new Date() + ",Manual Switch,make master instance read_only on success");
        statement.close();
        connectionMater.close();
    }

    private synchronized void MakeSlaveReadOnlyOff(Long rdsId) throws SQLException {

        Connection connectionSlave = infoService.setConnectionSlave(rdsId);
        Statement statement = connectionSlave.createStatement();
        String slave_readonly = "set global read_only = 0";
        statement.execute(slave_readonly);
        logger.info("Manual Switch,make slave instance read_only off success");
        list.add(new Date() + ",Manual Switch,make slave instance read_only off success");
        statement.close();
        connectionSlave.close();
    }

    private synchronized void switchFailed(Long rdsId) {

        try {
            Connection connection_mater = infoService.setConnectionMaster(rdsId);
            String master_readonly_off = "set global read_only = 0";
            Statement statement = connection_mater.createStatement();
            statement.execute(master_readonly_off);
            logger.info("Manual Switch failed,make master instance read_only off success");
            list.add(new Date() + ",Manual Switch failed,make master instance read_only off success");
            statement.close();
            connection_mater.close();
        } catch (Exception e) {
            logger.error("Manual Switch failed,make master instance read_only off failed", e);
            list.add(new Date() + ",Manual Switch failed,make master instance read_only off failed," + e);
        }
    }

    private synchronized void rollBack(Long rdsId) {
        try {
            Connection connection_mater = infoService.setConnectionMaster(rdsId);
            String master_readonly_off = "set global read_only = 0";
            Statement statementMaster = connection_mater.createStatement();
            statementMaster.execute(master_readonly_off);
            logger.info("Manual Switch failed,rollBack,make master instance read_only off success");
            list.add(new Date() + ",Manual Switch failed,rollBack,make master instance read_only off success");
            statementMaster.close();
            connection_mater.close();

            Connection connectionSlave = infoService.setConnectionSlave(rdsId);
            Statement statementSlave = connectionSlave.createStatement();
            String slave_readonly = "set global read_only = 1";
            statementSlave.execute(slave_readonly);
            logger.info("Manual Switch,rollBack,make slave instance read_only on success");
            list.add(new Date() + ",Manual Switch,rollBack,make slave instance read_only on success");
            statementSlave.close();
            connectionSlave.close();

            dbhaService.updateMasterAndSlave(rdsId);
            sendHttpRequestService.sendNewMaster(rdsId);
            sendHttpRequestService.sendUpdateDns(rdsId);
        } catch (Exception e) {
            logger.error("Manual Switch failed,make master instance read_only off failed", e);
            list.add(new Date() + ",Manual Switch failed,make master instance read_only off failed," + e);
        }
    }
}
