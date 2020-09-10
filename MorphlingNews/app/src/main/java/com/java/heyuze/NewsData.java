package com.java.heyuze;

import java.util.HashMap;
import java.util.SortedMap;

public class NewsData
{
    public enum NewsType {NEWS, PAPER, EVENT, POINTS}
    public String id, time, title, lang;
    public NewsType type;
    double value = 0;
}
