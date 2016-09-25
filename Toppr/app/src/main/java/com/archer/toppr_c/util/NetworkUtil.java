package com.archer.toppr_c.util;

/**
 * Created by Swastik on 21-08-2016.
 */
public class NetworkUtil
{
    //This is a test server used by me for experiments.
    public static final String BASE_URL = "https://todd-detection.herokuapp.com/";
    public static final String CREATE_USER = BASE_URL + "createUser";
    public static final String SOCIAL_LOGIN = BASE_URL + "socialLogin";
    public static final String GET_USER_DATA = BASE_URL + "getUserData";
    public static final String UPDATE_PASSWORD = BASE_URL + "updatePassword";
    public static final String CONNECT_ACCOUNT = BASE_URL + "connectAccount";
    public static final String USER_ID_LOGIN = BASE_URL + "loginUsingUserId";

    //Provided by hacker earth for the challenge
    public static final String FETCH_EVENTS =  "https://hackerearth.0x10.info/api/toppr_events?type=json&query=list_events";

    public static final String EMAIL = "email";
    public static final String F_NAME = "f_name";
    public static final String L_NAME = "l_name";
    public static final String GENDER = "gender";
    public static final String PHONE = "phone";
    public static final String DOB = "dob";
    public static final String COUNTRY = "country";
    public static final String AREA_2 = "area_2";
    public static final String AREA_1 = "area_1";
    public static final String USER_ID = "user_id";

    public static final String ID = "id";
    public static final String PASSWORD = "password";
    public static final String OLD_PASSWORD = "old_password";
    public static final String NEW_PASSWORD = "new_password";
}
