package com.dtstack.dbhaswitch.service;

import com.dtstack.dbhaswitch.model.Instance;


public interface AddInstanceService {

    /**
     * 将数据入库，创建DNS,暂时弃用
     * *
     */
    void AddInstanceToDataBase(Instance instance);

    /**
     * 创建新的线程
     */
    void threadMain(Instance instance);

    void addInstanceFlow(Instance instance);

    void deleteInstanceFlow(Long rdsId);
}
