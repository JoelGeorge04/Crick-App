package com.joelGeo.logg;

public class users {

    String name, age, dob,role;

    public users() {
    }

    public users(String name, String age, String dob, String role) {
        this.name = name;
        this.age = age;
        this.dob = dob;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
