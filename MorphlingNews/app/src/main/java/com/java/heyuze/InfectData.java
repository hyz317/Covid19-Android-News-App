package com.java.heyuze;

import java.util.Vector;

public class InfectData
{
    public Vector<String> location = new Vector<>(); // 地点名称有很多级
    public String beginDate;
    public Vector<Integer> confirmed = new Vector<>();
    public Vector<Integer> suspected = new Vector<>();
    public Vector<Integer> cured = new Vector<>();
    public Vector<Integer> dead = new Vector<>();
}
