package com.archer.toppr_c.modules.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.archer.toppr_c.R;
import com.archer.toppr_c.modules.home.Home;
import com.archer.toppr_c.modules.login.manual.ManualLogin;
import com.archer.toppr_c.network.NetworkRequest;
import com.archer.toppr_c.network.OnTaskCompleted;
import com.archer.toppr_c.util.NetworkUtil;
import com.archer.toppr_c.util.SharedPreferenceManager;
import com.archer.toppr_c.util.TopprUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Swastik on 11-08-2016.
 */
public class Login
        extends AppCompatActivity
        implements OnTaskCompleted,
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_CREATE_USER = 101;
    private static final int RC_SOCIAL_LOGIN = 102;
    private static final int RC_MANUAL_LOGIN = 103;
    private static final int RC_G_SIGN_IN = 201;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferenceManager pref = SharedPreferenceManager.getInstance(this);
        if (pref.getKeyData(TopprUtils.USER_ID).isEmpty())
        {
            GoogleSignInOptions gso = new GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            setContentView(R.layout.activity_login);
            getSupportActionBar().setTitle("Toppr challenge");
            init();
        }
        else {
            Intent intent = new Intent(Login.this, Home.class);
            startActivity(intent);
            finish();
        }
    }

    private void init() {
        findViewById(R.id.btnAutoLogin).setOnClickListener(this);
        findViewById(R.id.btnLoginManual).setOnClickListener(this);
        findViewById(R.id.btnConnectGoogle).setOnClickListener(this);
        setGooglePlusButtonText(((SignInButton)findViewById(R.id.btnConnectGoogle)),"Connect using G+");

    }
    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                tv.setPadding(-20,0,0,0);
                return;
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String lName,fName;
            lName = "";
            fName = acct.getDisplayName();
            int indexOfSpace = acct.getDisplayName().indexOf(" ");
            if(indexOfSpace>0)
            {
                fName = acct.getDisplayName().substring(0,indexOfSpace);
                lName = acct.getDisplayName().substring(indexOfSpace+1);
            }
            LoginUsingGoogle(acct.getEmail(),fName,lName);
        }
        else
        {
            TopprUtils.showAlertMessage("Authentication error",
                    "Please try later",Login.this);
        }
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId())
        {
            case R.id.btnAutoLogin:
            {
                NetworkRequest req = new NetworkRequest(this);
                req.networkCall(Request.Method.POST,NetworkUtil.CREATE_USER,
                        null, RC_CREATE_USER, this,true);
                break;
            }
            case R.id.btnLoginManual:
                i = new Intent(Login.this, ManualLogin.class);
                startActivityForResult(i,RC_MANUAL_LOGIN);
                break;
            case R.id.btnConnectGoogle:
            {
                signIn();
                break;
            }

        }
    }

    private void LoginUsingGoogle(String email, String fName, String lName) {
        Map<String,String> reqParams = new HashMap<>();
        reqParams.put(NetworkUtil.EMAIL, email);
        reqParams.put(NetworkUtil.F_NAME,fName);
        reqParams.put(NetworkUtil.L_NAME,lName);
        reqParams.put(NetworkUtil.GENDER,"");
        reqParams.put(NetworkUtil.PHONE,"");
        reqParams.put(NetworkUtil.DOB,"");
        reqParams.put(NetworkUtil.COUNTRY,"");
        reqParams.put(NetworkUtil.AREA_1,"");
        reqParams.put(NetworkUtil.AREA_2,"");

        NetworkRequest req = new NetworkRequest(this);
        req.networkCall(Request.Method.POST,NetworkUtil.SOCIAL_LOGIN,
                reqParams, RC_SOCIAL_LOGIN, this,true);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_G_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode)
        {
            case RC_MANUAL_LOGIN:
            {
                if(resultCode == Activity.RESULT_OK){
                    finish();
                    startActivity(new Intent(Login.this, Home.class));
                }
                break;
            }
            case RC_G_SIGN_IN:
            {
//                if(resultCode == Activity.RESULT_OK)
                {
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    handleSignInResult(result);
                }
                break;
            }
        }
    }

    @Override
    public void onTaskCompleted(String response, int requestCode) {
//        Log.w(String.valueOf(requestCode),response);
        switch (requestCode)
        {
            case RC_CREATE_USER:
                TopprUtils.closeProgressBar();
                if(!response.contains("err"))
                {
                    SharedPreferenceManager pref = SharedPreferenceManager.getInstance(getApplicationContext());
                    pref.saveKeyData(TopprUtils.USER_ID,response);
                    finish();
                    startActivity(new Intent(Login.this, Home.class));
                }
                else
                    TopprUtils.showAlertMessage("Oops..","Please try later",Login.this);
                break;
            case RC_SOCIAL_LOGIN:
            {
                String data[] = response.split("_");
                SharedPreferenceManager pref = SharedPreferenceManager.getInstance(getApplicationContext());
                pref.saveKeyData(TopprUtils.USER_ID,data[0]);
                finish();
                startActivity(new Intent(Login.this, Home.class));
            }
                break;
        }
    }

    @Override
    public void onError(String response, int requestCode) {
        Log.w(String.valueOf(requestCode),response);
    }


    @Override
    public void onConnected(Bundle bundle) {
    }
    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
