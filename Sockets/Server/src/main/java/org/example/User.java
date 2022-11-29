package org.example;

public class User {

    private  String username;

    private String password;

    public User(String username, String password) {

        this.username = username;
        this.password = password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return username+"_"+password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
