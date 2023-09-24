package com.example.closersharelocation.Model;

import java.text.SimpleDateFormat;
import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String password;
    private String email;
    private String avatarUrl;
    private Long createdTime;
    private Long lastModified;

    public Account() {
    }

    public Account(String name, String email, String avatarUrl) {
        this.name = name;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    public Account(Long id, String name, String password, String email, String avatarUrl, Long createdTime,
            Long lastModified) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.createdTime = createdTime;
        this.lastModified = lastModified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedTime() {
        return new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date(createdTime));
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public String getLastModified() {
        return new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date(lastModified));
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
