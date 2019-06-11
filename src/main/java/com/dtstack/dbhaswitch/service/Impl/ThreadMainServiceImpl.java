package com.dtstack.dbhaswitch.service.Impl;

import com.dtstack.dbhaswitch.mapper.InstanceDao;
import com.dtstack.dbhaswitch.mapper.SwitchStatusDao;
import com.dtstack.dbhaswitch.model.Instance;
import com.dtstack.dbhaswitch.model.SwitchStatus;
import com.dtstack.dbhaswitch.service.*;
import com.dtstack.dbhaswitch.utils.ConversionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Scope("prototype")//spring 多例
@Service
public class ThreadMainServiceImpl implements ThreadMainService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SlaveStatusService slaveStatusService;

    @Autowired
    private MasterStatusService masterStatusService;

    @Autowired
    private InfoService infoService;

    @Autowired
    private ConversionUtils conversionUtils;

    @Autowired
    private SwitchStatusDao switchStatusDao;

    @Autowired
    private DbhaService dbhaService;

    @Autowired
    private SendHttpRequestService sendHttpRequestService;

    @Autowired
    private InstanceDao instanceDao;

    @Override
    @Async("asyncServiceExecutor")
    public void getConnection(Instance instance) {

        try {
            //根据配置文件读取参数信息，连接实例
            /**
             * 因为主备实例的状态判断规则不同，分别进行实例连接
             * */
//            Integer getStatus = judgeDelete(instance.getRdsId());
            if (instance.getIsDelete() == "Y") {
                return;
            }
            if (instance.getJudgeMaster().equals(1)) {
                try {
                    masterStatusService.MasterStatus(infoService.setConnectionMaster(instance.getRdsId()));
                } catch (Exception e) {
                    //表明主实例连接已经出现异常，由catch进行捕获，进入倒换逻辑
                    logger.error("Begin the switch judgment", e);
                    List listLog = new ArrayList();
                    try {
                        SwitchStatus switchStatus = dbhaService.instanceValuation(instance);
                        //进入具体倒换逻辑
                        listLog.addAll(dbhaService.switchLogic(instance));
                        if (dbhaService.isGetbool() == false) {
                            switchStatus = dbhaService.autoSwitchFailed(switchStatus);
                            listLog.add(new Date() + "master error switch to slave failed,");
                            switchStatus.setLog(listLog);
                            switchStatus.setLogStr(conversionUtils.listToString(listLog));
                            switchStatusDao.insert(switchStatus);
                            instance.setRdsStatus(1);
                            instanceDao.update(instance);
                            return;
                        }
                        switchStatus = dbhaService.switchSucced(switchStatus);
                        listLog.add(new Date() + "master error switch to slave success。");
                        switchStatus.setLogStr(conversionUtils.listToString(listLog));
                        //倒换信息入库
                        switchStatusDao.insert(switchStatus);
                        dbhaService.updateMasterAndSlave(instance.getRdsId());
                        sendHttpRequestService.sendUpdateDns(instance.getRdsId());
                        logger.info("send Update DNS success,rdsId = {}", instance.getRdsId());
                        sendHttpRequestService.sendNewMaster(instance.getRdsId());
                        logger.info("send Update RDS database success,rdsId = {}", instance.getRdsId());
                        logger.info("Switch success,rdsId = {}", instance.getRdsId());
                        return;
                    } catch (Exception ex) {
                        logger.error("master error switch to slave failed", ex);
                        SwitchStatus switchStatus = dbhaService.instanceValuation(instance);
                        switchStatus = dbhaService.autoSwitchFailed(switchStatus);
                        listLog.add(new Date() + "master error switch to slave failed," + ex);
                        switchStatus.setLog(listLog);
                        switchStatus.setLogStr(conversionUtils.listToString(listLog));
                        switchStatusDao.insert(switchStatus);
                        instance.setRdsStatus(1);
                        instanceDao.update(instance);
                    }
                }
            } else if (instance.getJudgeMaster().equals(2)) {
                //备实例连接
                logger.info("connect to the slave database,name={}", instance.getRdsCode());
                slaveStatusService.SlaveStatus(infoService.setConnectionSlave(instance.getRdsId()), instance.getRdsId());
            }
        } catch (Exception e) {
            logger.error("Unable to create database connection,Instance name ={}", instance.getRdsCode() + "_" + instance.getRdsId(), e);
        }
    }
}
