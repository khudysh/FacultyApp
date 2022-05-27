package com.example.rpm.modelsJSON;

import com.google.gson.annotations.SerializedName;

public class CountForm {

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