package com.java.heyuze;

import java.util.Vector;

public class EventsVector
{
    public NewsData news;
    public Vector<Double> weight = new Vector<>();

    public double getLength()
    {
        double res = 0;
        for (int i = 0; i < weight.size(); ++i)
            res += weight.get(i) * weight.get(i);
        return Math.sqrt(res);
    }

    public double getDistance(EventsVector b)
    {
        double res = 0;
        for (int i = 0; i < weight.size(); ++i)
            res += (this.weight.get(i) - b.weight.get(i)) * (this.weight.get(i) - b.weight.get(i));
        return Math.sqrt(res);
    }

    public double getDistanceSquare(EventsVector b)
    {
        double res = 0;
        for (int i = 0; i < weight.size(); ++i)
            res += (this.weight.get(i) - b.weight.get(i)) * (this.weight.get(i) - b.weight.get(i));
        return res;
    }

    /*
    public double getDistance(EventsVector b)
    {
        double res = 0;
        for (int i = 0; i < weight.size(); ++i)
            res += this.weight.get(i) * b.weight.get(i);
        return res / (this.getLength() * b.getLength());
    }

    public double getDistanceSquare(EventsVector b)
    {
        double res = 0;
        for (int i = 0; i < weight.size(); ++i)
            res += this.weight.get(i) * b.weight.get(i);
        return (res / (this.getLength() * b.getLength())) * (res / (this.getLength() * b.getLength()));
    }
    */

}
