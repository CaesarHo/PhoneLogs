package com.caesar.phonelogs.data;

import java.io.Serializable;

/**
 * Created by heshengfang on 2017/9/22.
 */

public class ContactInfo implements Serializable {
    private int phoneType;
    private String number;

    public int getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(int phoneType) {
        this.phoneType = phoneType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
