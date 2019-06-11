package com.dtstack.dbhaswitch.mapper;

import com.dtstack.dbhaswitch.model.Instance;
import com.dtstack.dbhaswitch.utils.BaseDAO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InstanceDao extends BaseDAO<Instance> {

    public InstanceDao() {
        super("Instance");
    }

    public Instance sendDns(Long rdsId) {
        Map<String, Object> params = new HashMap<>();
        params.put("rdsId", rdsId);
        params.put("judgeMaster", 1);
        return this.sqlSessionTemplate.selectOne("Instance-sendDns", params);
    }


}
