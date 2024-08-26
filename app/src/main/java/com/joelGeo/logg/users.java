package com.joelGeo.logg;

public class users {

    String name, sixs, fours,role,wickets;

    public users() {
    }

    public users(String name, String sixs, String fours, String role , String wickets) {
        this.name = name;
        this.sixs = sixs;
        this.fours = fours;
        this.role = role;
        this.wickets = wickets;

    }

    public String getWickets() {
        return wickets;
    }

    public void setWickets(String wickets) {
        this.wickets = wickets;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSixs() {
        return sixs;
    }

    public void setSixs(String sixs) {
        this.sixs = sixs;
    }

    public String getFours() {
        return fours;
    }

    public void setFours(String fours) {
        this.fours = fours;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}