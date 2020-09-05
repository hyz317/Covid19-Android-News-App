package com.java.heyuze;

import android.util.Log;

public class HTTPHandler implements Runnable
{

	public HTTPHandler () {}

	@Override
	public void run()
	{
		Log.i("TEST",  InfoManager.getInstance().getCovidData(InfoManager.InfoType.DATA));
	}
}
