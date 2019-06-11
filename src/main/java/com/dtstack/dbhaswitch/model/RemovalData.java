package com.dtstack.dbhaswitch.model;

import com.dtstack.dbhaswitch.utils.ModelBase;
import lombok.Data;

public class RemovalData extends ModelBase {

    /**
     * 自增Id
     */
    private Long id;
    /**
     * 实例名
     */
    private String rdsCode;
    /**
     * 实例IP地址
     */
    private String ip;
    /**
     * 实例端口
     */
    private String port;
    /**
     * 用户名
     */
    private String userName;

    /**
     * 一套升级实例使用一个updateId
     */
    private String updateId;

    /**
     * 1.旧主，2.旧备，3.新主，4.新备
     */
    private Integer dbStatus;
    /**
     * 密码
     */
    private String passWord;
    /**
     * 实例名
     */
    private String hostName;

    private Long instanceId;

    private Long rdsId;

    private Integer judgeMaster;

    private String rdsUrl;

    private String isDelete;

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getJudgeMaster() {
        return judgeMaster;
    }

    public void setJudgeMaster(Integer judgeMaster) {
        this.judgeMaster = judgeMaster;
    }

    public Long getId() {
        return id;
    }

    public Long getRdsId() {
        return rdsId;
    }

    public void setRdsId(Long rdsId) {
        this.rdsId = rdsId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRdsCode() {
        return rdsCode;
    }

    public void setRdsCode(String rdsCode) {
        this.rdsCode = rdsCode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUpdateId() {
        return updateId;
    }

    public void setUpdateId(String updateId) {
        this.updateId = updateId;
    }

    public Integer getDbStatus() {
        return dbStatus;
    }

    public void setDbStatus(Integer dbStatus) {
        this.dbStatus = dbStatus;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public String getRdsUrl() {
        return rdsUrl;
    }

    public void setRdsUrl(String rdsUrl) {
        this.rdsUrl = rdsUrl;
    }
}
