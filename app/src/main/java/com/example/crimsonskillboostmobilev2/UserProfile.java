package com.example.crimsonskillboostmobilev2;

public class UserProfile {
    public String fullName;
    public String birthday;
    public String gender;
    public String email;
    public String username;

    public UserProfile() {} // Required by Firebase

    public UserProfile(String fullName, String birthday, String gender, String email, String username) {
        this.fullName = fullName;
        this.birthday = birthday;
        this.gender = gender;
        this.email = email;
        this.username = username;
    }
}


