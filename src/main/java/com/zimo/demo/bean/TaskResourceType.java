package com.zimo.demo.bean;

import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class TaskResourceType implements Serializable {

    @Id
    @Column(name = "id")
    @KeySql(useGeneratedKeys = true)
    private Integer id;
    private Integer taskResourceId;
    private String info;
    private Integer stuId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskResourceId() {
        return taskResourceId;
    }

    public void setTaskResourceId(Integer taskResourceId) {
        this.taskResourceId = taskResourceId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getStuId() {
        return stuId;
    }

    public void setStuId(Integer stuId) {
        this.stuId = stuId;
    }

    @Override
    public String toString() {
        return "TaskResourceType{" +
                "id=" + id +
                ", taskResourceId=" + taskResourceId +
                ", info='" + info + '\'' +
                ", stuId=" + stuId +
                '}';
    }
}
