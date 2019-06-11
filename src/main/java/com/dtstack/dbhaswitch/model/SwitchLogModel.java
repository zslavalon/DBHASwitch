package com.dtstack.dbhaswitch.model;

import java.util.Date;


public class SwitchLogModel {

    private Long rdsId;

    private Date startTime;

    private Date endTime;

    private int pageIndex = 1;

    private int pageSize = 20;

    public Long getRdsId() {
        return rdsId;
    }

    public void setRdsId(Long rdsId) {
        this.rdsId = rdsId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
