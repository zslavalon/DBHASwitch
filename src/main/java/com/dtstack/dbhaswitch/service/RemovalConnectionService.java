package com.dtstack.dbhaswitch.service;

import java.sql.Connection;

public interface RemovalConnectionService {

    /**
     * 实例升级迁移的连接数据库的接口，主备分开
     */
    Connection getMasterConnection(Long rdsId);

    Connection getSlaveConnection(Long rdsId);
}
