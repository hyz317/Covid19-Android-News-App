package com.java.heyuze;

import java.util.HashSet;
import java.util.HashMap;
import java.util.List;

import com.huaban.analysis.jieba.*;

public class NewsDictionary
{
    private int newsNum = 0;
    private HashMap<String, HashSet<NewsData>> keywordDictionary = new HashMap<>();
    private HashMap<String, Integer> keywordCount = new HashMap<>();

    public void setNewsNum(int num)
    {
        newsNum = num;
    }

    public int getNewsNum()
    {
        return newsNum;
    }

    public String getUselessText()
    {
        return "( ) , [ ] . - / “ ” : ' ~ = + @ # $ % ^ & * !  \\ | \" { } ， 。 : +  _ ; ； ";
    }

    public HashMap<String, HashSet<NewsData>> getKeywordDictionary()
    {
        return keywordDictionary;
    }

    public HashMap<String, Integer> getKeywordCount()
    {
        return keywordCount;
    }

    private synchronized void addKeyWord(String keyword, NewsData news)
    {
        if (keywordDictionary.get(keyword) == null) keywordDictionary.put(keyword, new HashSet<NewsData>());
        keywordDictionary.get(keyword).add(news);
    }

    public synchronized void addTitle(String title, NewsData news, JiebaSegmenter segmenter)
    {
        List<String> words = segmenter.sentenceProcess(title);
        for (String word : words)
        {
            if (getUselessText().contains(word)) continue;
            addKeyWord(word, news);
            if (news.wordCount.get(word) == null) news.wordCount.put(word, 1);
            else news.wordCount.put(word, news.wordCount.get(word) + 1);
        }
    }
}
