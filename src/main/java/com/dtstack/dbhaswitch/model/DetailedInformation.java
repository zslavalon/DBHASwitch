package com.dtstack.dbhaswitch.model;

import com.dtstack.dbhaswitch.utils.ModelBase;
import lombok.Data;

import java.util.List;

@Data
public class DetailedInformation extends ModelBase {

    /**
     * 老的master的IP
     */
    private String masterIp;
    /**
     * 实例ID
     */
    private String instanceId;
    /**
     * 日志信息
     */
    private List log;
    /**
     * 切换描述
     */
    private String swtichInfo;

    private String schedule;

    public String getSwtichInfo() {
        return swtichInfo;
    }

    public void setSwtichInfo(String swtichInfo) {
        this.swtichInfo = swtichInfo;
    }

    public String getMasterIp() {
        return masterIp;
    }

    public void setMasterIp(String masterIp) {
        this.masterIp = masterIp;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public List getLog() {
        return log;
    }

    public void setLog(List log) {
        this.log = log;
    }
}
