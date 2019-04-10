package com.example.jelelight.servicequeuing;

public class Queue {
    private String patientID;
    private String patientCase;
    private String status;

    public Queue(){
    }

    public Queue(String patientID,String patientCase,String status){
        this.patientID = patientID;
        this.patientCase = patientCase;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
