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

    public String toString() {
        String str = "";
        for (String l : location) {
            str += l;
            str += ",";
        }
        str += "BeginDate";
        str += " ";
        str += beginDate;
        str += " confirmed ";
        for (Integer l : confirmed) {
            str += l;
            str += " ";
        }
        return str;
    }
}
