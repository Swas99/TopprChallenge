package com.archer.thought_works_got.network;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Swastik on 21-08-2016.
 */
public class MyStringRequest extends StringRequest
{
    Map<String, String> params;
    public MyStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public MyStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
        params = new HashMap<>();
    }

    @Override
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=UTF-8";
    }

    @Override
    protected Map<String, String> getParams(){
        return params;
    }

    public void setParams(Map<String,String> paramList)
    {
        if(paramList!=null)
            params = paramList;
        else
            params = new HashMap<>();

        params.put("secret_sauce", "runRunRun");
    }
}
