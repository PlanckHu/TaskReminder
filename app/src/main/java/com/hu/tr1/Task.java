package com.hu.tr1;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Task {
    private boolean finished = false;
    private int number = 0;
    private List<Task> subTasks;//先不做子任务
    private String taskName = "";
    private String startTime = "";
    private String endTime = "";
    private String contents = "";

    public Task() {
    }

    public Task(Task task) {
        this.setNumber(task.getNumber());
        this.setFinished(task.Finished());
        this.setTaskName(task.getTaskName());
        this.setStartTime(task.getStartTime());
        this.setEndTime(task.getEndTime());
        this.setContents(task.getContents());
    }

    public boolean replace(Task task) {
        if (this.getNumber() != task.getNumber())
            return false;
        this.setFinished(task.Finished());
        this.setTaskName(task.getTaskName());
        this.setStartTime(task.getStartTime());
        this.setEndTime(task.getEndTime());
        this.setContents(task.getContents());
        return true;
    }

    public String[] getParaNames() {
        int paraNum;
        if (this.getNumber() != 0)
            paraNum = 6;
        else
            paraNum = 5;
        String[] str = new String[paraNum];
        str[0] = "finished";
        str[1] = "name";
        str[2] = "start_time";
        str[3] = "end_time";
        str[4] = "contents";
        if (paraNum == 6)
            str[5] = "id";
        return str;
    }

    public String[] getParaVals() {
        int paraNum;
        if (this.getNumber() != 0)
            paraNum = 6;
        else
            paraNum = 5;
        String[] str = new String[paraNum];
        str[0] = (this.finished) ? "1" : "0";
        str[1] = this.taskName;
        str[2] = this.startTime;
        str[3] = this.endTime;
        str[4] = this.contents;
        if (paraNum == 6)
            str[5] = String.valueOf(this.getNumber());
        return str;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int isFinished() {
        return ((finished) ? 1 : 0);
    }

    public boolean Finished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public List<Task> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<Task> subTasks) {
        this.subTasks = subTasks;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String content) {
        this.contents = content;
    }
}
