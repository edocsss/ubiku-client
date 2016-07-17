package com.prys.model;

public class User {
    private String fullName;
    private String email;

    public User (String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public String getFullName () { return this.fullName; }
    public String getEmail () { return this.email; }
}