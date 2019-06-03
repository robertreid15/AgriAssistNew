package com.robertreid.farm.system.postWork;


public class PostObject {
    private String uid;
    private String location;
    private String email;
    private String name;
    private String profile_url;
    private String description;

    public PostObject(String uid, String name, String email, String profile_url, String description) {
        this.uid = uid;
        //this.location = location;
        this.name = name;
        this.email = email;
        this.profile_url = profile_url;
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
