package com.archer.toppr_c.modules.login.manual;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.archer.toppr_c.R;
import com.archer.toppr_c.network.NetworkRequest;
import com.archer.toppr_c.network.OnTaskCompleted;
import com.archer.toppr_c.util.TopprUtils;
import com.archer.toppr_c.util.NetworkUtil;
import com.archer.toppr_c.util.SharedPreferenceManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Swastik on 11-08-2016.
 */
public class ManualLogin
        extends AppCompatActivity
        implements View.OnClickListener, OnTaskCompleted {


    private static final int RC_USER_ID_LOGIN = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maunal_login);
        getSupportActionBar().setTitle("Login");

        init();

    }

    private void init() {
        findViewById(R.id.btnLogin).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnLogin:
                String id = ((EditText)findViewById(R.id.etId)).getText().toString();
                String password = ((EditText)findViewById(R.id.etPassword)).getText().toString();
                boolean flag = false;
                if(id.isEmpty())
                {
                    ((TextInputLayout)findViewById(R.id.inputLayoutId)).setError("Enter a valid user name");
                    return;
                }
                else
                    ((TextInputLayout)findViewById(R.id.inputLayoutId)).setError(null);


                Map<String,String> reqParams = new HashMap<>();
                reqParams.put(NetworkUtil.ID,id);
                reqParams.put(NetworkUtil.PASSWORD,password);

                NetworkRequest req = new NetworkRequest(this);
                req.networkCall(Request.Method.POST,NetworkUtil.USER_ID_LOGIN,
                        reqParams, RC_USER_ID_LOGIN, this,true);
        }
    }


    @Override
    public void onTaskCompleted(String response, int requestCode) {
        Log.w(String.valueOf(requestCode),response);
        switch (requestCode)
        {
            case RC_USER_ID_LOGIN:
                if(response.contains("invalid"))
                    ((TextInputLayout)findViewById(R.id.inputLayoutPassword)).setError("Invalid credentials");


                else if(!response.contains("err"))
                {
                    String data[] = response.split("_");
                    SharedPreferenceManager pref = SharedPreferenceManager.getInstance(getApplicationContext());
                    pref.saveKeyData(TopprUtils.USER_ID,data[0]);
                    setResult(Activity.RESULT_OK);
                    finish();
                }
                else
                    TopprUtils.showAlertMessage("Oops..","Please try later",ManualLogin.this);
                break;
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        Log.w(String.valueOf(requestCode),response);
        switch (requestCode)
        {
            case RC_USER_ID_LOGIN:
                break;
        }
    }
}
