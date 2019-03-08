package com.hu.tr_v1;

public class SingleTask {

    int id;
    String taskName;
    String taskTime;
    String taskContent;
    boolean finished;
    String deadline;

    public SingleTask(int id){
        this.id = id;
        finished = false;
    }

    public SingleTask(){
        finished = false;
    }


    public int getId() {
        return id;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

}
