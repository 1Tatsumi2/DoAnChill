package com.example.doanchill.Class;

import java.io.Serializable;

public class Users implements Serializable {
    private String name;
    private String email;
    private String image;
    private String role;

    public Users(String name, String email, String image, String role) {
        this.name = name;
        this.email = email;
        this.image = image;
        this.role = role;
    }

    public Users() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
