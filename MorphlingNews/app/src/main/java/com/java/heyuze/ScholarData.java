package com.java.heyuze;

public class ScholarData
{
    public enum ScholarType {HIGHATTENTION, PASSAWAY}
    public ScholarType type;
    public String id, imgUrl, nameEn, nameCn;
    public String activity, sociability, newStar, risingStar, diversity; // Double
    public String pubs, citations, hIndex, gIndex; // Integer
    public String numFollowed, numViewed; // Integer
    public ScholarDataDetailed detail; // 共通的数据
    public PassawayDataDetailed pDetail; // 只有类型是PASSAWAY的学者才有信息

}
