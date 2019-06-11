package com.dtstack.dbhaswitch.service;

import com.dtstack.dbhaswitch.model.Instance;
import com.dtstack.dbhaswitch.model.SwitchStatus;

import java.sql.SQLException;
import java.util.List;

public interface DbhaService {

    /**
     * 一般倒换逻辑
     */
    List switchLogic(Instance instance) throws SQLException;

    /**
     * 添加倒换状态的对象信息
     */
    SwitchStatus instanceValuation(Instance instance);

    /**
     * 判断是否倒换成功
     */
    boolean isGetbool();

    /**
     * 倒换失败
     */
    SwitchStatus autoSwitchFailed(SwitchStatus switchStatus);

    /**
     * 倒换成功
     */
    SwitchStatus switchSucced(SwitchStatus switchStatus);

    /**
     * 倒换成功后更新数据库状态信息
     */
    void updateMasterAndSlave(Long rdsId);

}
