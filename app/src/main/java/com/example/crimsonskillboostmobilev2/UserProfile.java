package com.example.crimsonskillboostmobilev2;

public class UserProfile {
    public String fullName, birthday, gender, email, username, userType;

    public UserProfile() {
        // Default constructor required for calls to DataSnapshot.getValue(UserProfile.class)
    }

    public UserProfile(String fullName, String birthday, String gender, String email, String username, String userType) {
        this.fullName = fullName;
        this.birthday = birthday;
        this.gender = gender;
        this.email = email;
        this.username = username;
        this.userType = userType;
    }
}



