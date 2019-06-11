package com.dtstack.dbhaswitch.service;

import com.dtstack.dbhaswitch.service.http.ResultModel;

import java.sql.SQLException;

public interface RemovalDataService {

    /**
     * 实例升级迁移前的检测
     */
//    ResultModel testDbSynStatus(String updateId)throws SQLException;

    ResultModel updateDataBase(String updateId) throws SQLException;
}
