package com.dtstack.dbhaswitch.controller;

import com.dtstack.dbhaswitch.controller.utils.Controllers;
import com.dtstack.dbhaswitch.mapper.InstanceDao;
import com.dtstack.dbhaswitch.model.Instance;
import com.dtstack.dbhaswitch.service.AddInstanceService;
import com.dtstack.dbhaswitch.service.SendHttpRequestService;
import com.dtstack.dbhaswitch.service.SlaveStatusService;
import com.dtstack.dbhaswitch.service.http.ResultModel;
import com.dtstack.dbhaswitch.service.http.ResultTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = Controllers.API_HA + "/highavailability/instances")
public class InstanceController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private InstanceDao instanceDao;

    @Autowired
    private AddInstanceService addInstanceService;

    @Autowired
    private SendHttpRequestService sendHttpRequestService;

    @Autowired
    private SlaveStatusService slaveStatusService;

    @RequestMapping(value = {"/add"}, method = RequestMethod.POST)
    public ResultModel addInstance(@RequestBody List<Instance> instances) {
        //创建实例的接口，所有DBHA倒换的入口
        Long rdsId = null;
        for (Instance instance : instances) {
            Map<String, Object> map_tmp = new HashMap<>();
            try {
                addInstanceService.addInstanceFlow(instance);
                rdsId = instance.getRdsId();
            } catch (Exception e) {
                map_tmp.put("data", instance);
                logger.error("Instance Data entry failed:", e);
                return ResultTools.result_map(map_tmp, 1002, instance.getRdsCode() + ":实例已存在");
            }
        }
        try {
            slaveStatusService.SlaveReadOnly(rdsId);
            boolean getMark = sendHttpRequestService.sendAddDns(rdsId);
            if (getMark == false) {
                addInstanceService.deleteInstanceFlow(instances.get(0).getRdsId());
                return ResultTools.result(500, "add DNS failed");
            }
        } catch (Exception e) {
            logger.error("receive instance cause error", e);
            return ResultTools.result(500, String.valueOf(e));
        }

        return ResultTools.result(0, "成功");
    }

    @RequestMapping(value = {"/delete"}, method = RequestMethod.POST)
    public ResultModel deleteInstance(@RequestBody Instance instancePost) {
        //删除实例
        try {
            Instance instance = new Instance();
            instance.setRdsId(instancePost.getRdsId());
            List<Instance> instanceList = instanceDao.select(instance);
            for (Instance instanceTmp : instanceList) {
                instanceTmp.setIsDelete("Y");
                instanceDao.update(instanceTmp);
                if (instanceTmp.getJudgeMaster() == 1) {
                    sendHttpRequestService.sendDeleteDns(instanceTmp.getRdsId());
                }
            }
            return ResultTools.result(0, "");
        } catch (Exception e) {
            return ResultTools.result(404, e.getMessage());
        }
    }
}
