package com.dtstack.dbhaswitch.service;

import java.sql.Connection;
import java.sql.SQLException;

public interface TestConnectionService {


    //避免循环倒换，反复(目前未使用)
    boolean testConnection(Connection connection) throws SQLException;
}
