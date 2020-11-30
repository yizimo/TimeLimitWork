package com.zimo.demo.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class WorkStart implements Delayed, Serializable {

    @Id
    @Column(name = "id")
    @KeySql(useGeneratedKeys = true)
    private Integer id;
    private Integer workId;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @Transient
    private Long startTimeLong;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWorkId() {
        return workId;
    }

    public void setWorkId(Integer workId) {
        this.workId = workId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Long getStartTimeLong() {
        return startTimeLong;
    }

    public void setStartTimeLong(Long startTimeLong) {
        this.startTimeLong = startTimeLong;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return startTimeLong - new Date().getTime();
    }

    @Override
    public int compareTo(Delayed o) {
        WorkStart workStart = (WorkStart) o;
        long diff = this.startTimeLong - workStart.getStartTimeLong();
        if(diff <= 0) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        return "WorkStart{" +
                "id=" + id +
                ", workId=" + workId +
                ", startTime=" + startTime +
                ", startTimeLong=" + startTimeLong +
                '}';
    }
}
