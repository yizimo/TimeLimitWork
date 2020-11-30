package com.zimo.demo.bean;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class TaskTypeInfo {

    private Integer taskResourceTypeId;
    private Integer workId;
    private String info;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    private Integer stuId;

    public Integer getTaskResourceTypeId() {
        return taskResourceTypeId;
    }

    public void setTaskResourceTypeId(Integer taskResourceTypeId) {
        this.taskResourceTypeId = taskResourceTypeId;
    }

    public Integer getWorkId() {
        return workId;
    }

    public void setWorkId(Integer workId) {
        this.workId = workId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getStuId() {
        return stuId;
    }

    public void setStuId(Integer stuId) {
        this.stuId = stuId;
    }

    @Override
    public String toString() {
        return "TaskTypeInfo{" +
                "taskResourceTypeId=" + taskResourceTypeId +
                ", workId=" + workId +
                ", info='" + info + '\'' +
                ", endTime=" + endTime +
                ", stuId=" + stuId +
                '}';
    }
}
