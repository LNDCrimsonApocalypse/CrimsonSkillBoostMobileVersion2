package com.example.crimsonskillboostmobilev2;

public class UserProfile {
    public String fullName;
    public String birthday;
    public String gender;

    public UserProfile() {} // Required by Firebase

    public UserProfile(String fullName, String birthday, String gender) {
        this.fullName = fullName;
        this.birthday = birthday;
        this.gender = gender;
    }
}

