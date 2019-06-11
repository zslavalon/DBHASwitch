package com.dtstack.dbhaswitch.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface SlaveStatusService {


    /**
     * 连接备实例
     */
    void SlaveStatus(Connection connection, Long rdsId) throws SQLException;

    /**
     * 触发倒换,返回是否成功
     */
    boolean isGetStatus(Long rdsId) throws SQLException;

    /**
     * 实例升级时检测备实例状态
     */
    boolean switchForUpdate(Connection connection) throws SQLException;

    /**
     * 触发倒换,不返回是否成功
     */
    boolean slaveTimeStatus(Long rdsId) throws SQLException;

    List getList();

    void SlaveReadOnly(Long rdsId) throws SQLException;
}
