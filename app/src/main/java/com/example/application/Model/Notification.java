package com.example.application.Model;

import android.media.audiofx.NoiseSuppressor;

public class Notification {
    private String userid;
    private String text;
    private String postid;
    private boolean isPost;
    public Notification(){}

    public Notification(String userid,String text,String postid, boolean isPost)
    {
        this.isPost = isPost;
        this.text = text;
        this.postid = postid;
        this.userid = userid;
    }

    public String getUserid()
    {
       return userid;
    }
    public void setUserid(String userid)
    {
        this.userid = userid;
    }
    public String getText()
    {
        return text;
    }
    public void SetText()
    {
        this.text = text;
    }
    public String getPostid()
    {
        return postid;
    }
    public void setPostid()
    {
        this.postid = postid;
    }
    public boolean isIsPost()
    {
        return isPost;
    }
    public void setIsPost(boolean post)
    {
        isPost = post;
    }


}
