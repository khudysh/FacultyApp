package com.example.rpm.controllers;

import android.util.Log;

import com.example.rpm.models.Person;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiService {

    //private ArrayList<Person> allPerson = new ArrayList<>();
    public void executePerson() throws Exception {
        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://94.103.188.48/FacultyApp/students.php")
                .build();
        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            System.out.println(response.code());
            Gson g = new Gson();
            JSONArray json = new JSONArray(response.body().string());
            //Log.d("MyLog",""+);
            for (int i=0; i < json.length(); i++) {
                Person person = g.fromJson(json.getJSONObject(i).toString(), Person.class);
                //allPerson.add(person);
                Log.d("MyLogi",person.toString());
            }
            Log.d("MyLog",response.body().string());
        }
    }

    public void AddStudent() throws Exception {
        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://94.103.188.48/FacultyApp/students.php")
                .build();
        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            System.out.println(response.code());
            Gson g = new Gson();
            JSONArray json = new JSONArray(response.body().string());
            //Log.d("MyLog",""+);
            for (int i=0; i < json.length(); i++) {
                Person person = g.fromJson(json.getJSONObject(i).toString(), Person.class);
                //allPerson.add(person);
                Log.d("MyLogi",person.toString());
            }
            Log.d("MyLog",response.body().string());
        }
    }
}
