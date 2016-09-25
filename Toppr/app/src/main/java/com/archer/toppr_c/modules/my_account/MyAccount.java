package com.archer.toppr_c.modules.my_account;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.archer.toppr_c.util.TopprUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.archer.toppr_c.R;
import com.archer.toppr_c.data_model.MyAccDO;
import com.archer.toppr_c.network.NetworkRequest;
import com.archer.toppr_c.network.OnTaskCompleted;
import com.archer.toppr_c.util.MySQLiteHelper;
import com.archer.toppr_c.util.NetworkUtil;
import com.archer.toppr_c.util.SharedPreferenceManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MyAccount
        extends AppCompatActivity
        implements
        View.OnClickListener, OnTaskCompleted,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{

    private static final int RC_SAVE_USER_DATA = 101;
    private static final int RC_CONNECT_GOOGLE = 102;
    private static final int RC_UPDATE_PASSWORD = 103;
    private static final int RC_GET_USER_DATA = 104;
    private static final int RC_GOOGLE_SIGN_IN = 201;

    int tag;
    Date dob;
    Dialog dialog;
    String newPassword;
    EditText et = null;
    TextInputLayout etLayout = null;

    NetworkRequest req;
    private GoogleApiClient mGoogleApiClient;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_account, menu);

        MenuItem menuItem;
        menuItem = menu.add(Menu.NONE, R.id.menu_sign_out, 0, "Sign out");
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Activate the navigation drawer toggle

        switch (item.getItemId())
        {
            case R.id.menu_sign_out:
            {
                SharedPreferenceManager pref = SharedPreferenceManager.getInstance(getApplicationContext());
                pref.deleteKey(TopprUtils.USER_ID);
                setResult(Activity.RESULT_OK);
                finish();
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        getSupportActionBar().setTitle("My account");
        init();

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void init() {
        req = new NetworkRequest(MyAccount.this);

        findViewById(R.id.tvDob).setOnClickListener(this);
        findViewById(R.id.tvName).setOnClickListener(this);
        findViewById(R.id.tvPhone).setOnClickListener(this);
        findViewById(R.id.tvCountry).setOnClickListener(this);
        findViewById(R.id.tvPassword).setOnClickListener(this);
        findViewById(R.id.btnSave).setOnClickListener(this);
        findViewById(R.id.btn_edit_dob).setOnClickListener(this);
        findViewById(R.id.btn_edit_name).setOnClickListener(this);
        findViewById(R.id.btn_edit_phone).setOnClickListener(this);
        findViewById(R.id.btnConnectGoogle).setOnClickListener(this);
        findViewById(R.id.btn_edit_country).setOnClickListener(this);
        findViewById(R.id.btn_edit_password).setOnClickListener(this);
        fetchAccData();

        setGooglePlusButtonText(((SignInButton)findViewById(R.id.btnConnectGoogle)),"Connect G+");
    }
    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                tv.setPadding(-27,0,0,0);
                return;
            }
        }
    }


    private void fetchAccData() {
        MyAccDO acc = MySQLiteHelper.fetchRowFromAccountsTable(getApplicationContext(),getUserId());
        if(acc!=null)
        {
            dob = acc.getDob();
            loadAccData(acc);
        }
        else
        {
            Map<String,String> reqParams = new HashMap<>();
            reqParams.put(NetworkUtil.ID,getUserId());
            req.networkCall(Request.Method.POST,NetworkUtil.GET_USER_DATA,
                    reqParams, RC_GET_USER_DATA, this, true);
        }
    }

    private String getUserId() {
        SharedPreferenceManager pref = SharedPreferenceManager.getInstance(getApplicationContext());
        return pref.getKeyData(TopprUtils.USER_ID);
    }

    private void loadAccData(MyAccDO acc) {

        String dob = TopprUtils.getDateInText(acc.getDob());

        ((TextView)findViewById(R.id.tvDob)).setText(dob);
        String name = acc.getF_name() + " " + acc.getL_name();
        ((TextView)findViewById(R.id.tvName)).setText(name);
        ((TextView)findViewById(R.id.tvPhone)).setText(acc.getPhone());
        ((TextView)findViewById(R.id.tvUserId)).setText(acc.getUser_id());
        ((TextView)findViewById(R.id.tvCountry)).setText(acc.getCountry());
        if(acc.getPassword().isEmpty())
            findViewById(R.id.tvPasswordMessage).setVisibility(View.VISIBLE);
        else
            ((TextView)findViewById(R.id.tvPassword)).setText(acc.getPassword());

        if(acc.getEmail().isEmpty())
            findViewById(R.id.btnConnectGoogle).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.btnConnectGoogle).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {


        switch(v.getId())
        {
            case R.id.tvPassword:
            case R.id.btn_edit_password:
                dialog = getDialog(R.layout.dialog_change_password);
                dialog.findViewById(R.id.btnCancel).setOnClickListener(this);
                dialog.findViewById(R.id.btnSavePassword).setOnClickListener(this);
                break;
            case R.id.tvName:
            case R.id.btn_edit_name:
                tag = R.id.btn_edit_name;
                loadTextInputDialog("Enter your Name",InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                et.setText(((TextView)findViewById(R.id.tvName)).getText());
                break;
            case R.id.tvDob:
            case R.id.btn_edit_dob:
                LaunchDatePicker();
                break;
            case R.id.tvCountry:
            case R.id.btn_edit_country:
                tag = R.id.btn_edit_country;
                loadTextInputDialog("Enter your country name",InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                et.setText(((TextView)findViewById(R.id.tvCountry)).getText());
                break;
            case R.id.tvPhone:
            case R.id.btn_edit_phone:
                tag = R.id.btn_edit_phone;
                loadTextInputDialog("Enter your phone number",InputType.TYPE_CLASS_PHONE);
                et.setText(((TextView)findViewById(R.id.tvPhone)).getText());
                break;
            case R.id.btnCancel:
                dialog.dismiss();
                break;
            case R.id.btnOk:
                processInputData();
                break;
            case R.id.btnSavePassword:
            {
                String oldPassword = ((EditText)dialog.findViewById(R.id.etOldPassword)).getText().toString();
                String newPassword = ((EditText)dialog.findViewById(R.id.etNewPassword)).getText().toString();
                String newPassword2 = ((EditText)dialog.findViewById(R.id.etReEnterNewPassword)).getText().toString();
                boolean flag = false;
                if(newPassword.length()<6)
                {
                    ((TextInputLayout)dialog.findViewById(R.id.inputLayoutNewPassword))
                            .setError("password must have 6 or more characters");
                    flag=true;
                }
                else
                    ((TextInputLayout)dialog.findViewById(R.id.inputLayoutNewPassword)).setError(null);
                if(newPassword2.length()<6)
                {
                    ((TextInputLayout)dialog.findViewById(R.id.inputLayoutReEnterNewPassword))
                            .setError("password must have 6 or more characters");
                    flag=true;
                }else
                    ((TextInputLayout)dialog.findViewById(R.id.inputLayoutReEnterNewPassword)).setError(null);
                if(flag)
                    return;
                if(!newPassword.equals(newPassword2))
                {
                    ((TextInputLayout)dialog.findViewById(R.id.inputLayoutNewPassword))
                            .setError("passwords do not match");
                    ((TextInputLayout)dialog.findViewById(R.id.inputLayoutReEnterNewPassword))
                            .setError("passwords do not match");
                    return;
                }
                dialog.dismiss();
                this.newPassword = newPassword;
                updatePassword(oldPassword,newPassword);
            }

                break;
            case R.id.btnConnectGoogle:
            {
                connect();
                break;
            }
            case R.id.btnSave:
            {
                String name = String.valueOf(((TextView)findViewById(R.id.tvName)).getText());
                String country = String.valueOf(((TextView)findViewById(R.id.tvCountry)).getText());
                String phone = String.valueOf(((TextView)findViewById(R.id.tvPhone)).getText());
                MySQLiteHelper.saveAccData(MyAccount.this,
                        getUserId(),name,dob,country,phone);
                finish();
            }
                break;
        }
    }

    private void updatePassword(String oldPassword, String newPassword) {

        SharedPreferenceManager pref = SharedPreferenceManager.getInstance(getApplicationContext());
        Map<String,String> reqParams = new HashMap<>();
        reqParams.put(NetworkUtil.USER_ID,pref.getKeyData(TopprUtils.USER_ID));
        reqParams.put(NetworkUtil.OLD_PASSWORD,oldPassword);
        reqParams.put(NetworkUtil.NEW_PASSWORD, newPassword);
        NetworkRequest req = new NetworkRequest(this);
        req.networkCall(Request.Method.POST,NetworkUtil.UPDATE_PASSWORD,
                reqParams, RC_UPDATE_PASSWORD, this, true);
    }

    private void ConnectGoogleAcc(String email,String f_name,String l_name) {

        Map<String,String> reqParams = new HashMap<>();
        reqParams.put(NetworkUtil.F_NAME,f_name);
        reqParams.put(NetworkUtil.L_NAME,l_name);
        reqParams.put(NetworkUtil.EMAIL,email);
        reqParams.put(NetworkUtil.GENDER,"");
        reqParams.put(NetworkUtil.PHONE,"");
        reqParams.put(NetworkUtil.DOB,"");
        reqParams.put(NetworkUtil.COUNTRY,"");
        reqParams.put(NetworkUtil.AREA_1,"");
        reqParams.put(NetworkUtil.AREA_2,"");
        reqParams.put(NetworkUtil.USER_ID,getUserId());

        NetworkRequest req = new NetworkRequest(this);
        req.networkCall(Request.Method.POST,NetworkUtil.CONNECT_ACCOUNT,
                reqParams, RC_CONNECT_GOOGLE, this, true);
    }

    private void connect() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
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
            ConnectGoogleAcc(acct.getEmail(),fName,lName);
        }
        else
        {
            TopprUtils.showAlertMessage("Authentication error",
                    "Please try later",MyAccount.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode)
        {
            case RC_GOOGLE_SIGN_IN:
            {
                if(resultCode == Activity.RESULT_OK)
                {
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    handleSignInResult(result);
                }
                break;
            }
        }
    }


    private void loadTextInputDialog(String hint, int inputType) {
        dialog = getDialog(R.layout.dialog_text_input);
        dialog.findViewById(R.id.btnOk).setOnClickListener(this);
        dialog.findViewById(R.id.btnCancel).setOnClickListener(this);
        et = ((EditText)dialog.findViewById(R.id.etTextInput));
        etLayout = ((TextInputLayout) dialog.findViewById(R.id.inputLayoutTextInput));
        etLayout.setHint(hint);
        et.setInputType(inputType);
    }

    private void processInputData() {
        String input = et.getText().toString();
        switch (tag)
        {
            case R.id.btn_edit_name:
                if(input.length()<2 || input.length()>20)
                {
                    etLayout.setError("Enter a valid name");
                    return;
                }
                else
                    etLayout.setError(null);

                ((TextView)findViewById(R.id.tvName)).setText(input);
                //process data

                dialog.dismiss();
                break;
            case R.id.btn_edit_country:
                if(input.length()>18)
                {
                    etLayout.setError("Too long. Use less than 18 characters");
                    return;
                }
                else
                    etLayout.setError(null);

                ((TextView)findViewById(R.id.tvCountry)).setText(input);
                //process data

                dialog.dismiss();
                break;
            case R.id.btn_edit_phone:
                if(input.length()<10 || input.length()>14)
                {
                    etLayout.setError("Enter a valid phone number");
                    return;
                }
                else
                    etLayout.setError(null);

                ((TextView)findViewById(R.id.tvPhone)).setText(input);
                //process data

                dialog.dismiss();
                break;
        }
    }

    public Dialog getDialog(int id)
    {
        if(dialog==null)
        {
            dialog = new AlertDialog.Builder(this).show();
            dialog.setCancelable(true);
//            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//            Window window = dialog.getWindow();
//            lp.copyFrom(window.getAttributes());
//            lp.width = JojoUtils.getScreenWidth(getWindowManager().getDefaultDisplay())
//                    - JojoUtils.ConvertToPx(getApplicationContext(), 40); //WindowManager.LayoutParams.WRAP_CONTENT;
//            window.setAttributes(lp);
        }
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(id, null, true);
        dialog.setContentView(view);
        dialog.show();
        dialog.getWindow()
                .clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    private void LaunchDatePicker()
    {
        DatePickerFragment  newFragment;
        newFragment = new DatePickerFragment();
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        newFragment.setArguments(args);
        DatePickerDialog.OnDateSetListener onDateSet = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                String date = String.valueOf(dayOfMonth) + " " +
                        TopprUtils.getMonthName(monthOfYear) + ", " +
                        String.valueOf(year);

                ((TextView)findViewById(R.id.tvDob)).setText(date);

                String startDateString = String.format(Locale.ENGLISH,"%02d/%02d/%s",
                        monthOfYear, dayOfMonth, year);
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    dob = df.parse(startDateString);
                } catch (Exception e) {}
            }
        };
        newFragment.setCallBack(onDateSet);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onTaskCompleted(String response, int requestCode) {
        Log.w(String.valueOf(requestCode),response);
        switch(requestCode)
        {
            case RC_SAVE_USER_DATA:

                break;
            case RC_CONNECT_GOOGLE:
            {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    MyAccDO acc = objectMapper.readValue(response, new TypeReference<MyAccDO>(){} );
                    MySQLiteHelper.insertRowToAccountsTable(getApplicationContext(),acc);

                    SharedPreferenceManager pref = SharedPreferenceManager.getInstance(getApplicationContext());
                    pref.saveKeyData(TopprUtils.USER_ID,String.valueOf(acc.getUser_id()));

                    loadAccData(acc);
                }catch (Exception e)
                {
//                    Log.w(String.valueOf(requestCode),e.toString());
                }
            }
                break;
            case RC_UPDATE_PASSWORD:
            {
                String title = "Failure";
                String msg = "Old password did not match our records";
                if(response.equals(TopprUtils.OK))
                {
                    title = "Success";
                    msg = "Password changed successfully";
                    ((TextView)findViewById(R.id.tvPassword)).setText(newPassword);
                    ((TextView)findViewById(R.id.tvPasswordMessage)).setText("");
                }
                TopprUtils.showAlertMessage(title,msg,MyAccount.this);
                MySQLiteHelper.updatePassword(MyAccount.this,getUserId(),newPassword);
            }
                break;
            case RC_GET_USER_DATA:
            {
                try
                {
                    ObjectMapper objectMapper = new ObjectMapper();
                    MyAccDO acc = objectMapper.readValue(response, MyAccDO.class);
                    dob = acc.getDob();
                    MySQLiteHelper.insertRowToAccountsTable(getApplicationContext(),acc);
                    loadAccData(acc);
                }
                catch (Exception e) {
//                    Log.w(">>",e);
                }

            }
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static class DatePickerFragment extends DialogFragment {
        DatePickerDialog.OnDateSetListener onDateSet;

        public DatePickerFragment() {
        }

        public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
            onDateSet = ondate;
        }

        private int year, month, day;

        @Override
        public void setArguments(Bundle args) {
            super.setArguments(args);
            year = args.getInt("year");
            month = args.getInt("month");
            day = args.getInt("day");
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(getActivity(), onDateSet, year, month, day);
        }
    }

}