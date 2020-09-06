package com.java.heyuze;

import java.util.Vector;

public class InfectData
{
    Vector<String> location = new Vector<>(); // 地点名称有很多级
    String beginDate;
    Vector<Integer> confirmed = new Vector<>();
    Vector<Integer> suspected = new Vector<>();
    Vector<Integer> cured = new Vector<>();
    Vector<Integer> dead = new Vector<>();
}
