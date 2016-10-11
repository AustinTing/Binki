package com.expixel.binki;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cellbody on 2016/10/7.
 */

public class Post {
    public String userName;
    public String userImg;
    public String bookName;
    public String bookInfo;
    public Long postTime;
    public Map<String, Boolean> liker = new HashMap<>();

    public Post() {
    }

    public Post(String userName, String userImg, String bookName, String bookInfo, Long postTime) {
        this.userImg = userImg;
        this.userName = userName;
        this.bookName = bookName;
        this.bookInfo = bookInfo;
        this.postTime = postTime;
    }

}
