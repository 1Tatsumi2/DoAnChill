package com.example.doanchill.Class;

import java.io.Serializable;

public class Users implements Serializable {
    private String fName;
    private String email;
    private String image;
    private String role;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Users(String fName, String email, String image, String role) {
        this.fName = fName;
        this.email = email;
        this.image = image;
        this.role = role;
    }

    public Users() {
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
