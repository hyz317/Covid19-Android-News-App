package com.java.heyuze;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

public class knowledgeData
{
    public Double hot;
    public String url, imgUrl;
    public String label; // 标签
    public String description; // 介绍
    public LinkedHashMap<String, String> properties = new LinkedHashMap<>(); // 特性（可以遍历）
    public LinkedHashMap<String, String> forwardRelations = new LinkedHashMap<>(); // 前向关系（可以遍历）
    public LinkedHashMap<String, String> backwardRelations = new LinkedHashMap<>(); // 后向关系（可以遍历）
}
