package com.dtstack.dbhaswitch.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dtstack.dbhaswitch.controller.utils.Controllers;
import com.dtstack.dbhaswitch.mapper.SwitchStatusDao;
import com.dtstack.dbhaswitch.model.SwitchLogModel;
import com.dtstack.dbhaswitch.model.SwitchStatus;
import com.dtstack.dbhaswitch.service.SlaveStatusService;
import com.dtstack.dbhaswitch.service.http.ResultModel;
import com.dtstack.dbhaswitch.service.http.ResultTools;
import com.dtstack.dbhaswitch.service.http.SSHConnectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = Controllers.API_HA + "/highavailability")
public class SwtichStatusController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SwitchStatusDao switchStatusDao;

    @Autowired
    private SlaveStatusService slaveStatusService;

    @RequestMapping(value = {"/page"}, method = RequestMethod.POST)
    public ResultModel selectInstanceByAll(@RequestBody SSHConnectRequest sshConnectRequest) {
        try {
            Map<String, Object> map = new HashMap<>();
            SwitchStatus switchStatus = new SwitchStatus();
            switchStatus.setRdsId(Long.valueOf(sshConnectRequest.getPk()));
            List<SwitchStatus> switchStatusList = switchStatusDao.select(switchStatus);
            int size = switchStatusList.size();
            if (size == 0) {
                map.put("data", null);
                map.put("total", 0);
                return ResultTools.result_map(map, 0, "");
            }
            JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(switchStatusList));
            map.put("data", jsonArray);
            map.put("total", size);
            return ResultTools.result_map(map, 0, "");
        } catch (Exception e) {
            logger.error("SwitchStatus Port request failed:", e);
            return ResultTools.result_map(null, 404, e.getMessage());
        }
    }

    @RequestMapping(value = {"/switchLog"}, method = RequestMethod.POST)
    public ResultModel selectLogByAll(@RequestBody SwitchLogModel switchLogModel) {
        try {
            List<SwitchStatus> switchStatusList = switchStatusDao.sendLog(switchLogModel);
            int size = switchStatusList.size();
            JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(switchStatusList));
            HashMap<String, Object> map = new HashMap<>();
            map.put("data", jsonArray);
            map.put("total", size);
            return ResultTools.result_map(map, 0, "");
        } catch (Exception e) {
            logger.error("SwitchStatus Port request failed:", e);
            return ResultTools.result_map(null, 404, e.getMessage());
        }
    }

    @RequestMapping(value = {"/behindMaster"}, method = RequestMethod.POST)
    public ResultModel secondBehindMaster(@RequestBody SSHConnectRequest sshConnectRequest) throws SQLException {
        Long rdsId = Long.valueOf(sshConnectRequest.getPk());
        HashMap<String, Object> map = new HashMap<>();
        boolean getStatus = slaveStatusService.slaveTimeStatus(rdsId);
        map.put("data", getStatus);
        return ResultTools.result_map(map, 0, "");
    }
}
