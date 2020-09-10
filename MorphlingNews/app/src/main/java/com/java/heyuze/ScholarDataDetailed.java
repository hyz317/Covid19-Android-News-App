package com.java.heyuze;

import java.util.LinkedHashMap;

public class ScholarDataDetailed
{
    public String address;
    public String affiliation;
    public String biography;
    public String education;
    public String email;
    public String homepage;
    public String phone;
    public String position;
    public String work;
    public LinkedHashMap<String, Double> tag = new LinkedHashMap<>(); // tag名字到占比的映射，不是每个学者都有！
}
