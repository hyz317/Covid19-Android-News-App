package com.java.heyuze;

public class SimpleInfectData {
    public String name;
    public int increase;
    public int accumulation;
    public int cure;
    public int dead;

    public SimpleInfectData(InfectData d, String type) {
        if (type.equals("China")) {
            this.name = d.location.get(1);
        } else {
            this.name = d.location.get(0);
        }
        this.accumulation = d.confirmed.get(d.confirmed.size() - 1);
        this.increase = this.accumulation - d.confirmed.get(d.confirmed.size() - 2);
        this.cure = d.cured.get(d.cured.size() - 1);
        this.dead = d.dead.get(d.dead.size() - 1);
        // System.out.println("type " + type + " name " + name + " confirmed " + accumulation);
    }
}