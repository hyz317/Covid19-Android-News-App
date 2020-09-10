package com.java.heyuze;

import com.tencent.mm.opensdk.openapi.*;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppRegister extends BroadcastReceiver
{
    private String APP_ID = "wxa4141da31840665b";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        final IWXAPI api = WXAPIFactory.createWXAPI(context, null);
        api.registerApp(APP_ID);
    }

    public String getAppID()
    {
        return APP_ID;
    }
}

