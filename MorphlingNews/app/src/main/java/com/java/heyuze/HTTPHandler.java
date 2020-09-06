package com.java.heyuze;

import android.util.Log;

import java.util.Vector;

public class HTTPHandler implements Runnable
{
	public HTTPHandler () {}

	@Override
	public void run()
	{
		InfoManager instance = InfoManager.getInstance();
		instance.LoadCovidData(InfoManager.InfoType.INFECTDATA);
		instance.LoadCovidData(InfoManager.InfoType.NEWSDATA);
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
	}
}
