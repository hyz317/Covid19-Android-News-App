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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.Vector;
import java.util.Collections;

import com.alibaba.fastjson.*;

public class InfoManager
{
    public enum InfoType
    {INFECTDATA, NEWSDATA}

    private static InfoManager instance = null;
    private JSONObject infectData = null;
    private JSONObject newsData = null;

    private InfoManager()
    {
    }

    public static InfoManager getInstance()
    {
        if (instance == null) instance = new InfoManager();
        return instance;
    }

    public boolean hasInfectData() { return infectData != null; }

    public boolean hasNewsData() { return newsData != null; }

    public void loadJSON(InfoType type, String path) throws IOException
    {
        File file = new File(path);
        if (!file.exists()) return;
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
                infectData = JSON.parseObject(stringBuffer.toString());
                break;
            case NEWSDATA:
                newsData = JSON.parseObject(stringBuffer.toString());
                break;
            default:
        }
    }

    public void saveJSON(String path, String buf) throws IOException
    {
        File file = new File(path);
        if (!file.exists()) file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
        writer.write(buf);
        writer.flush();
        writer.close();
    }

    public String getJSON(String urlString) throws Exception
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

    public String updateCovidData(InfoType type) throws Exception
    {
        String jsonString;
        switch (type)
        {
            case INFECTDATA:
                jsonString = getJSON("https://covid-dashboard.aminer.cn/api/dist/epidemic.json");
                if (jsonString != null) infectData = JSONObject.parseObject(jsonString);
                return jsonString;
            case NEWSDATA:
                jsonString = getJSON("https://covid-dashboard.aminer.cn/api/dist/events.json");
                if (jsonString != null) newsData  = JSONObject.parseObject(jsonString);
                return jsonString;
            default:
        }
        return null;
    }

    public Vector<InfectData> getInfectData()
    {
        if (infectData == null) return null;
        Vector<InfectData> res = new Vector<>();
        for (String key : infectData.keySet())
        {
            InfectData data = new InfectData();
            String[] stringSet = key.split("\\|");
            for (String entry : stringSet)
                data.location.add(entry);
            JSONObject content = infectData.getJSONObject(key);
            JSONArray dayInfo = content.getJSONArray("data");
            data.beginDate = content.getString("begin");
            for (int i = 0; i < dayInfo.size(); ++i)
            {
                JSONArray detail = dayInfo.getJSONArray(i);
                data.confirmed.add(detail.getIntValue(0));
                data.suspected.add(detail.getIntValue(1));
                data.cured.add(detail.getIntValue(2));
                data.dead.add(detail.getIntValue(1));
            }
            res.add(data);
        }
        return res;
    }

    public Vector<NewsData> getNewsData()
    {
        if (newsData == null) return null;
        Vector<NewsData> res = new Vector<>();
        JSONArray contents = newsData.getJSONArray("datas");
        for (int i = 0; i < contents.size(); ++i)
        {
            JSONObject content = contents.getJSONObject(i);
            NewsData data = new NewsData();
            data.id = content.getString("_id");
            data.time = content.getString("time");
            data.title = content.getString("title");
            if (content.getString("type").equals("news")) data.type = NewsData.NewsType.NEWS;
            else data.type = NewsData.NewsType.PAPER;
            res.add(data);
        }
        Collections.sort(res, new Comparator<NewsData>()
        {
            @Override
            public int compare(NewsData a, NewsData b)
            {
                return b.time.compareTo(a.time);
            }
        });
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
        JSONArray labels = newsContent.getJSONArray("entities");
        for (int i = 0; i < labels.size(); ++i)
        {
            data.labels.add(labels.getJSONObject(i).getString("label"));
            String keyWord = newsContent.getString("seg_text");
            for (String token : keyWord.split(" "))
                data.words.add(token);
        }
        return data;
    }
}