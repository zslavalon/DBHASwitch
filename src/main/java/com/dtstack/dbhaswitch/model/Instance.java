package com.dtstack.dbhaswitch.model;


import com.dtstack.dbhaswitch.utils.ModelBase;


public class Instance extends ModelBase {


    private Long id;
    /**
     * 实例ID
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
     * 密码
     */
    private String passWord;
    /**
     * 实例名
     */
    private String hostName;

    /**
     * 对端实例的ip地址
     */
    private String brotherIp;

    /**
     * 对应rds的Rds_Base的主键ID
     */
    private Long rdsId;

    /**
     * 判断主备，1是主，2是备
     */
    private Integer judgeMaster;

    /**
     * 判断是否删除
     */
    private String isDelete;

    /**
     * 域名
     */
    private String rdsUrl;

    /**
     * 对应是Rds_Instance表的主键Id
     */
    private Long instanceId;

    /**
     * 实例的运行状态，0是正常运行，1是倒换失败，取消继续倒换，2是升级的实例
     */
    private Integer rdsStatus;

    public Integer getRdsStatus() {
        return rdsStatus;
    }

    public void setRdsStatus(Integer rdsStatus) {
        this.rdsStatus = rdsStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getRdsId() {
        return rdsId;
    }

    public void setRdsId(Long rdsId) {
        this.rdsId = rdsId;
    }

    public Integer getJudgeMaster() {
        return judgeMaster;
    }

    public void setJudgeMaster(Integer judgeMaster) {
        this.judgeMaster = judgeMaster;
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


    public String getBrotherIp() {
        return brotherIp;
    }

    public void setBrotherIp(String brotherIp) {
        this.brotherIp = brotherIp;
    }


//    public long getMasterId() {
//        return masterId;
//    }
//
//    public void setMasterId(long masterId) {
//        this.masterId = masterId;
//    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

}