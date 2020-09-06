package com.java.heyuze;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.Collections;

import com.alibaba.fastjson.*;

public class InfoManager
{
    public enum InfoType  {INFECTDATA, NEWSDATA}

    private static InfoManager instance = null;
    public JSONObject infectData = null;
    public JSONObject newsData = null;

    private InfoManager() {}

    public void LoadCovidData(InfoType type)
    {
        try
        {
            String urlString = null;
            switch (type)
            {
                case INFECTDATA:
                    urlString = "https://covid-dashboard.aminer.cn/api/dist/epidemic.json";
                    break;
                case NEWSDATA:
                    urlString = "https://covid-dashboard.aminer.cn/api/dist/events.json";
                    break;
                default:
            }
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
                String content = null;
                while ((content = bufferedReader.readLine()) != null)
                {
                    stringBuffer.append(content);
                    stringBuffer.append("\r\n");
                }
                switch (type)
                {
                    case INFECTDATA:
                        infectData = JSONObject.parseObject(stringBuffer.toString());
                        break;
                    case NEWSDATA:
                        newsData = JSONObject.parseObject(stringBuffer.toString());
                        break;
                    default:
                }
            }
        } catch (MalformedURLException e)
        {
            System.out.println(e);
        } catch (IOException e)
        {
            System.out.println(e);
        }
    }

    public static InfoManager getInstance()
    {
        if (instance == null) instance = new InfoManager();
        return instance;
    }

    public Vector<InfectData> getInfectData()
    {
        Vector<InfectData> res = new Vector<InfectData>();
        for (String key: infectData.keySet())
        {
            InfectData data = new InfectData();
            String[] stringSet = key.split("\\|");
            for (String entry: stringSet)
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
        Vector<NewsData> res = new Vector<NewsData>();
        JSONArray contents = newsData.getJSONArray("datas");
        for(int i = 0; i < contents.size(); ++i)
        {
            JSONObject content = contents.getJSONObject(i);
            NewsData data = new NewsData();
            data.id = content.getString("_id");
            data.time = content.getString("time");
            data.title = content.getString("title");
            if (content.getString("type") == "news") data.type = NewsData.NewsType.NEWS;
            else data.type = NewsData.NewsType.PAPER;
            res.add(data);
        }
        Collections.sort(res, new Comparator<NewsData>()
        {
            @Override
            public int compare(NewsData a, NewsData b)
            {
                return a.time.compareTo(b.time);
            }
        });
        return res;
    }

}
