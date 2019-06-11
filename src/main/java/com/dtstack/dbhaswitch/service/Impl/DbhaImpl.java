package com.dtstack.dbhaswitch.service.Impl;

import com.dtstack.dbhaswitch.mapper.InstanceDao;
import com.dtstack.dbhaswitch.model.Instance;
import com.dtstack.dbhaswitch.model.SwitchStatus;
import com.dtstack.dbhaswitch.service.*;
import com.dtstack.dbhaswitch.utils.TimeTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DbhaImpl implements DbhaService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    boolean getbool = true;

    @Autowired
    private SlaveStatusService slaveStatusService;

    @Autowired
    private InstanceDao instanceDao;

    @Autowired
    private SendHttpRequestService sendHttpRequestService;

    @Override
    public synchronized List switchLogic(Instance instance) throws SQLException {
        List listLog = new ArrayList();
        /**
         * 此处进行主备切换
         * */
        boolean getStatus = slaveStatusService.isGetStatus(instance.getRdsId());
        listLog.addAll(slaveStatusService.getList());
        if (getStatus == true) {
            logger.info("update success");
            listLog.add(new Date() + ",update success。");
        } else {
            logger.error("switch failed,instance={}", instance);
            listLog.add(new Date() + ",update the DNS failed,instance rdsID = " + instance.getRdsId() + "。");
            getbool = false;
        }
        return listLog;
    }

    @Override
    public boolean isGetbool() {
        return getbool;
    }

    @Override
    public synchronized SwitchStatus instanceValuation(Instance instance) {
        SwitchStatus switchStatus = new SwitchStatus();
        switchStatus.setSwitchTimeBegin(TimeTransform.getTime());
        switchStatus.setMasterIp(instance.getIp());
        switchStatus.setRdsId(instance.getRdsId());
        switchStatus.setSlaveIp(instance.getBrotherIp());
        switchStatus.setPort(instance.getPort());
        switchStatus.setHostName(instance.getHostName());
        switchStatus.setRdsCode(instance.getRdsCode());
        return switchStatus;
    }

    @Override
    public SwitchStatus autoSwitchFailed(SwitchStatus switchStatus) {
        switchStatus.setSwitchResult("失败");
        switchStatus.setSchedule("50%");
        switchStatus.setSwtichInfo("主实例连接超时");
        switchStatus.setSwitchType("故障倒换");
        switchStatus.setSwitchTimeEnd(TimeTransform.getTime());
        sendHttpRequestService.sendMonitorEvent(switchStatus.getRdsId());
        return switchStatus;
    }

    @Override
    public SwitchStatus switchSucced(SwitchStatus switchStatus) {
        switchStatus.setSwitchResult("成功");
        switchStatus.setSwitchType("故障倒换");
        switchStatus.setSwtichInfo("主实例连接超时");
        switchStatus.setSchedule("100%");
        switchStatus.setSwitchTimeEnd(TimeTransform.getTime());
        sendHttpRequestService.sendMonitorEvent(switchStatus.getRdsId());
        return switchStatus;
    }

    @Override
    public void updateMasterAndSlave(Long rdsId) {
        Instance instance = new Instance();
        instance.setRdsId(rdsId);
        List<Instance> instanceList = instanceDao.select(instance);
        for (Instance instanceTmp : instanceList) {
            if (instanceTmp.getJudgeMaster() == 1) {
                instanceTmp.setJudgeMaster(2);
                instanceDao.update(instanceTmp);
            } else {
                instanceTmp.setJudgeMaster(1);
                instanceDao.update(instanceTmp);
            }
        }
    }
}
