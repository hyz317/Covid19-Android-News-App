package com.java.heyuze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class InfoManager
{
    public enum InfoType  {DATA, NEWSTITLE}

    private static InfoManager instance = null;
    private String[] covidData = null;

    private InfoManager()
    {
        covidData = new String[5];
    }

    public String getCovidData(InfoType type)
    {
        try
        {
            String urlString = null;
            switch (type)
            {
                case DATA:
                    urlString = "https://covid-dashboard.aminer.cn/api/dist/epidemic.json";
                    break;
                case NEWSTITLE:
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
                covidData[InfoType.DATA.ordinal()] = stringBuffer.toString();
            }
        } catch (MalformedURLException e)
        {
            System.out.println(e);
        } catch (IOException e)
        {
            System.out.println(e);
        }
        return covidData[InfoType.DATA.ordinal()];
    }

    public static InfoManager getInstance()
    {
        if (instance == null) instance = new InfoManager();
        return instance;
    }

}
