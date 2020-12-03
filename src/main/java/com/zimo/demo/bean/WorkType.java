package com.zimo.demo.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Table(name = "work_stu_finish")
public class WorkType implements Serializable {

    @Id
    @Column(name = "id")
    @KeySql(useGeneratedKeys = true)
    private Integer id;
    private Integer workId;
    private Integer stuId;
    private Integer type; // 0 未开始   1 开始   2 结束  3 未完成
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    private Work work;
    private List<TaskResource> taskResourceList;

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    public List<TaskResource> getTaskResourceList() {
        return taskResourceList;
    }

    public void setTaskResourceList(List<TaskResource> taskResourceList) {
        this.taskResourceList = taskResourceList;
    }

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

    public Integer getStuId() {
        return stuId;
    }

    public void setStuId(Integer stuId) {
        this.stuId = stuId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    @Override
    public String toString() {
        return "WorkType{" +
                "id=" + id +
                ", workId=" + workId +
                ", stuId=" + stuId +
                ", type=" + type +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", work=" + work +
                ", taskResourceList=" + taskResourceList +
                '}';
    }
}
