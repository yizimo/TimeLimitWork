package com.zimo.demo.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class WorkEnd implements Serializable, Delayed {

    @Id
    @Column(name = "id")
    @KeySql(useGeneratedKeys = true)
    private Integer id;
    private Integer workId;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    @Transient
    private Long endTimeLong;

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

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getEndTimeLong() {
        return endTimeLong;
    }

    public void setEndTimeLong(Long endTimeLong) {
        this.endTimeLong = endTimeLong;
    }



    @Override
    public long getDelay(TimeUnit unit) {
        return endTimeLong - new Date().getTime();
    }

    @Override
    public int compareTo(Delayed o) {
        WorkEnd workEnd = (WorkEnd) o;
        long diff = this.endTimeLong - workEnd.getEndTimeLong();
        if(diff <= 0) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        return "WorkEnd{" +
                "id=" + id +
                ", workId=" + workId +
                ", endTime=" + endTime +
                ", endTimeLong=" + endTimeLong +
                '}';
    }
}
