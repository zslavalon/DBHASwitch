package com.dtstack.dbhaswitch.service;

import com.dtstack.dbhaswitch.model.Instance;

public interface ThreadMainService {

    /**
     * 线程池接入
     */
    void getConnection(Instance instance);


}
