package com.dtstack.dbhaswitch.service;

import com.dtstack.dbhaswitch.model.Instance;
import com.dtstack.dbhaswitch.service.http.ResultModel;

import java.sql.Connection;
import java.sql.SQLException;

public interface MasterStatusService {


    /**
     * 检测正常运行的主实例状态，写表
     */
    void MasterStatus(Connection connection) throws SQLException;

    /**
     * 手动切换
     */
    ResultModel manualSwtich(Long rdsId);

    ResultModel forceSwitch(Long rdsId);

}
