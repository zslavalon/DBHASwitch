package com.dtstack.dbhaswitch.mapper;

import com.dtstack.dbhaswitch.model.RemovalData;
import com.dtstack.dbhaswitch.utils.BaseDAO;
import org.springframework.stereotype.Repository;

@Repository
public class RemovalDataDao extends BaseDAO<RemovalData> {

    public RemovalDataDao() {
        super("Removal");
    }

}
