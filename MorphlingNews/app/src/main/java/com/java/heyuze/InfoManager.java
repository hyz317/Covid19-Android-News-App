package com.java.heyuze;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.invoke.VolatileCallSite;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;
import java.util.Collections;

import com.alibaba.fastjson.*;
import com.huaban.analysis.jieba.*;

// InfoManager类public接口列表
///////////////////////////////////////
/*
        public static InfoManager getInstance(); // 获得实例

        public boolean hasInfectData(); // 判断疫情信息是否加载完毕
        public boolean hasNewsData(); // 判断新闻信息是否加载完毕
        public boolean isReadyForSearch(); // 判断搜索功能是否加载完毕

        public void loadJieba(); // 加载Jieba分词器
        public void loadJSON(InfoType type, String path); // 加载被本地JSON文件
        public void saveJSON(String path, String buf); // 保存本地JSON文件
        public String updateCovidData(InfoType type); // 通过HTTP更新疫情数据

        public Vector<InfectData> getInfectData(); // 获得加载好的疫情数据
        public Vector<NewsData> getNewsData(NewsData.NewsType type) // 获得加载好的新闻
        public Vector<knowledgeData> getKnowledge(String key); // 通过HTTP获得某关键词的知识图谱
        public NewsContent getNewsContent(String id) // 通过HTTP获得新闻正文

        public Vector<NewsData> searchNewsData(NewsData.NewsType type, String text) // 根据标题搜索新闻

*/
///////////////////////////////////////

public class InfoManager
{
    public enum InfoType
    {INFECTDATA, NEWSDATA}
    public boolean dictNeedUpdate = false;

    private static InfoManager instance = null;
    private JSONObject infectJSON = null;
    private JSONObject newsJSON = null;

    private Vector<InfectData> infectData = null;
    private Vector<NewsData> newsData = null;

    private NewsDictionary newsDict = null;
    JiebaSegmenter segmenter = null;

    private InfoManager() {}

    public static InfoManager getInstance()
    {
        if (instance == null) instance = new InfoManager();
        return instance;
    }

    public boolean hasInfectData() { return infectData != null; }

    public boolean hasNewsData() { return newsData != null; }

    public boolean isReadyForSearch() { return newsDict != null; }

    public void loadJieba() { segmenter = new JiebaSegmenter(); }

    public void loadJSON(InfoType type, String path) throws Exception
    {
        File file = new File(path);
        if (!file.exists())
        {
            System.out.println("Cannot load local JSON!");
            return;
        }
        FileInputStream fileInputStream = new FileInputStream(path);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuffer stringBuffer = new StringBuffer();
        String content;
        while ((content = bufferedReader.readLine()) != null)
        {
            stringBuffer.append(content);
            stringBuffer.append("\r\n");
        }
        bufferedReader.close();
        switch (type)
        {
            case INFECTDATA:
                infectJSON = JSON.parseObject(stringBuffer.toString());
                break;
            case NEWSDATA:
                newsJSON  = JSON.parseObject(stringBuffer.toString());
                break;
            default:
        }
        loadData(type);
        System.out.println("Load local JSON Success!");
        System.out.println("JSON Content: " + stringBuffer.toString());
    }

    public void saveJSON(String path, String buf) throws IOException
    {
        File file = new File(path);
        if (!file.exists()) file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
        writer.write(buf);
        writer.flush();
        writer.close();
        File newFile = new File(path);
    }

