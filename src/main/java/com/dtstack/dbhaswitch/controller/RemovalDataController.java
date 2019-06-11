package com.dtstack.dbhaswitch.controller;

import com.dtstack.dbhaswitch.controller.utils.Controllers;
import com.dtstack.dbhaswitch.mapper.InstanceDao;
import com.dtstack.dbhaswitch.mapper.RemovalDataDao;
import com.dtstack.dbhaswitch.model.Instance;
import com.dtstack.dbhaswitch.model.RemovalData;
import com.dtstack.dbhaswitch.service.RemovalDataService;
import com.dtstack.dbhaswitch.service.http.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(value = Controllers.API_HA + "/highavailability/instances")
public class RemovalDataController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RemovalDataDao removalDataDao;

    @Autowired
    private InstanceDao instanceDao;

    @Autowired
    private RemovalDataService removalDataService;

    @RequestMapping(value = {"/update"}, method = RequestMethod.POST)
    public ResultModel removalInstance(@RequestBody List<RemovalData> removalData) throws SQLException {
        StringBuffer stringBuffer = new StringBuffer();
        for (RemovalData removalDataGet : removalData) {
            if (removalDataGet.getDbStatus() == 1 || removalDataGet.getDbStatus() == 2) {
                Instance instance = new Instance();
                instance.setRdsCode(removalDataGet.getRdsCode());
                instance.setJudgeMaster(removalDataGet.getJudgeMaster());
                instance = instanceDao.selectOne(instance);

                removalDataGet.setJudgeMaster(instance.getJudgeMaster());
                removalDataGet.setHostName(instance.getHostName());
                removalDataGet.setInstanceId(instance.getInstanceId());
                removalDataGet.setIp(instance.getIp());
                removalDataGet.setPort(instance.getPort());
                removalDataGet.setUserName(instance.getUserName());
                removalDataGet.setPassWord(instance.getPassWord());
                removalDataGet.setRdsUrl(instance.getRdsUrl());
                removalDataGet.setInstanceId(instance.getInstanceId());
            } else {
                Instance instance = new Instance();
                instance.setRdsId(removalDataGet.getRdsId());
                instance.setRdsCode(removalDataGet.getRdsCode());
                instance.setJudgeMaster(removalDataGet.getJudgeMaster());
                instance.setInstanceId(removalDataGet.getInstanceId());
                instance.setUserName(removalDataGet.getUserName());
                instance.setRdsUrl(removalDataGet.getRdsUrl());
                instance.setPort(removalDataGet.getPort());
                instance.setIp(removalDataGet.getIp());
                instance.setPassWord(removalDataGet.getPassWord());
                instance.setHostName(removalDataGet.getHostName());
                instance.setRdsStatus(3);
                instanceDao.insert(instance);
            }
            stringBuffer.append(removalDataGet.getRdsId());
        }
        String getUpdateId = stringBuffer.toString();
        for (RemovalData removalDataGet : removalData) {
            removalDataGet.setUpdateId(getUpdateId);
            removalDataDao.insert(removalDataGet);
        }

        return removalDataService.updateDataBase(getUpdateId);

    }
}
