// UserProfile.java
package com.example.crimsonskillboostmobilev2;

public class UserProfile {
    public String fullName;
    public String birthday;
    public String gender;
    public String email;
    public String username;
    public String role;
    public String year;
    public String section;
    public String bio;

    // Empty constructor required for Firebase
    public UserProfile() {}

    public UserProfile(String fullName, String birthday, String gender, String email, String username, String role, String year, String section, String bio) {
        this.fullName = fullName;
        this.birthday = birthday;
        this.gender = gender;
        this.email = email;
        this.username = username;
        this.role = role;
        this.year = year;
        this.section = section;
        this.bio = bio;
    }
}