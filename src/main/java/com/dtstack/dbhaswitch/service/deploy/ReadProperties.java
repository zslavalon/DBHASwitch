package com.dtstack.dbhaswitch.service.deploy;

import com.dtstack.dbhaswitch.mapper.InstanceDao;
import com.dtstack.dbhaswitch.model.Instance;
import com.dtstack.dbhaswitch.service.AddInstanceService;
import com.dtstack.dbhaswitch.service.InfoService;
import com.dtstack.dbhaswitch.service.SendHttpRequestService;
import com.dtstack.dbhaswitch.service.TestConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class ReadProperties implements InitializingBean {
    /**
     * If the file exists，read them
     */

    @Autowired
    private InstanceDao instanceDao;

    @Override
    public void afterPropertiesSet() {
        readPropertiesToInstance();
    }

    @Autowired
    private AddInstanceService addInstanceService;

    @Autowired
    private TestConnectionService testConnectionService;

    @Autowired
    private InfoService infoService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private synchronized void readPropertiesToInstance() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {

                Instance instance = new Instance();
                instance.setIsDelete("N");
                instance.setRdsStatus(0);
                List<Instance> lists = instanceDao.select(instance);
                for (Instance instanceGet : lists) {
                    //进入线程池
                    addInstanceService.threadMain(instanceGet);
                    logger.info("Process aborted，Reread data，instance = {}", instanceGet.getRdsCode());
                }

                Instance instanceError = new Instance();
                instanceError.setRdsStatus(1);
                instanceError.setIsDelete("N");
                List<Instance> instanceList = instanceDao.select(instanceError);
                for (Instance instanceTmp : instanceList) {
                    try {
                        boolean flag = testConnectionService.testConnection(infoService.setConnectionMaster(instanceTmp.getRdsId()));
                        if (flag) {
                            instanceTmp.setRdsStatus(0);
                            instanceDao.update(instanceTmp);
                        }
                    } catch (Exception e) {
                        logger.error("test connection failed,rdsId = {}", instanceTmp.getRdsId());
                    }
                }
            }
        }, 10000, 15000);
    }
}
