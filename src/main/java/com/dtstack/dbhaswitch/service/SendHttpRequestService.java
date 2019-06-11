package com.dtstack.dbhaswitch.service;

import java.io.IOException;

public interface SendHttpRequestService {

    /**
     * 向rds发送倒换后新的主实例
     */
    Integer sendNewMaster(Long rdsId);

    /**
     * 更新DNS
     */
    Integer sendUpdateDns(Long rdsId);

    /**
     * 倒换失败，触发审计日志
     */
    void sendMonitorEvent(Long rdsId);

    /**
     * 创建新的实例，添加DNS
     **/
    boolean sendAddDns(Long rdsId);

    /**
     * 删除实例，清除DNS
     */
    void sendDeleteDns(Long rdsId);

    boolean sendAddRemovalDns(String updateId);

    /**
     * 备实例IO异常
     */
    void sendSlaveIoError(Long rdsId, Integer IoStatus, Integer runningStatus);
}
