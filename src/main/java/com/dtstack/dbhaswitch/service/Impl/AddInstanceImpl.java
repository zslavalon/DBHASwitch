package com.dtstack.dbhaswitch.service.Impl;

import com.dtstack.dbhaswitch.mapper.InstanceDao;
import com.dtstack.dbhaswitch.model.Instance;
import com.dtstack.dbhaswitch.service.AddInstanceService;
import com.dtstack.dbhaswitch.service.ThreadMainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AddInstanceImpl implements AddInstanceService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private InstanceDao instanceDao;

    @Autowired
    private ThreadMainService threadMainService;

    @Override
    public void AddInstanceToDataBase(Instance instance) {
        //暂时弃用
        addInstanceFlow(instance);
        threadInstance(instance);
    }

    @Override
    public void threadMain(Instance instance) {
        logger.info("create a new Thread,instance = {}", instance.getRdsCode() + instance.getIp());

        threadInstance(instance);
    }

    public void addInstanceFlow(Instance instance) {
        //实例入库
        instanceDao.insert(instance);
        logger.info("Write to database success，InstanceId={}, Ip={}, port={}", instance.getRdsCode(), instance.getIp(), instance.getPort());
    }

    public void deleteInstanceFlow(Long rdsId) {
        Instance instance = new Instance();
        instance.setRdsId(rdsId);
        List<Instance> instanceList = instanceDao.select(instance);
        for (Instance instanceTmp : instanceList) {
            instanceTmp.setIsDelete("Y");
            instanceDao.update(instanceTmp);
        }
    }

    private void threadInstance(Instance instance) {
        try {
            threadMainService.getConnection(instance);
            logger.info("create a new Thread success,instance name is {}", instance.getRdsCode() + "_" + instance.getJudgeMaster());
        } catch (Exception e) {
            logger.error("create a new Thread failed,instance name is {}", instance.getRdsCode() + "_" + instance.getJudgeMaster(), e);
        }
    }
}
