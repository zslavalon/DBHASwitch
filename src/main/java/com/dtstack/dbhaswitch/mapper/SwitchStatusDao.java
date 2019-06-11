package com.dtstack.dbhaswitch.mapper;

import com.dtstack.dbhaswitch.model.SwitchLogModel;
import com.dtstack.dbhaswitch.model.SwitchStatus;
import com.dtstack.dbhaswitch.utils.BaseDAO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SwitchStatusDao extends BaseDAO<SwitchStatus> {
    public SwitchStatusDao() {
        super("SwitchStatus");
    }

    public SwitchStatus getDetailIns(Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return this.sqlSessionTemplate.selectOne("SwitchStatus-getDetailIns", params);
    }

    public List<SwitchStatus> sendLog(SwitchLogModel switchLogModel) {
        Map<String, Object> params = new HashMap<>();
        params.put("startTime", switchLogModel.getStartTime());
        params.put("endTime", switchLogModel.getEndTime());
        params.put("rdsId", switchLogModel.getRdsId());
        return this.sqlSessionTemplate.selectList("SwitchStatus-log", params);
    }
}
