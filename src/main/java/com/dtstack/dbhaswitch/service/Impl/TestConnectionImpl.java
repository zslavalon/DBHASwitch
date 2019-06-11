package com.dtstack.dbhaswitch.service.Impl;

import com.dtstack.dbhaswitch.service.TestConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Service
public class TestConnectionImpl implements TestConnectionService {

    boolean test = false;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean testConnection(Connection connection) throws SQLException {
        if (!connection.isClosed()) {
            return true;
        }
        return false;
    }

    public boolean testSlaveStatus(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet_io = statement.executeQuery("show slave status;");
        while (resultSet_io.next()) {
            String getIoValue = resultSet_io.getString("Slave_IO_Running");
            String getSqlValue = resultSet_io.getString("Slave_SQL_Running");

            if (!getIoValue.equals("Yes") || !getSqlValue.equals("Yes")) {
                return false;
            }
        }
        return true;
    }
}
