package com.dtstack.dbhaswitch.model;

import com.dtstack.dbhaswitch.utils.ModelBase;

import java.util.List;

import lombok.Data;

public class SwitchStatus extends ModelBase {

    private Long id;
    /**
     * 数据库类型
     */
    private String dbType;

    private Long rdsId;
    /**
     * 实例ID
     */
    private String rdsCode;
    /**
     * master的IP
     */
    private String masterIp;
    /**
     * slave的IP
     */
    private String slaveIp;
    /**
     * 端口
     */
    private String port;
    /**
     * 域名
     */
    private String hostName;
    /**
     * 倒换开始时间
     */
    private String switchTimeBegin;
    /**
     * 倒换结束时间
     */
    private String switchTimeEnd;
    /**
     * 倒换结果
     */
    private String switchResult;
    /**
     * 倒换类型
     */
    private String switchType;
    /**
     * 倒换原因
     */
    private String swtichInfo;
    /**
     * 倒换进度
     */
    private String schedule;

    private List log;

    private String logStr;

    public Long getRdsId() {
        return rdsId;
    }

    public void setRdsId(Long rdsId) {
        this.rdsId = rdsId;
    }

    public String getLogStr() {
        return logStr;
    }

    public void setLogStr(String logStr) {
        this.logStr = logStr;
    }

    public String getSwitchTimeBegin() {
        return switchTimeBegin;
    }

    public void setSwitchTimeBegin(String switchTimeBegin) {
        this.switchTimeBegin = switchTimeBegin;
    }

    public String getSwitchTimeEnd() {
        return switchTimeEnd;
    }

    public void setSwitchTimeEnd(String switchTimeEnd) {
        this.switchTimeEnd = switchTimeEnd;
    }

    public List getLog() {
        return log;
    }

    public void setLog(List log) {
        this.log = log;
    }

    public String getSwtichInfo() {
        return swtichInfo;
    }

    public void setSwtichInfo(String swtichInfo) {
        this.swtichInfo = swtichInfo;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getRdsCode() {
        return rdsCode;
    }

    public void setRdsCode(String rdsCode) {
        this.rdsCode = rdsCode;
    }

    public String getMasterIp() {
        return masterIp;
    }

    public void setMasterIp(String masterIp) {
        this.masterIp = masterIp;
    }

    public String getSlaveIp() {
        return slaveIp;
    }

    public void setSlaveIp(String slaveIp) {
        this.slaveIp = slaveIp;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getSwitchResult() {
        return switchResult;
    }

    public void setSwitchResult(String switchResult) {
        this.switchResult = switchResult;
    }

    public String getSwitchType() {
        return switchType;
    }

    public void setSwitchType(String switchType) {
        this.switchType = switchType;
    }
}
