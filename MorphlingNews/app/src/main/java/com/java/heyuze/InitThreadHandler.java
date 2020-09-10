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
                instance.loadScholarData();

                /*
                Vector<ScholarData> test1 = instance.getScholarData(ScholarData.ScholarType.HIGHATTENTION);
                Vector<ScholarData> test2 = instance.getScholarData(ScholarData.ScholarType.PASSAWAY);
                System.out.println(test1.size());
                System.out.println(test2.size());
                System.out.println(test1.lastElement().gIndex);
                System.out.println(test1.lastElement().activity);
                for (LinkedHashMap.Entry<String, Double> entry: test1.firstElement().detail.tag.entrySet())
                    System.out.println(entry.getKey() + "  " + entry.getValue());
                System.out.println(test2.firstElement().pubs);
                System.out.println(test2.firstElement().pDetail.passawayMonth);
                System.out.println(test2.firstElement().pDetail.passawayDay);
                System.out.println(test2.firstElement().pDetail.passawayReason);
                 */

                String infectString = instance.updateCovidData(InfoManager.InfoType.INFECTDATA);
                String newsString = instance.updateCovidData(InfoManager.InfoType.NEWSDATA);
                if (infectString != null) instance.saveJSON(fileDir + "infect_data.json", infectString);
                if (newsString != null) instance.saveJSON(fileDir + "news_data.json", newsString);
            }

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
