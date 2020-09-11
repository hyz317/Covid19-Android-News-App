package com.java.heyuze;

import android.content.Context;
import android.icu.text.IDNA;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

public class InitThreadHandler implements Runnable
{
    private String fileDir;
    public InitThreadHandler(String fileDir)
    {
        this.fileDir = fileDir;
    }

    @Override
    public void run()
    {
        try
        {
            synchronized (this)
            {
                InfoManager instance = InfoManager.getInstance();
                instance.loadJSON(InfoManager.InfoType.NEWSDATA, fileDir + "news_data.json");
                instance.loadJSON(InfoManager.InfoType.INFECTDATA, fileDir + "infect_data.json");
            }

        } catch (Exception e)
        {
        	e.printStackTrace();
        }

        try
        {
            synchronized (this)
            {
                InfoManager instance = InfoManager.getInstance();
                instance.loadScholarData();
                String infectString = instance.updateCovidData(InfoManager.InfoType.INFECTDATA);
                String newsString = instance.updateCovidData(InfoManager.InfoType.NEWSDATA);
                if (infectString != null) instance.saveJSON(fileDir + "infect_data.json", infectString);
                if (newsString != null) instance.saveJSON(fileDir + "news_data.json", newsString);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
