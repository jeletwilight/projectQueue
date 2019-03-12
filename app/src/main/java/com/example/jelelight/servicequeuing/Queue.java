package com.example.jelelight.servicequeuing;

public class Queue {
    private Integer id;
    private Integer currentQueue;
    private String patientID;
    private String patientCase;

    public Queue(){
    }

    public Queue(String patientID,String patientCase){
        this.patientID = patientID;
        this.patientCase = patientCase;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCurrentQueue() {
        return currentQueue;
    }

    public void setCurrentQueue(Integer currentQueue) {
        this.currentQueue = currentQueue;
    }

    public String getPatientCase() {
        return patientCase;
    }

    public void setPatientCase(String patientCase) {
        this.patientCase = patientCase;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }




}
