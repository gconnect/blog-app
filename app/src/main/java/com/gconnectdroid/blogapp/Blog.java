package com.gconnectdroid.blogapp;

/**
 * Created by gconnect on 17/10/2017.
 */

public class Blog {
    private String title, desc,image,username;
public Blog(){

}
    public Blog(String title, String desc, String image, String username) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username =username;
    }
}
