package com.dtstack.dbhaswitch.service;

import java.sql.Connection;

public interface InfoService {

    /**
     * 连接数据库的工具
     */
    Connection setConnectionMaster(Long rdsId);

    Connection setConnectionSlave(Long rdsId);
}
