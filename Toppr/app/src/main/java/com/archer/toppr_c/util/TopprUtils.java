package com.archer.toppr_c.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Created by udit on 11/17/2015.
 */
public class TopprUtils {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ROW_ITEM = 1;
    public static final int TYPE_FOOTER = 2;


    public static final int RC_MY_ACC_DATA = 120;
    public static final String USER_ID = "uId";

    public static final String USER_FULL_NAME = "uName";
    public static final String OK = "ok";
    public static final String DOB = "dob";
    public static final String GENDER = "gender";
    public static final String MALE = "m";
    public static final String FEMALE = "f";
    public static final String OTHERS = "o";
    public static final String NAME = "name";
    public static final String NO = "0";
    public static final String YES = "1";
    public static final String Q_1 = "q1";
    public static final String Q_2 = "q2";
    public static final String HIRING = "hiring";
    public static final String ID = "id";
    public static final String IMAGE = "image";
    public static final String IS_FAV = "isFav";
    public static final String CATEGORY = "category";
    public static final String EXPERIENCE = "exp";
    public static final String DESCRIPTION = "desc";

    private static ProgressDialog progressBar;



    public static void showProgressBar(Context context) {
        try
        {
            progressBar = ProgressDialog.show(context,"","Processing..");
        }
        catch (Exception ex) {}
    }

    public static void closeProgressBar() {
        try {
            if (progressBar != null)
                progressBar.dismiss();
        }catch (Exception ex) {}
    }

    public static void showAlertMessage(String title, String message,
                                        Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setTitle(title);

        alertDialogBuilder.setMessage(message).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }
    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    public static String getMonthName(int month) {
        month++;
        switch (month)
        {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
        }
        return "";
    }


    public static String getDateInText(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        String dob = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        dob+= " " + TopprUtils.getMonthName(c.get(Calendar.MONTH));
        dob+= ", " + String.valueOf(c.get(Calendar.YEAR));

        return dob;
     }

    public static String encodeBitmapToBase64(Bitmap bmp)
    {
        byte[] image_base64;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 99, baos);
        image_base64 = baos.toByteArray();
        return Base64.encodeToString(image_base64, Base64.DEFAULT);
    }

    public static Bitmap decodeBase64ToBitmap(String encodedImage)
    {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
