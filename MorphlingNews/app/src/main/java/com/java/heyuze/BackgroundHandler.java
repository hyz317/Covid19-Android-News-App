package com.java.heyuze;

import android.content.Context;
import android.icu.text.IDNA;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;

public class BackgroundHandler implements Runnable
{
    private String fileDir;
    public BackgroundHandler(String fileDir)
    {
        this.fileDir = fileDir;
    }

    @Override
    public void run()
    {
        try
        {
            InfoManager instance = InfoManager.getInstance();
            // instance.loadJSON(InfoManager.InfoType.NEWSDATA, fileDir + "news_data.json");
            // instance.loadJSON(InfoManager.InfoType.INFECTDATA, fileDir + "infect_data.json");

            String infectString = instance.updateCovidData(InfoManager.InfoType.INFECTDATA);
            String newsString = instance.updateCovidData(InfoManager.InfoType.NEWSDATA);
            if (infectString != null) instance.saveJSON(fileDir + "infect_data.json", infectString);
            if (newsString != null) instance.saveJSON(fileDir + "news_data.json", newsString);

            /*
			Vector<NewsData> data = instance.getNewsData();
			System.out.println(data.get(5).id);
			System.out.println(data.get(5).time);
			System.out.println(data.get(5).title);
			Vector<InfectData> data2 = instance.getInfectData();
			System.out.println(data2.get(12).beginDate);
			System.out.println(data2.get(12).location.size());
			System.out.println(data2.get(12).location.get(0));
			System.out.println(data2.get(12).location.get(1));
			System.out.println(data2.get(12).confirmed.get(5));
			*/
        } catch (Exception e)
        {
        	e.printStackTrace();
        }
    }
}
