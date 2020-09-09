package com.java.heyuze;

import java.util.HashMap;
import java.util.SortedMap;

public class NewsData
{
    public enum NewsType {NEWS, PAPER, EVENT}
    public String id, time, title, lang;
    public NewsType type;
    public HashMap<String, Integer> wordCount = new HashMap<>();
    public Integer value = 0;
}
