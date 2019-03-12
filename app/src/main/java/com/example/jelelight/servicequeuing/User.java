package com.example.jelelight.servicequeuing;

public class User {
    private String id;
    private String name;
    private String gender;
    private String blood;
    private String email;
    private String birthdate;
    private String phone;
    private String weight;
    private String height;
    private String[] caution;

    public User(){

    }

    public User(String phone){
        id = phone;
        this.id = id;
        this.name = null;
        this.birthdate = null;
        this.phone = phone;
        this.weight = null;
        this.height = null;
        this.caution = null;
    }

    public User(String name,String email){
        //id = email;
        this.name = name;
        this.email = email;
        this.birthdate = null;
        this.phone = null;
        this.weight = null;
        this.height = null;
        this.caution = null;
        this.gender = null;
        this.blood = null;
    }

    public User(String name,String gender,String blood,String birthdate,String phone,String weight,String height,String[] caution){
        //id = email;
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.blood = blood;
        this.birthdate = birthdate;
        this.phone = phone;
        this.weight = weight;
        this.height = height;
        this.caution = caution;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String[] getCaution() {
        return caution;
    }

    public void setCaution(String[] caution) {
        this.caution = caution;
    }
}
