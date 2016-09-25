package com.archer.toppr_c.network;


import android.content.Context;
import android.os.StrictMode;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.archer.toppr_c.util.TopprUtils;

import java.util.Map;

/**
 * Created by Swastik on 21-08-2016.
 */
public class NetworkRequest implements Response.ErrorListener, Response.Listener<String> {

    private OnTaskCompleted mTaskCompleted;
    private Context mContext = null;
    RequestQueue queue;

    private String url;
    private int requestCode;
    private Map<String,String> requestObject;
    private boolean showPleaseWait;

    public NetworkRequest(Context lContext) {
        mContext = lContext;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        queue = Volley.newRequestQueue(mContext);
    }

    public void networkCall(int method,String _url,Map<String,String> reqParams,
                            int _requestCode, OnTaskCompleted lTaskCompleted,
                            boolean showPleaseWait)
    {
        this.url = _url;
        this.requestCode = _requestCode;
        requestObject = reqParams;
        mTaskCompleted = lTaskCompleted;

        int socketTimeout = 30000;//30 seconds
        RetryPolicy policy =
                new DefaultRetryPolicy(socketTimeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES+2,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        MyStringRequest myReq = new MyStringRequest(method,
                url,this, this);
        myReq.setParams(requestObject);
        myReq.setRetryPolicy(policy);
        queue.add(myReq);

        if(showPleaseWait)
            ShowPleaseWaitDialog();
    }

    private void ShowPleaseWaitDialog() {
        this.showPleaseWait = true;
        TopprUtils.showProgressBar(mContext);
    }

    public void fetchEvents(String _url,int _requestCode, OnTaskCompleted lTaskCompleted)
    {
        this.url = _url;
        this.requestCode = _requestCode;
        mTaskCompleted = lTaskCompleted;

        int socketTimeout = 30000;//30 seconds
        RetryPolicy policy =
                new DefaultRetryPolicy(socketTimeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES+2,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        MyStringRequest myReq = new MyStringRequest(Request.Method.GET,
                url,this, this);
        myReq.setRetryPolicy(policy);
        queue.add(myReq);

        ShowPleaseWaitDialog();
    }



    private boolean getBoolean(Object obj) {
        return Boolean.parseBoolean(String.valueOf(obj));
    }

    private int getInt(Object obj)
    {
        return Integer.parseInt(String.valueOf(obj));
    }

    private String getString(Object obj)
    {
        return String.valueOf(obj);
    }


    @Override
    public void onResponse(String response) {
        if(showPleaseWait)
            TopprUtils.closeProgressBar();

        mTaskCompleted.onTaskCompleted(response, requestCode);
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        if(showPleaseWait)
            TopprUtils.closeProgressBar();

        mTaskCompleted.onError(error.toString(), requestCode);

        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            Toast.makeText(mContext,
                    "Check your connection",
                    Toast.LENGTH_LONG).show();
        } else if (error instanceof AuthFailureError) {
            //TODO implement the error case handling for particular error
        } else if (error instanceof ServerError) {
            Toast.makeText(mContext,
                    "Please try again later",
                    Toast.LENGTH_LONG).show();
        } else if (error instanceof NetworkError) {
            //TODO implement the error case handling for particular error
        } else {
            Toast.makeText(mContext,
                    "Please try again later",
                    Toast.LENGTH_LONG).show();
        }
    }


}