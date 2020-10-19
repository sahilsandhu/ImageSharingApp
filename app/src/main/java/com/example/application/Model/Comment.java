package com.example.application.Model;

public class Comment {
    private String comment;
    private String publisher;
    private String id;

    public Comment() {
    }

    public Comment(String comment, String publisher,String id) {
        this.comment = comment;
        this.publisher = publisher;
        this.id = id;
    }

    public String getId(){ return id;}
    public void setId(){
        this.id = id;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher()
    {
        this.publisher = publisher;
    }
}
