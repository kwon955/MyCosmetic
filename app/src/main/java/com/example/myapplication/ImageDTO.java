package com.example.myapplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 권대욱 on 2017-11-27.
 */


public class ImageDTO {


    public String imageUrl;
    public String title;
    public String name;
    public String uid;
    public String userId;
    public String memo;
    public String open;
    public String sellbydate;
    public String imageName;

    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

}