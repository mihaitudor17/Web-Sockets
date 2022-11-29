package org.example;

public class Message {
    private String content;
    private User user;

    public Message(String content, User user) {

        this.content = content;
        this.user = user;

    }

    public String getContent() {
        return content;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return content+"_"+user.toString();
    }
}
