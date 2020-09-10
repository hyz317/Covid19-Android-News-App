package com.java.heyuze;

import android.content.Context;
import android.icu.text.IDNA;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;

public class DictionaryHandler implements Runnable
{
    public  DictionaryHandler() {}

    @Override
    public void run()
    {
        try
        {
            synchronized (this)
            {
                InfoManager.getInstance().loadJieba();
                while (true)
                {
                    if (!InfoManager.getInstance().dictNeedUpdate) wait(500);
                    else
                    {
                        InfoManager.getInstance().dictNeedUpdate = false;
                        InfoManager.getInstance().loadDictionary();
                        System.out.println("Update Dict Sucuess!");
                    }
                }
            }
        } catch (Exception e)
        {
        	e.printStackTrace();
        }
    }
}