    private String getJSON(String urlString) throws Exception
    {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(60000);
        connection.connect();
        if (connection.getResponseCode() == 200)
        {
            InputStream stream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuffer stringBuffer = new StringBuffer();
            String content;
            while ((content = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(content);
                stringBuffer.append("\r\n");
            }
            bufferedReader.close();
            return stringBuffer.toString();
        }
        return null;
    }

    public void loadDictionary()
    {
        NewsDictionary resDict = new NewsDictionary();
        for (NewsData data: newsData)
            resDict.addTitle(data.title, data, segmenter);
        resDict.setNewsNum(newsData.size());
        synchronized (this)
        {
            newsDict = resDict;
        }
    }
  
    private void loadData(InfoType type) throws Exception
    {
        switch (type)
        {
            case INFECTDATA:
                Vector<InfectData> resInfect = new Vector<>();
                for (String key : infectJSON.keySet())
                {
                    InfectData data = new InfectData();
                    String[] stringSet = key.split("\\|");
                    for (String entry : stringSet)
                        data.location.add(entry);
                    JSONObject content = infectJSON.getJSONObject(key);
                    JSONArray dayInfo = content.getJSONArray("data");
                    data.beginDate = content.getString("begin");
                    for (int i = 0; i < dayInfo.size(); ++i)
                    {
                        JSONArray detail = dayInfo.getJSONArray(i);
                        data.confirmed.add(detail.getIntValue(0));
                        data.suspected.add(detail.getIntValue(1));
                        data.cured.add(detail.getIntValue(2));
                        data.dead.add(detail.getIntValue(3));
                    }
                    resInfect.add(data);
                }
                synchronized (this)
                {
                    infectData = resInfect;
                }
                break;
            case NEWSDATA:
                Vector<NewsData> resNews = new Vector<>();
                JSONArray contents = newsJSON.getJSONArray("datas");
                for (int i = 0; i < contents.size(); ++i)
                {
                    JSONObject content = contents.getJSONObject(i);
                    NewsData data = new NewsData();
                    if (content.getString("type").equals("paper"))
                        data.type = NewsData.NewsType.PAPER;
                    else if (content.getString("type").equals("news"))
                        data.type = NewsData.NewsType.NEWS;
                    else if (content.getString("type").equals("points"))
                        data.type = NewsData.NewsType.POINTS;
                    else data.type = NewsData.NewsType.EVENT;
                    data.id = content.getString("_id");
                    data.time = content.getString("time");
                    data.title = content.getString("title");
                    resNews.add(data);
                }
                Collections.sort(resNews, new Comparator<NewsData>()
                {
                    @Override
                    public int compare(NewsData a, NewsData b)
                    {
                        return b.time.compareTo(a.time);
                    }
                });
                synchronized (this)
                {
                    newsData = resNews;
                    dictNeedUpdate = true;
                }
                break;
        }
    }

    public String updateCovidData(InfoType type) throws Exception
    {
        String jsonString;
        switch (type)
        {
            case INFECTDATA:
                jsonString = getJSON("https://covid-dashboard.aminer.cn/api/dist/epidemic.json");
                if (jsonString != null)
                {
                    infectJSON = JSONObject.parseObject(jsonString);
                    loadData(type);
                }
                return jsonString;
            case NEWSDATA:
                jsonString = getJSON("https://covid-dashboard.aminer.cn/api/dist/events.json");
                if (jsonString != null)
                {
                    newsJSON = JSONObject.parseObject(jsonString);
                    loadData(type);
                }
                return jsonString;
            default:
        }
        return null;
    }

    public synchronized Vector<InfectData> getInfectData()
    {
        return infectData;
    }

    public synchronized Vector<NewsData> getNewsData(NewsData.NewsType type)
    {
        Vector<NewsData> res = new Vector<>();
        for (NewsData entry: newsData)
            if (entry.type == type) res.add(entry);
        return res;
    }

    public Vector<knowledgeData> getKnowledge(String key) throws Exception
    {
        String jsonString = getJSON("https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery?entity=" + key);
        if (jsonString == null) return null;
        JSONArray knowledgeContents= JSONObject.parseObject(jsonString).getJSONArray("data");
        Vector<knowledgeData> res = new Vector<>();
        for (int i = 0; i < knowledgeContents.size(); ++i)
        {
            JSONObject knowledgeContent = knowledgeContents.getJSONObject(i);
            knowledgeData data = new knowledgeData();
            data.hot = knowledgeContent.getDouble("hot");
            data.label = knowledgeContent.getString("label");
            data.url = knowledgeContent.getString("url");
            data.imgUrl = knowledgeContent.getString("img");
            JSONObject abstractInfo = knowledgeContent.getJSONObject("abstractInfo");
            if (!abstractInfo.getString("enwiki").equals("")) data.description = abstractInfo.getString("enwiki");
            else if (!abstractInfo.getString("baidu").equals("")) data.description = abstractInfo.getString("baidu");
            else data.description = abstractInfo.getString("zhwiki");
            JSONObject properties = abstractInfo.getJSONObject("COVID").getJSONObject("properties");
            for (String propertyName : properties.keySet())
                data.properties.put(propertyName, properties.getString(propertyName));
            JSONArray relations = abstractInfo.getJSONObject("COVID").getJSONArray("relations");
            for (int j = 0; j < relations.size(); ++j)
            {
                JSONObject relation = relations.getJSONObject(j);
                if (relation.getBooleanValue("forward"))
                    data.forwardRelations.put(relation.getString("label"), relation.getString("relation"));
                else
                    data.backwardRelations.put(relation.getString("label"), relation.getString("relation"));
            }
            res.add(data);
        }
        Collections.sort(res, new Comparator<knowledgeData>()
        {
            @Override
            public int compare(knowledgeData a, knowledgeData b)
            {
                return b.hot.compareTo(a.hot);
            }
        });
        return res;
    }

    public NewsContent getNewsContent(String id) throws Exception
    {
        String jsonString = getJSON("https://covid-dashboard.aminer.cn/api/event/" + id);
        if (jsonString == null) return null;
        JSONObject newsContent = JSONObject.parseObject(jsonString).getJSONObject("data");
        NewsContent data = new NewsContent();
        data.content = newsContent.getString("content");
        data.date = newsContent.getString("date");
        data.type = newsContent.getString("type");
        if (data.type == "news" || data.type == "paper")
            data.source = newsContent.getString("source");
        JSONArray labels = newsContent.getJSONArray("entities");
        for (int i = 0; i < labels.size(); ++i)
            data.labels.add(labels.getJSONObject(i).getString("label"));
        return data;
    }

    public synchronized Vector<NewsData> searchNewsData(NewsData.NewsType type, String text)
    {
        for (NewsData news: newsData)
            news.value = 0;
        Vector<NewsData> res = new Vector<>();
        List<String> words = segmenter.sentenceProcess(text);
        for (String word: words)
        {
            if (newsDict.getUselessText().contains(word)) continue;
            HashSet<NewsData> newsList = newsDict.getKeywordDictionary().get(word);
            if (newsList == null) continue;
            for (NewsData news: newsList)
            {
                int tf = news.wordCount.get(word);
                double idf = Math.log(newsDict.getNewsNum() / (double)(newsList.size()));
                news.value += tf * idf;
            }
        }
        for (NewsData news: newsData)
            if (news.value != 0) res.add(news);

        Collections.sort(res, new Comparator<NewsData>()
        {
            @Override
            public int compare(NewsData a, NewsData b)
            {
                if (b.value > a.value + 0.0005) return 1;
                else if (a.value > b.value + 0.0005) return -1;
                else return 0;
            }
        });
        return res;
    }
}

