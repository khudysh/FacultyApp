package com.example.rpm.models;

import com.google.gson.annotations.SerializedName;

public class Person {

    @SerializedName("title")
    public String title;

    @SerializedName("countBudget")
    public String countBudget;

    @SerializedName("countDogovor")
    public String countDogovor;

    @Override
    public String toString() {
        return(title + ' ' + countBudget + ' ' + countDogovor);
    }
}