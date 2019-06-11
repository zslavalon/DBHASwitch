package com.dtstack.dbhaswitch.controller;

import com.dtstack.dbhaswitch.controller.utils.Controllers;
import com.dtstack.dbhaswitch.mapper.InstanceDao;
import com.dtstack.dbhaswitch.model.Instance;
import com.dtstack.dbhaswitch.service.MasterStatusService;
import com.dtstack.dbhaswitch.service.http.ResultModel;
import com.dtstack.dbhaswitch.service.http.ResultTools;
import com.dtstack.dbhaswitch.service.http.SSHConnectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping(value = Controllers.API_HA + "/highavailability")
public class InstanceSwtichController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private InstanceDao instanceDao;

    @Autowired
    private MasterStatusService masterStatusService;

    @RequestMapping(method = RequestMethod.POST, value = "/handSwitchNewPort")
    public ResultModel switchInstance(@RequestBody SSHConnectRequest sshConnectRequest) {
        Map<String, Object> map = new HashMap<>();
        try {
            return masterStatusService.manualSwtich(Long.valueOf(sshConnectRequest.getPk()));
        } catch (Exception e) {
            map.put("Error Reason", e);
        }
        return ResultTools.result_map(map, 1001, "失败");
    }

    @RequestMapping(method = RequestMethod.POST, value = "/handswitch")
    public ResultModel switchOldPort(@RequestBody List<Instance> instanceList) {
        Map<String, Object> map = new HashMap<>();
        try {
            return masterStatusService.manualSwtich(instanceList.get(0).getRdsId());
        } catch (Exception e) {
            map.put("Error Reason", e);
        }
        return ResultTools.result_map(map, 1001, "失败");
    }

    @RequestMapping(method = RequestMethod.POST, value = "/forceSwitch")
    public ResultModel forceSwitch(@RequestBody SSHConnectRequest sshConnectRequest) {
        return masterStatusService.forceSwitch(Long.valueOf(sshConnectRequest.getPk()));
    }

    @RequestMapping(value = "/forbid/{rdsId}", method = RequestMethod.POST)
    public ResultModel forbidSwitch(@PathVariable(name = "rdsId") @Valid Long rdsId) {
        try {
            logger.info("receive instruct for forbid switch,instance ID = {}", rdsId);
            Instance instance = new Instance();
            instance.setRdsId(rdsId);
            List<Instance> instanceList = instanceDao.select(instance);
            for (Instance instanceTmp : instanceList) {
                instanceTmp.setIsDelete("Y");
                instanceDao.update(instanceTmp);
            }
        } catch (Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("Error Reason", e);
            return ResultTools.result_map(map, 1001, "失败");
        }
        return ResultTools.result(0, "成功");
    }

    @RequestMapping(value = "/cancelForbid/{rdsId}", method = RequestMethod.POST)
    public ResultModel cancelForbidSwitch(@PathVariable(name = "rdsId") @Valid Long rdsId) {
        try {
            logger.info("receive instruct for cancel forbid switch,instance ID = {}", rdsId);
            Instance instance = new Instance();
            instance.setRdsId(rdsId);
            List<Instance> instanceList = instanceDao.select(instance);
            for (Instance instanceTmp : instanceList) {
                instanceTmp.setIsDelete("N");
                instanceDao.update(instanceTmp);
            }
        } catch (Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("Error Reason", e);
            return ResultTools.result_map(map, 1001, "失败");
        }
        return ResultTools.result(0, "成功");
    }
}
