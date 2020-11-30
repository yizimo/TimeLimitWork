package com.zimo.demo.bean;

import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

public class Receive implements Serializable {

    private List<Integer> ids;
    private Work work;
    private String taskResourceInfo;

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    public String getTaskResourceInfo() {
        return taskResourceInfo;
    }

    public void setTaskResourceInfo(String taskResourceInfo) {
        this.taskResourceInfo = taskResourceInfo;
    }

    @Override
    public String toString() {
        return "Receive{" +
                "ids=" + ids +
                ", work=" + work +
                ", taskResourceInfo='" + taskResourceInfo + '\'' +
                '}';
    }
}
