package com.archer.toppr_c.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.archer.toppr_c.data_model.MyAccDO;
import com.archer.toppr_c.data_model.EventDO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Swastik Sahu
 *
 */
public class MySQLiteHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "toppr.db";
    private static final int DATABASE_VERSION = 1;


    //region FAVORITE table - keys
    public static final String TABLE_FAV = "t_fav";
    //    public static final String COLUMN_USER_ID = "u_id";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_EXPERIENCE = "experience";
    public static final String COLUMN_DESCRIPTION = "description";
    //endregion

    //region ACCOUNT table - keys

    public static final String TABLE_ACCOUNT = "accounts";
    //    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID = "u_id";
    public static final String COLUMN_F_NAME = "fNamme";
    public static final String COLUMN_L_NAME = "lNamme";
    public static final String COLUMN_DOB = "dob";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_PASSWORD = "password";

    //endregion


    // Database creation sql statement
    //region create FAVORITE table
    private static final String CREATE_FAVORITE_TABLE =
            "create table " + TABLE_FAV + "("
                    + COLUMN_ID + " integer primary key , "
                    + COLUMN_USER_ID + " text, "
                    + COLUMN_NAME + " text , "
                    + COLUMN_IMAGE + " text , "
                    + COLUMN_CATEGORY + " text , "
                    + COLUMN_EXPERIENCE + " text , "
                    + COLUMN_DESCRIPTION + " text"
                    + " );";
    //endregion

    //region create ACCOUNT table
    private static final String CREATE_ACCOUNTS_TABLE =
            "create table " + TABLE_ACCOUNT + "("
                    + COLUMN_ID + " integer primary key autoincrement, "
                    + COLUMN_USER_ID + " text UNIQUE, "
                    + COLUMN_F_NAME + " text , "
                    + COLUMN_L_NAME + " text , "
                    + COLUMN_DOB + " text , "
                    + COLUMN_EMAIL + " text , "
                    + COLUMN_PHONE + " text , "
                    + COLUMN_COUNTRY + " text , "
                    + COLUMN_PASSWORD + " text"
                    + " );";
    //endregion


    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FAVORITE_TABLE);
        db.execSQL(CREATE_ACCOUNTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAV);
        onCreate(db);
    }

    //region Account table operations

    public static void insertRowToAccountsTable(Context context, MyAccDO acc) {
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_USER_ID, acc.getUser_id());
        values.put(MySQLiteHelper.COLUMN_EMAIL, acc.getEmail());
        values.put(MySQLiteHelper.COLUMN_PHONE, acc.getPhone());
        values.put(MySQLiteHelper.COLUMN_F_NAME, acc.getF_name());
        values.put(MySQLiteHelper.COLUMN_L_NAME, acc.getL_name());
        values.put(MySQLiteHelper.COLUMN_COUNTRY, acc.getCountry());
        values.put(MySQLiteHelper.COLUMN_PASSWORD, acc.getPassword());
        values.put(MySQLiteHelper.COLUMN_DOB, acc.getDob().getTime());
        database.insertWithOnConflict(MySQLiteHelper.TABLE_ACCOUNT, null, values,SQLiteDatabase.CONFLICT_REPLACE);
        database.close();
    }

    public static MyAccDO fetchRowFromAccountsTable(Context context,String uId) {
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

//        String selectQuery = "SELECT * FROM " + TABLE_ACCOUNT;
//        Cursor c = database.rawQuery(selectQuery, null);

        Cursor c = database.query(TABLE_ACCOUNT,
                new String[]{},
                COLUMN_USER_ID+" = ?",
                new String[]{uId},
                null,
                null,
                null
        );
        MyAccDO acc = null;
        if (c.moveToNext()) {
            acc = new MyAccDO();
            acc.setUser_id(c.getString(c.getColumnIndex(COLUMN_USER_ID)));
            acc.setEmail(c.getString(c.getColumnIndex(COLUMN_EMAIL)));
            acc.setPhone(c.getString(c.getColumnIndex(COLUMN_PHONE)));
            acc.setF_name(c.getString(c.getColumnIndex(COLUMN_F_NAME)));
            acc.setL_name(c.getString(c.getColumnIndex(COLUMN_L_NAME)));
            acc.setCountry(c.getString(c.getColumnIndex(COLUMN_COUNTRY)));
            acc.setPassword(c.getString(c.getColumnIndex(COLUMN_PASSWORD)));
            acc.setDob(new Date(c.getLong(c.getColumnIndex(COLUMN_DOB))));
        }
        c.close();
        database.close();
        return acc;
    }

    public static void updatePassword(Context context, String userId, String newPassword) {
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PASSWORD,newPassword);
        database.update(TABLE_ACCOUNT, cv, COLUMN_USER_ID + "=?",new String[]{userId});
        database.close();
    }
    public static void saveAccData(Context context, String userId, String name,
                                   Date dob, String country, String phone) {
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_F_NAME,name);
        cv.put(COLUMN_L_NAME,"");
        cv.put(COLUMN_DOB,dob.getTime());
        cv.put(COLUMN_COUNTRY,country);
        cv.put(COLUMN_PHONE,phone);
        database.update(TABLE_ACCOUNT, cv, COLUMN_USER_ID + "=?",new String[]{userId});
        database.close();
    }

    //endregion

    //region Fav table operations

    public static void insertRowToFavoriteTable(Context context, EventDO result, String u_id)
    {
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_USER_ID, u_id);

        values.put(MySQLiteHelper.COLUMN_ID, result.getId());
        values.put(MySQLiteHelper.COLUMN_NAME, result.getName());
        values.put(MySQLiteHelper.COLUMN_IMAGE, result.getImage());
        values.put(MySQLiteHelper.COLUMN_CATEGORY, result.getCategory());
        values.put(MySQLiteHelper.COLUMN_EXPERIENCE, result.getExperience());
        values.put(MySQLiteHelper.COLUMN_DESCRIPTION, result.getDescription());
        database.insertWithOnConflict(MySQLiteHelper.TABLE_FAV, null, values,SQLiteDatabase.CONFLICT_REPLACE);
        database.close();
    }

    public static void deleteRowFromFavTable(Context context, String u_id,String id)
    {
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(TABLE_FAV,
                COLUMN_USER_ID + "=? and " + COLUMN_ID + "=?"
                , new String[]{u_id,id});
        database.close();
    }

    public static List<EventDO> fetchFavEventsFromFavTable(Context context, String u_id) {
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor c = database.query(TABLE_FAV, new String[]{},
                COLUMN_USER_ID+" = ?",
                new String[]{u_id},
                null,
                null,
                null
        );

        List<EventDO> resultList = new ArrayList<>();
        EventDO result = null;
        while (c.moveToNext())
        {
            result = new EventDO();
            result.setId(c.getString(c.getColumnIndex(COLUMN_ID)));
            result.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));
            result.setImage(c.getString(c.getColumnIndex(COLUMN_IMAGE)));
            result.setCategory(c.getString(c.getColumnIndex(COLUMN_CATEGORY)));
            result.setExperience(c.getString(c.getColumnIndex(COLUMN_EXPERIENCE)));
            resultList.add(result);
        }

        c.close();
        database.close();
        return resultList;
    }

    public static Boolean checkEventForFavorite(Context context,String u_id,String id) {
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        boolean isFav;
        Cursor c = database.query(TABLE_FAV, new String[]{},
                COLUMN_USER_ID + "=? and " + COLUMN_ID + "=?",
                new String[]{u_id,id},
                null,
                null,
                null
        );
        isFav = c.moveToNext();
        c.close();
        database.close();

        return isFav;
    }

    //endregion

}