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
    public int starCount = 0;
    public Map<String, Boolean> likers = new HashMap<>();

    public Post() {
    }

    public Post(String userName, String userImg, String bookName, String bookInfo, Long postTime) {
        this.userImg = userImg;
        this.userName = userName;
        this.bookName = bookName;
        this.bookInfo = bookInfo;
        this.postTime = postTime;
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userName", userName);
        result.put("userImg", userImg);
        result.put("bookName", bookName);
        result.put("bookInfo", bookInfo);
        result.put("postTime", postTime);
        result.put("starCount", starCount);
        result.put("likers", likers);

        return result;
    }

}

