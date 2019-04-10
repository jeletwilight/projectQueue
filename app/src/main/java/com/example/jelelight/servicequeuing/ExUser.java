package com.example.jelelight.servicequeuing;

public class ExUser {
    private boolean inQueue;
    private String name;
    private int points;
    private int queueNo;

    public ExUser(){

    }

    public ExUser(boolean inQueue,String name,int points,int queueNo){
        this.inQueue = inQueue;
        this.name = name;
        this.points = points;
        this.queueNo = queueNo;
    }


    public boolean isInQueue() {
        return inQueue;
    }

    public void setInQueue(boolean inQueue) {
        this.inQueue = inQueue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getQueueNo() {
        return queueNo;
    }

    public void setQueueNo(int queueNo) {
        this.queueNo = queueNo;
    }
}
