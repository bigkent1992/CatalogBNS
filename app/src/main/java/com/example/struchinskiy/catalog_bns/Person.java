package com.example.struchinskiy.catalog_bns;

import java.io.Serializable;
import java.util.Date;

public class Person implements Serializable{
    private int id;
    private String fio;
    private String job;
    private String podrazdel;
    private String short_tel;
    private String tel;
    private String mobile;
    private String e_mail;
    private String birth_day;

    Person(String fio, String job, String podrazdel, String short_tel, String tel, String mobile, String e_mail) {
    //    this.id = id;
        this.fio = fio;
        this.job = job;
        this.podrazdel = podrazdel;
        this.short_tel = short_tel;
        this.tel = tel;
        this.mobile = mobile;
        this.e_mail = e_mail;
      //  this.birth_date = birth_date;
    }

    Person(String fio, String job, String podrazdel, String short_tel, String tel, String mobile, String e_mail, String birth_day) {
        //    this.id = id;
        this.fio = fio;
        this.job = job;
        this.podrazdel = podrazdel;
        this.short_tel = short_tel;
        this.tel = tel;
        this.mobile = mobile;
        this.e_mail = e_mail;
        this.birth_day = birth_day;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getShort_tel() {
        return short_tel;
    }

    public void setShort_tel(String short_tel) {
        this.short_tel = short_tel;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getE_mail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }

    public String getBirth_date() {
        return birth_day;
    }

    public void setBirth_date(String birth_date) {
        this.birth_day = birth_date;
    }

    public String getPodrazdel() {
        return podrazdel;
    }

    public void setPodrazdel(String podrazdel) {
        this.podrazdel = podrazdel;
    }
}
