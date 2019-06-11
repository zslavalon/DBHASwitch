package com.dtstack.dbhaswitch.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dtstack.dbhaswitch.controller.utils.Controllers;
import com.dtstack.dbhaswitch.mapper.SwitchStatusDao;
import com.dtstack.dbhaswitch.model.SwitchStatus;
import com.dtstack.dbhaswitch.service.http.ResultModel;
import com.dtstack.dbhaswitch.service.http.ResultTools;
import com.dtstack.dbhaswitch.service.http.SSHConnectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping(value = Controllers.API_HA + "/highavailability")
public class DetailedInformationController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SwitchStatusDao switchStatusDao;

    @RequestMapping(value = {"/DetailedInformation"}, method = RequestMethod.POST)
    public ResultModel detailInfo(@RequestBody SSHConnectRequest sshConnectRequest) {

        try {
            HashMap<String, Object> map = new HashMap<>();
            SwitchStatus switchStatus = switchStatusDao.getDetailIns(Long.valueOf(sshConnectRequest.getPk()));
            List<SwitchStatus> switchStatusList = new ArrayList<>();
            switchStatusList.add(switchStatus);
            JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(switchStatusList));
            map.put("data", jsonArray);
            return ResultTools.result_map(map, 0, "");
        } catch (Exception e) {
            logger.error("DetailedInformation Query database exceptions:", e);
            return ResultTools.result_map(null, 1, "查询数据失败");
        }
    }
}
