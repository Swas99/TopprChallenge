package com.archer.toppr_c.data_model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * Created by Swastik on 25-08-2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MyAccDO
{
    private Date dob;
    private Long wallet;
    private String email;
    private String phone;
    private String f_name;
    private String l_name;
    private String user_id;
    private String country;
    private String password;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getDob() {
        if(dob==null)
            dob = new Date();
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Long getWallet() {
        return wallet;
    }

    public void setWallet(Long wallet) {
        this.wallet = wallet;
    }

    public String getEmail() {
        return email==null? "" :  email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getF_name() {
        return f_name == null ? "" : f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getL_name() {
        return l_name == null ? "" : l_name;
    }

    public void setL_name(String l_name) {
        this.l_name = l_name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone==null? "":phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password==null? "" : password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
