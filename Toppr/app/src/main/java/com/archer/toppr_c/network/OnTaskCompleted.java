package com.archer.toppr_c.network;

/**
 * Created by Swastik on 21-08-2016.
 */
public interface OnTaskCompleted {

    void onTaskCompleted(String response, int requestCode);
    void onError(String response, int requestCode);


}


